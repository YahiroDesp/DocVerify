CREATE TABLE documents (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id     UUID REFERENCES users(id),
    file_name    VARCHAR(255) NOT NULL,
    file_type    VARCHAR(20)  NOT NULL CHECK (file_type IN ('DOCX','PDF','MD')),
    storage_key  VARCHAR(512) NOT NULL,
    status       VARCHAR(30)  DEFAULT 'PENDING',
    uploaded_at  TIMESTAMP    DEFAULT NOW()
);
