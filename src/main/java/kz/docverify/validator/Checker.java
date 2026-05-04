package kz.docverify.validator;

import kz.docverify.parser.model.ParsedDocument;
import kz.docverify.validator.model.Issue;
import kz.docverify.validator.model.Rule;
import kz.docverify.validator.model.RuleType;

import java.util.stream.Stream;

public interface Checker {
    RuleType getSupportedType();
    Stream<Issue> check(ParsedDocument document, Rule rule);
}
