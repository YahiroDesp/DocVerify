package kz.docverify.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService {

    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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
                .orElseGet(this::loadRulesFromClasspath);
    }

    private List<Rule> loadRulesFromClasspath() {
        try {
            ClassPathResource resource = new ClassPathResource("rules/gost_7_32.yml");
            String yaml = new String(resource.getInputStream().readAllBytes());
            return parseRules(yaml);
        } catch (Exception e) {
            log.error("Failed to load rules from classpath: {}", e.getMessage());
            return List.of();
        }
    }

    private List<Rule> parseRules(String yaml) {
        try {
            JsonNode root = YAML_MAPPER.readTree(yaml);
            return YAML_MAPPER.convertValue(root.get("rules"), new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Failed to parse rules YAML: {}", e.getMessage());
            return List.of();
        }
    }
}
