package com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.repository;

import com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.entity.ZonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZonaJpaRepository extends JpaRepository<ZonaEntity, String> {
}
