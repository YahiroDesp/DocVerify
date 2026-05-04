package kz.docverify.parser;

import kz.docverify.parser.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Component
public class PdfParser {

    public ParsedDocument parse(InputStream inputStream) throws IOException {
        try (PDDocument doc = Loader.loadPDF(inputStream.readAllBytes())) {
            ParsedDocument result = new ParsedDocument();

            PDFTextStripper stripper = new PDFTextStripper();
            String fullText = stripper.getText(doc);

            List<ParsedParagraph> paragraphs = new ArrayList<>();
            String[] lines = fullText.split("\n");
            for (int i = 0; i < lines.length; i++) {
                ParsedParagraph p = new ParsedParagraph();
                p.setText(lines[i]);
                p.setLineNumber(i + 1);
                p.setPageNumber(1);
                paragraphs.add(p);
            }

            result.setParagraphs(paragraphs);
            result.setHeadings(List.of());
            result.setStyles(Map.of());
            result.setMetadata(extractMetadata(doc));
            result.setMargins(new PageMargins());
            return result;
        }
    }

    private Map<String, String> extractMetadata(PDDocument doc) {
        Map<String, String> metadata = new HashMap<>();
        var info = doc.getDocumentInformation();
        if (info.getTitle() != null) metadata.put("title", info.getTitle());
        if (info.getAuthor() != null) metadata.put("author", info.getAuthor());
        return metadata;
    }
}
