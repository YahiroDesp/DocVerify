package kz.docverify.parser.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ParsedDocument {
    private List<Heading> headings;
    private List<ParsedParagraph> paragraphs;
    private Map<String, String> styles;
    private Map<String, String> metadata;
    private PageMargins margins;
    private String fileName;
    private String fileType;
}
