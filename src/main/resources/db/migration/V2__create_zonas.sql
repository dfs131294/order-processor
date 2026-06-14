CREATE TABLE zonas (
    id VARCHAR(50) PRIMARY KEY,
    soporte_refrigeracion BOOLEAN NOT NULL
);

INSERT INTO zonas (id, soporte_refrigeracion)
VALUES
    ('ZONA1', true),
    ('ZONA2', false),
    ('ZONA3', true),
    ('ZONA5', false);