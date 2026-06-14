package com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.repository;

import com.diego.orders.processor.domain.model.Pedido;
import com.diego.orders.processor.domain.port.out.PedidoRepositoryPort;
import com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.entity.PedidoEntity;
import com.diego.orders.processor.infrastructure.adapters.out.persistance.mapper.PedidoMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PedidoRepositoryAdapter implements PedidoRepositoryPort {

    private final PedidoJpaRepository repository;

    @Override
    public Pedido save(Pedido pedido) {
        PedidoEntity saved = repository.save(PedidoMapper.toEntity(pedido));
        return PedidoMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void saveAll(List<Pedido> pedidos) {
        List<PedidoEntity> pedidoEntities = pedidos.stream()
                .map(PedidoMapper::toEntity)
                .toList();
        repository.saveAll(pedidoEntities);
    }

    @Override
    public Optional<Pedido> findById(UUID id) {
        return repository.findById(id)
                .map(PedidoMapper::toDomain);
    }

    @Override
    public Optional<Pedido> findByNumeroPedido(String numeroPedido) {
        return repository.findByNumeroPedido(numeroPedido)
                .map(PedidoMapper::toDomain);
    }
}
