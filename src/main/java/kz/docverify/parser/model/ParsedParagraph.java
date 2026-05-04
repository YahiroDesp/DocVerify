package kz.docverify.parser.model;

import lombok.Data;

@Data
public class ParsedParagraph {
    private String text;
    private String fontName;
    private Double fontSize;
    private Integer pageNumber;
    private Integer lineNumber;
}
