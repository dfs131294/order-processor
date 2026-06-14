package com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.repository;

import com.diego.orders.processor.application.model.Idempotency;
import com.diego.orders.processor.application.port.out.IdempotencyPort;
import com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.entity.IdempotencyEntity;
import com.diego.orders.processor.infrastructure.adapters.out.persistance.mapper.IdempotencyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class IdempotencyRepositoryAdapter implements IdempotencyPort {

    private final IdempotencyJpaRepository repository;

    @Override
    public Optional<Idempotency> find(String key, String hash) {
        return repository.findByIdempotencyKeyAndFileHash(key, hash)
                .map(IdempotencyMapper::toModel);
    }

    @Override
    public Idempotency save(Idempotency idempotency) {
        IdempotencyEntity saved = repository.save(IdempotencyMapper.toEntity(idempotency));
        return IdempotencyMapper.toModel(saved);
    }
}
