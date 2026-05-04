package kz.docverify.validator;

import kz.docverify.parser.model.PageMargins;
import kz.docverify.parser.model.ParsedDocument;
import kz.docverify.validator.model.Issue;
import kz.docverify.validator.model.Rule;
import kz.docverify.validator.model.RuleType;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MarginChecker implements Checker {

    private static final double TOLERANCE = 0.5;

    @Override
    public RuleType getSupportedType() {
        return RuleType.MARGIN_CHECK;
    }

    @Override
    public Stream<Issue> check(ParsedDocument document, Rule rule) {
        PageMargins margins = document.getMargins();
        if (margins == null) return Stream.empty();

        Stream.Builder<Issue> issues = Stream.builder();
        checkMargin(rule, issues, "left", margins.getLeft(), parseMm(rule.getParams().get("left")));
        checkMargin(rule, issues, "right", margins.getRight(), parseMm(rule.getParams().get("right")));
        checkMargin(rule, issues, "top", margins.getTop(), parseMm(rule.getParams().get("top")));
        checkMargin(rule, issues, "bottom", margins.getBottom(), parseMm(rule.getParams().get("bottom")));
        return issues.build();
    }

    private void checkMargin(Rule rule, Stream.Builder<Issue> issues, String side, double actual, double expected) {
        if (expected > 0 && Math.abs(actual - expected) > TOLERANCE) {
            issues.add(Issue.error(rule.getId(),
                    String.format("Поле %s: %.1f мм, ожидается: %.1f мм", side, actual, expected),
                    null, null));
        }
    }

    private double parseMm(Object value) {
        if (value == null) return 0;
        String str = value.toString().replaceAll("[^\\d.]", "");
        return str.isEmpty() ? 0 : Double.parseDouble(str);
    }
}
