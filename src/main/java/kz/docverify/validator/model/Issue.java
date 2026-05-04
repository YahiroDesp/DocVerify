package kz.docverify.validator.model;

import lombok.Data;

@Data
public class Issue {
    private String ruleId;
    private Severity severity;
    private String description;
    private Integer pageNumber;
    private Integer lineNumber;

    public static Issue error(String ruleId, String description, Integer page, Integer line) {
        Issue issue = new Issue();
        issue.setRuleId(ruleId);
        issue.setSeverity(Severity.ERROR);
        issue.setDescription(description);
        issue.setPageNumber(page);
        issue.setLineNumber(line);
        return issue;
    }

    public static Issue warning(String ruleId, String description, Integer page, Integer line) {
        Issue issue = new Issue();
        issue.setRuleId(ruleId);
        issue.setSeverity(Severity.WARNING);
        issue.setDescription(description);
        issue.setPageNumber(page);
        issue.setLineNumber(line);
        return issue;
    }
}
