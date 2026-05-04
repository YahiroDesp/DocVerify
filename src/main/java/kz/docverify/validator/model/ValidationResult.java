package kz.docverify.validator.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ValidationResult {
    private double score;
    private List<Issue> issues;
}
