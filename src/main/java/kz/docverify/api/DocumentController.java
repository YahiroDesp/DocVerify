package kz.docverify.api;

import kz.docverify.dto.DocumentDto;
import kz.docverify.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<DocumentDto>> upload(
            @RequestPart("file") FilePart file,
            @RequestPart("userId") String userId) {
        return documentService.uploadAndQueue(file, userId)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto));
    }

    @GetMapping("/{id}/status")
    public Mono<DocumentDto> getStatus(@PathVariable UUID id) {
        return documentService.getById(id);
    }

    @GetMapping("/my")
    public Flux<DocumentDto> getMyDocuments(@RequestParam UUID userId) {
        return documentService.getByOwner(userId);
    }
}
