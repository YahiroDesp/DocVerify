CREATE TABLE validation_reports (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id  UUID REFERENCES documents(id),
    score        NUMERIC(5,2),
    summary      JSONB,
    created_at   TIMESTAMP DEFAULT NOW()
);

CREATE TABLE report_issues (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    report_id    UUID REFERENCES validation_reports(id),
    rule_name    VARCHAR(100),
    page_number  INT,
    line_number  INT,
    description  TEXT,
    severity     VARCHAR(20) CHECK (severity IN ('ERROR','WARNING','INFO'))
);
