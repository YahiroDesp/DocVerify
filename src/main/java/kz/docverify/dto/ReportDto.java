package kz.docverify.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class ReportDto {
    private UUID documentId;
    private String fileName;
    private String standard;
    private BigDecimal score;
    private Instant checkedAt;
    private List<IssueDto> issues;

    @Data
    public static class IssueDto {
        private String ruleId;
        private String severity;
        private String description;
        private Integer page;
        private Integer line;
    }
}
