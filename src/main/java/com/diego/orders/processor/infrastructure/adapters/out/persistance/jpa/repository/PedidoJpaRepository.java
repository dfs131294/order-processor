package com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.repository;

import com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PedidoJpaRepository extends JpaRepository<PedidoEntity, UUID> {

    Optional<PedidoEntity> findByNumeroPedido(String numeroPedido);
}
