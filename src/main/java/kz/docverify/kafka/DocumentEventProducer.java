package kz.docverify.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentEventProducer {

    private final KafkaTemplate<String, DocumentEvent> kafkaTemplate;

    public void sendUploadEvent(UUID documentId, String storageKey, String fileType) {
        DocumentEvent event = new DocumentEvent(documentId, storageKey, fileType, Instant.now());
        kafkaTemplate.send("document.uploaded", documentId.toString(), event);
        log.info("Sent upload event for document: {}", documentId);
    }

    public void sendValidatedEvent(UUID documentId, double score) {
        DocumentEvent event = new DocumentEvent(documentId, null, null, Instant.now());
        kafkaTemplate.send("document.validated", documentId.toString(), event);
        log.info("Sent validated event for document: {}, score: {}", documentId, score);
    }
}
