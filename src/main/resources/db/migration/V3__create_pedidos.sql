CREATE TABLE pedidos (
    id UUID PRIMARY KEY,

    numero_pedido VARCHAR(100) NOT NULL,

    cliente_id VARCHAR(50) NOT NULL,

    zona_id VARCHAR(50) NOT NULL,

    fecha_entrega DATE NOT NULL,

    estado VARCHAR(20) NOT NULL,

    requiere_refrigeracion BOOLEAN NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_pedidos_numero_pedido
        UNIQUE (numero_pedido),

    CONSTRAINT chk_pedidos_estado
        CHECK (
            estado IN (
                'PENDIENTE',
                'CONFIRMADO',
                'ENTREGADO'
            )
        )
);

CREATE INDEX idx_pedidos_estado_fecha_entrega
    ON pedidos (estado, fecha_entrega);