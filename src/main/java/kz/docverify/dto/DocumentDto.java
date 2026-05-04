package kz.docverify.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class DocumentDto {
    private UUID id;
    private UUID ownerId;
    private String fileName;
    private String fileType;
    private String storageKey;
    private String status;
    private Instant uploadedAt;
}
