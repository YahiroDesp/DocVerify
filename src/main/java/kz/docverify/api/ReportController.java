package kz.docverify.api;

import kz.docverify.dto.ReportDto;
import kz.docverify.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/{documentId}")
    public Mono<ReportDto> getReport(@PathVariable UUID documentId) {
        return Mono.fromCallable(() -> reportService.getByDocumentId(documentId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping(value = "/{documentId}/export", produces = MediaType.APPLICATION_PDF_VALUE)
    public Mono<ResponseEntity<byte[]>> exportPdf(@PathVariable UUID documentId) {
        return Mono.fromCallable(() -> reportService.getByDocumentId(documentId))
                .subscribeOn(Schedulers.boundedElastic())
                .map(report -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(new byte[0]));
    }
}
