CREATE TABLE cargas_idempotencia (
    id UUID PRIMARY KEY,

    idempotency_key VARCHAR(255) NOT NULL,

    archivo_hash VARCHAR(255) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_cargas_idempotencia
        UNIQUE (
            idempotency_key,
            archivo_hash
        )
);