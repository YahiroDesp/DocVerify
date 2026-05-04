package kz.docverify.service;

import kz.docverify.domain.Document;
import kz.docverify.domain.ReportIssue;
import kz.docverify.domain.ValidationReport;
import kz.docverify.dto.ReportDto;
import kz.docverify.exception.DocumentNotFoundException;
import kz.docverify.repository.DocumentRepository;
import kz.docverify.repository.ReportRepository;
import kz.docverify.validator.model.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final DocumentRepository documentRepository;

    @Transactional
    public void saveReport(UUID documentId, ValidationResult result) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));

        ValidationReport report = new ValidationReport();
        report.setDocument(document);
        report.setScore(BigDecimal.valueOf(result.getScore()));

        List<ReportIssue> issues = result.getIssues().stream().map(i -> {
            ReportIssue ri = new ReportIssue();
            ri.setReport(report);
            ri.setRuleName(i.getRuleId());
            ri.setDescription(i.getDescription());
            ri.setSeverity(i.getSeverity().name());
            ri.setPageNumber(i.getPageNumber());
            ri.setLineNumber(i.getLineNumber());
            return ri;
        }).collect(Collectors.toList());

        report.setIssues(issues);
        reportRepository.save(report);

        document.setStatus("VALIDATED");
        documentRepository.save(document);
        log.info("Report saved for document: {}, score: {}", documentId, result.getScore());
    }

    public ReportDto getByDocumentId(UUID documentId) {
        ValidationReport report = reportRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new RuntimeException("Report not found for document: " + documentId));

        ReportDto dto = new ReportDto();
        dto.setDocumentId(documentId);
        dto.setFileName(report.getDocument().getFileName());
        dto.setScore(report.getScore());
        dto.setCheckedAt(report.getCreatedAt());

        List<ReportDto.IssueDto> issueDtos = report.getIssues().stream().map(i -> {
            ReportDto.IssueDto d = new ReportDto.IssueDto();
            d.setRuleId(i.getRuleName());
            d.setSeverity(i.getSeverity());
            d.setDescription(i.getDescription());
            d.setPage(i.getPageNumber());
            d.setLine(i.getLineNumber());
            return d;
        }).collect(Collectors.toList());

        dto.setIssues(issueDtos);
        return dto;
    }
}
