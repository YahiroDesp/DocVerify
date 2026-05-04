package kz.docverify.validator;

import kz.docverify.parser.model.ParsedDocument;
import kz.docverify.validator.model.Issue;
import kz.docverify.validator.model.Rule;
import kz.docverify.validator.model.Severity;
import kz.docverify.validator.model.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RuleEngineService {

    private final List<Checker> checkers;

    public ValidationResult validate(ParsedDocument doc, List<Rule> rules) {
        List<Issue> issues = rules.stream()
                .flatMap(rule -> findChecker(rule.getType())
                        .map(checker -> checker.check(doc, rule))
                        .orElse(Stream.empty()))
                .collect(Collectors.toList());

        double score = calculateScore(issues, rules.size());
        return new ValidationResult(score, issues);
    }

    private Optional<Checker> findChecker(kz.docverify.validator.model.RuleType type) {
        return checkers.stream()
                .filter(c -> c.getSupportedType() == type)
                .findFirst();
    }

    private double calculateScore(List<Issue> issues, int totalRules) {
        if (totalRules == 0) return 100.0;
        long errors = issues.stream().filter(i -> i.getSeverity() == Severity.ERROR).count();
        long warnings = issues.stream().filter(i -> i.getSeverity() == Severity.WARNING).count();
        double penalty = (errors * 2.0 + warnings * 0.5) / totalRules * 100;
        return Math.max(0, 100 - penalty);
    }
}
