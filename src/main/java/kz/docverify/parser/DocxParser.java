package kz.docverify.parser;

import kz.docverify.parser.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DocxParser {

    public ParsedDocument parse(InputStream inputStream) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(inputStream)) {
            ParsedDocument result = new ParsedDocument();
            result.setHeadings(extractHeadings(doc));
            result.setParagraphs(extractParagraphs(doc));
            result.setStyles(extractStyles(doc));
            result.setMetadata(extractMetadata(doc));
            result.setMargins(extractMargins(doc));
            return result;
        }
    }

    private List<Heading> extractHeadings(XWPFDocument doc) {
        return doc.getParagraphs().stream()
                .filter(p -> p.getStyle() != null && p.getStyle().startsWith("Heading"))
                .map(p -> new Heading(p.getStyle(), p.getText()))
                .collect(Collectors.toList());
    }

    private List<ParsedParagraph> extractParagraphs(XWPFDocument doc) {
        List<ParsedParagraph> result = new ArrayList<>();
        int lineNumber = 0;
        for (XWPFParagraph p : doc.getParagraphs()) {
            lineNumber++;
            ParsedParagraph pp = new ParsedParagraph();
            pp.setText(p.getText());
            pp.setLineNumber(lineNumber);
            pp.setPageNumber(1);
            if (!p.getRuns().isEmpty()) {
                XWPFRun run = p.getRuns().get(0);
                pp.setFontName(run.getFontName());
                pp.setFontSize(run.getFontSizeAsDouble());
            }
            result.add(pp);
        }
        return result;
    }

    private Map<String, String> extractStyles(XWPFDocument doc) {
        return new HashMap<>();
    }

    private Map<String, String> extractMetadata(XWPFDocument doc) {
        Map<String, String> metadata = new HashMap<>();
        try {
            var props = doc.getPackage().getPackageProperties();
            props.getTitleProperty().ifPresent(v -> metadata.put("title", v));
            props.getCreatorProperty().ifPresent(v -> metadata.put("creator", v));
            props.getDescriptionProperty().ifPresent(v -> metadata.put("description", v));
        } catch (Exception e) {
            log.warn("Could not extract metadata: {}", e.getMessage());
        }
        return metadata;
    }

    private PageMargins extractMargins(XWPFDocument doc) {
        PageMargins margins = new PageMargins();
        var sectPr = doc.getDocument().getBody().getSectPr();
        if (sectPr != null && sectPr.getPgMar() != null) {
            var pgMar = sectPr.getPgMar();
            margins.setLeft(pgMar.getLeft() instanceof Number n ? n.doubleValue() / 567.0 : 0);
            margins.setRight(pgMar.getRight() instanceof Number n ? n.doubleValue() / 567.0 : 0);
            margins.setTop(pgMar.getTop() instanceof Number n ? n.doubleValue() / 567.0 : 0);
            margins.setBottom(pgMar.getBottom() instanceof Number n ? n.doubleValue() / 567.0 : 0);
        }
        return margins;
    }
}
