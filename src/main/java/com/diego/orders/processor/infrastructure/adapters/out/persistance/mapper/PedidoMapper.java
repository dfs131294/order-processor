package com.diego.orders.processor.infrastructure.adapters.out.persistance.mapper;

import com.diego.orders.processor.domain.model.OrderStatus;
import com.diego.orders.processor.domain.model.Pedido;
import com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.entity.PedidoEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PedidoMapper {

    public static Pedido toDomain(PedidoEntity e) {
        return new Pedido(
                e.getId(),
                e.getNumeroPedido(),
                e.getClienteId(),
                e.getZonaId(),
                e.getFechaEntrega(),
                OrderStatus.valueOf(e.getEstado()),
                e.isRequiereRefrigeracion(),
                0,
                new ArrayList<>()
        );
    }

    public static PedidoEntity toEntity(Pedido p) {
        PedidoEntity e = new PedidoEntity();

        e.setId(p.getId());
        e.setNumeroPedido(p.getNumeroPedido());
        e.setClienteId(p.getClienteId());
        e.setZonaId(p.getZonaId());
        e.setFechaEntrega(p.getFechaEntrega());
        e.setEstado(p.getEstado().name());
        e.setRequiereRefrigeracion(p.isRequiereRefrigeracion());
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());

        return e;
    }
}
