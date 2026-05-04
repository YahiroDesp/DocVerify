package kz.docverify.validator;

import kz.docverify.parser.model.ParsedDocument;
import kz.docverify.validator.model.Issue;
import kz.docverify.validator.model.Rule;
import kz.docverify.validator.model.RuleType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class MetadataChecker implements Checker {

    @Override
    public RuleType getSupportedType() {
        return RuleType.METADATA_CHECK;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Stream<Issue> check(ParsedDocument document, Rule rule) {
        List<String> required = (List<String>) rule.getParams().get("required");
        if (required == null || required.isEmpty()) return Stream.empty();

        return required.stream()
                .filter(key -> !document.getMetadata().containsKey(key) ||
                        document.getMetadata().get(key).isBlank())
                .map(key -> Issue.warning(rule.getId(),
                        "Отсутствует метаданные: " + key, null, null));
    }
}
