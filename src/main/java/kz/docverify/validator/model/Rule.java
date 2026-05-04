package kz.docverify.validator.model;

import lombok.Data;

import java.util.Map;

@Data
public class Rule {
    private String id;
    private String name;
    private RuleType type;
    private Severity severity;
    private Map<String, Object> params;
}
