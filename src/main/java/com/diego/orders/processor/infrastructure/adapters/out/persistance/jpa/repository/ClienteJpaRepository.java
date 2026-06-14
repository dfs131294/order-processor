package com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.repository;

import com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, String> {
}
