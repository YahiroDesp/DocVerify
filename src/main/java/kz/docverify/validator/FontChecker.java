package kz.docverify.validator;

import kz.docverify.parser.model.ParsedDocument;
import kz.docverify.validator.model.Issue;
import kz.docverify.validator.model.Rule;
import kz.docverify.validator.model.RuleType;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class FontChecker implements Checker {

    @Override
    public RuleType getSupportedType() {
        return RuleType.FONT_CHECK;
    }

    @Override
    public Stream<Issue> check(ParsedDocument document, Rule rule) {
        String expectedFont = rule.getParams().get("fontName").toString();
        Object fontSizeObj = rule.getParams().get("fontSize");
        double expectedSize = fontSizeObj != null ? Double.parseDouble(fontSizeObj.toString()) : 0;

        return document.getParagraphs().stream()
                .filter(p -> p.getText() != null && !p.getText().isBlank())
                .flatMap(p -> {
                    Stream.Builder<Issue> issues = Stream.builder();
                    if (p.getFontName() != null && !expectedFont.equals(p.getFontName())) {
                        issues.add(Issue.error(rule.getId(),
                                "Неверный шрифт: " + p.getFontName() + ", ожидается: " + expectedFont,
                                p.getPageNumber(), p.getLineNumber()));
                    }
                    if (expectedSize > 0 && p.getFontSize() != null && p.getFontSize() != expectedSize) {
                        issues.add(Issue.warning(rule.getId(),
                                "Неверный размер шрифта: " + p.getFontSize() + ", ожидается: " + expectedSize,
                                p.getPageNumber(), p.getLineNumber()));
                    }
                    return issues.build();
                });
    }
}
