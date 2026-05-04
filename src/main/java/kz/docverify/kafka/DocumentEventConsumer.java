package kz.docverify.kafka;

import kz.docverify.service.ReportService;
import kz.docverify.service.ValidationService;
import kz.docverify.validator.model.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentEventConsumer {

    private final ValidationService validationService;
    private final ReportService reportService;
    private final DocumentEventProducer producer;

    @KafkaListener(topics = "document.uploaded", groupId = "docverify-group")
    public void handleDocumentUploaded(DocumentEvent event) {
        log.info("Received upload event for document: {}", event.getDocumentId());
        ValidationResult result = validationService.validate(event.getDocumentId(), event.getStorageKey(), event.getFileType());
        reportService.saveReport(event.getDocumentId(), result);
        producer.sendValidatedEvent(event.getDocumentId(), result.getScore());
    }
}
