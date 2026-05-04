package kz.docverify.validator;

import kz.docverify.parser.model.ParsedDocument;
import kz.docverify.validator.model.Issue;
import kz.docverify.validator.model.Rule;
import kz.docverify.validator.model.RuleType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class HeadingChecker implements Checker {

    @Override
    public RuleType getSupportedType() {
        return RuleType.HEADING_CHECK;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Stream<Issue> check(ParsedDocument document, Rule rule) {
        List<String> required = (List<String>) rule.getParams().get("required");
        if (required == null || required.isEmpty()) {
            return Stream.empty();
        }

        Set<String> presentHeadings = document.getHeadings().stream()
                .map(h -> h.getText().trim())
                .collect(Collectors.toSet());

        return required.stream()
                .filter(req -> presentHeadings.stream().noneMatch(h -> h.equalsIgnoreCase(req)))
                .map(req -> Issue.error(rule.getId(),
                        "Отсутствует обязательный раздел: " + req, null, null));
    }
}
