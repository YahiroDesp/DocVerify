package kz.docverify.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentEvent {
    private UUID documentId;
    private String storageKey;
    private String fileType;
    private Instant occurredAt;
}
