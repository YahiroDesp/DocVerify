package kz.docverify.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "report_issues")
@Getter
@Setter
@NoArgsConstructor
public class ReportIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private ValidationReport report;

    @Column(name = "rule_name")
    private String ruleName;

    @Column(name = "page_number")
    private Integer pageNumber;

    @Column(name = "line_number")
    private Integer lineNumber;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String severity;
}
