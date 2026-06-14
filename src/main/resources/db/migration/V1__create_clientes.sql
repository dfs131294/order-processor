CREATE TABLE clientes (
    id VARCHAR(50) PRIMARY KEY,
    activo BOOLEAN NOT NULL
);

INSERT INTO clientes (id, activo)
VALUES
    ('CLI-123', true),
    ('CLI-234', true),
    ('CLI-457', false),
    ('CLI-999', true);