package com.diego.orders.processor.infrastructure.adapters.out.persistance.mapper;

import com.diego.orders.processor.application.model.Idempotency;
import com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.entity.IdempotencyEntity;

import java.time.LocalDateTime;

public class IdempotencyMapper {

    public static Idempotency toModel(IdempotencyEntity idempotencyEntity) {
        return new Idempotency(
                idempotencyEntity.getId(),
                idempotencyEntity.getIdempotencyKey(),
                idempotencyEntity.getFileHash()
        );
    }

    public static IdempotencyEntity toEntity(Idempotency idempotency) {
        IdempotencyEntity idempotencyEntity = new IdempotencyEntity();

        idempotencyEntity.setId(idempotency.getId());
        idempotencyEntity.setIdempotencyKey(idempotency.getIdempotencyKey());
        idempotencyEntity.setFileHash(idempotency.getFileHash());
        idempotencyEntity.setCreatedAt(LocalDateTime.now());

        return idempotencyEntity;
    }
}
