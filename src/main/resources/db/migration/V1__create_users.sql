CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email       VARCHAR(255) NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    role        VARCHAR(50)  NOT NULL CHECK (role IN ('AUTHOR','CONTROLLER','ADMIN')),
    created_at  TIMESTAMP    DEFAULT NOW()
);
