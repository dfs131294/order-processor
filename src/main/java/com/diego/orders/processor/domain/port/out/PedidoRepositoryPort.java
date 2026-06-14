package com.diego.orders.processor.domain.port.out;

import com.diego.orders.processor.domain.model.Pedido;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepositoryPort {

    Pedido save(Pedido pedido);

    void saveAll(List<Pedido> pedido);

    Optional<Pedido> findById(UUID id);

    Optional<Pedido> findByNumeroPedido(String numeroPedido);
}
