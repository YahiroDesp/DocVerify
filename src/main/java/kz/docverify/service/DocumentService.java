package kz.docverify.service;

import kz.docverify.domain.Document;
import kz.docverify.domain.User;
import kz.docverify.dto.DocumentDto;
import kz.docverify.dto.mapper.DocumentMapper;
import kz.docverify.exception.DocumentNotFoundException;
import kz.docverify.kafka.DocumentEventProducer;
import kz.docverify.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final StorageService storageService;
    private final DocumentEventProducer eventProducer;
    private final DocumentMapper documentMapper;

    public Mono<DocumentDto> uploadAndQueue(FilePart file, String userId) {
        return Mono.fromCallable(() -> {
            String fileName = file.filename();
            String fileType = resolveFileType(fileName);

            Document document = new Document();
            User owner = new User();
            owner.setId(UUID.fromString(userId));
            document.setOwner(owner);
            document.setFileName(fileName);
            document.setFileType(fileType);
            document.setStorageKey("pending");
            document.setStatus("UPLOADING");
            Document saved = documentRepository.save(document);

            PipedInputStream pis = new PipedInputStream();
            PipedOutputStream pos = new PipedOutputStream(pis);

            file.content().subscribeOn(Schedulers.boundedElastic())
                    .doOnTerminate(() -> {
                        try { pos.close(); } catch (IOException ignored) {}
                    })
                    .subscribe(dataBuffer -> {
                        try {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            pos.write(bytes);
                        } catch (IOException e) {
                            log.error("Error writing to pipe", e);
                        }
                    });

            String storageKey = storageService.uploadFile(fileName, pis, -1, resolveContentType(fileType));

            saved.setStorageKey(storageKey);
            saved.setStatus("PENDING");
            documentRepository.save(saved);

            eventProducer.sendUploadEvent(saved.getId(), storageKey, fileType);
            return documentMapper.toDto(saved);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<DocumentDto> getById(UUID id) {
        return Mono.fromCallable(() -> documentRepository.findById(id)
                .map(documentMapper::toDto)
                .orElseThrow(() -> new DocumentNotFoundException(id)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private String resolveFileType(String fileName) {
        if (fileName.endsWith(".pdf")) return "PDF";
        if (fileName.endsWith(".docx")) return "DOCX";
        return "MD";
    }

    private String resolveContentType(String fileType) {
        return switch (fileType) {
            case "PDF" -> "application/pdf";
            case "DOCX" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default -> "text/plain";
        };
    }
}
