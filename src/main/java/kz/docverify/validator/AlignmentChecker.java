package kz.docverify.validator;

import kz.docverify.parser.model.ParsedDocument;
import kz.docverify.validator.model.Issue;
import kz.docverify.validator.model.Rule;
import kz.docverify.validator.model.RuleType;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class AlignmentChecker implements Checker {

    @Override
    public RuleType getSupportedType() {
        return RuleType.ALIGNMENT_CHECK;
    }

    @Override
    public Stream<Issue> check(ParsedDocument document, Rule rule) {
        String expected = rule.getParams().getOrDefault("expected", "BOTH").toString();

        return document.getParagraphs().stream()
                .filter(p -> p.getText() != null && !p.getText().isBlank())
                .filter(p -> p.getStyle() == null || !p.getStyle().startsWith("Heading"))
                .filter(p -> p.getAlignment() != null && !p.getAlignment().equals(expected))
                .map(p -> Issue.warning(rule.getId(),
                        "Выравнивание параграфа: " + translateAlignment(p.getAlignment())
                                + ", ожидается: " + translateAlignment(expected),
                        p.getPageNumber(), p.getLineNumber()));
    }

    private String translateAlignment(String alignment) {
        return switch (alignment) {
            case "BOTH" -> "по ширине";
            case "LEFT" -> "по левому краю";
            case "RIGHT" -> "по правому краю";
            case "CENTER" -> "по центру";
            default -> alignment;
        };
    }
}
