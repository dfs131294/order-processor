package com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.repository;

import com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.entity.IdempotencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IdempotencyJpaRepository extends JpaRepository<IdempotencyEntity, UUID> {

    Optional<IdempotencyEntity> findByIdempotencyKeyAndFileHash(String key, String hash);
}
