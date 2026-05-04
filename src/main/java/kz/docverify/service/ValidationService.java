package kz.docverify.service;

import kz.docverify.domain.Document;
import kz.docverify.exception.DocumentNotFoundException;
import kz.docverify.parser.DocxParser;
import kz.docverify.parser.PdfParser;
import kz.docverify.parser.model.ParsedDocument;
import kz.docverify.repository.DocumentRepository;
import kz.docverify.repository.RuleTemplateRepository;
import kz.docverify.validator.RuleEngineService;
import kz.docverify.validator.model.Rule;
import kz.docverify.validator.model.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService {

    private final StorageService storageService;
    private final DocxParser docxParser;
    private final PdfParser pdfParser;
    private final RuleEngineService ruleEngineService;
    private final RuleTemplateRepository ruleTemplateRepository;
    private final DocumentRepository documentRepository;

    public ValidationResult validate(UUID documentId, String storageKey, String fileType) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));

        try (InputStream stream = storageService.downloadFile(storageKey)) {
            ParsedDocument parsed = parseDocument(stream, fileType);
            parsed.setFileName(document.getFileName());
            parsed.setFileType(fileType);

            List<Rule> rules = loadDefaultRules();
            return ruleEngineService.validate(parsed, rules);
        } catch (IOException e) {
            throw new RuntimeException("Validation failed for document: " + documentId, e);
        }
    }

    private ParsedDocument parseDocument(InputStream stream, String fileType) throws IOException {
        return switch (fileType.toUpperCase()) {
            case "PDF" -> pdfParser.parse(stream);
            default -> docxParser.parse(stream);
        };
    }

    private List<Rule> loadDefaultRules() {
        return ruleTemplateRepository.findByStandard("ГОСТ 7.32-2017")
                .map(t -> parseRules(t.getRulesYaml()))
                .orElse(List.of());
    }

    private List<Rule> parseRules(String yaml) {
        return List.of();
    }
}
