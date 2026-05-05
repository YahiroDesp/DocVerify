package kz.docverify.validator;

import kz.docverify.parser.model.ParsedDocument;
import kz.docverify.validator.model.Issue;
import kz.docverify.validator.model.Rule;
import kz.docverify.validator.model.RuleType;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class LineSpacingChecker implements Checker {

    @Override
    public RuleType getSupportedType() {
        return RuleType.LINE_SPACING_CHECK;
    }

    @Override
    public Stream<Issue> check(ParsedDocument document, Rule rule) {
        double minSpacing = parseDouble(rule.getParams().get("minSpacing"), 1.5);
        double maxSpacing = parseDouble(rule.getParams().get("maxSpacing"), 2.0);

        return document.getParagraphs().stream()
                .filter(p -> p.getText() != null && !p.getText().isBlank())
                .filter(p -> p.getLineSpacing() != null)
                .filter(p -> p.getStyle() == null || !p.getStyle().startsWith("Heading"))
                .filter(p -> p.getLineSpacing() < minSpacing - 0.1 || p.getLineSpacing() > maxSpacing + 0.1)
                .map(p -> Issue.warning(rule.getId(),
                        String.format("Межстрочный интервал: %.2f, ожидается от %.1f до %.1f",
                                p.getLineSpacing(), minSpacing, maxSpacing),
                        p.getPageNumber(), p.getLineNumber()));
    }

    private double parseDouble(Object val, double def) {
        if (val == null) return def;
        try { return Double.parseDouble(val.toString()); } catch (Exception e) { return def; }
    }
}
