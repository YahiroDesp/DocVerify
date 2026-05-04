package kz.docverify.parser;

import kz.docverify.parser.model.*;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
@Component
public class OcrParser {

    private final Tesseract tesseract;

    public OcrParser() {
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath("/usr/share/tesseract-ocr/5/tessdata");
        this.tesseract.setLanguage("rus+eng");
    }

    public ParsedDocument parse(InputStream inputStream) throws IOException {
        File tempFile = File.createTempFile("ocr_", ".png");
        try {
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            String text = tesseract.doOCR(tempFile);

            ParsedDocument result = new ParsedDocument();
            List<ParsedParagraph> paragraphs = new ArrayList<>();
            String[] lines = text.split("\n");
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
            result.setMetadata(Map.of());
            result.setMargins(new PageMargins());
            return result;
        } catch (TesseractException e) {
            throw new IOException("OCR failed: " + e.getMessage(), e);
        } finally {
            tempFile.delete();
        }
    }
}
