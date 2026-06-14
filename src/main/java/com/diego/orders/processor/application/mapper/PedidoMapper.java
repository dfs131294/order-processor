package com.diego.orders.processor.application.mapper;

import com.diego.orders.processor.application.dto.PedidoDTO;
import com.diego.orders.processor.application.util.DateUtil;
import com.diego.orders.processor.domain.model.OrderStatus;
import com.diego.orders.processor.domain.model.Pedido;

import java.util.ArrayList;
import java.util.UUID;

public class PedidoMapper {

    public static Pedido toDomain(PedidoDTO pedidoDTO) {
        return Pedido.builder()
                .id(UUID.randomUUID())
                .numeroPedido(pedidoDTO.getNumeroPedido())
                .clienteId(pedidoDTO.getClienteId())
                .estado(OrderStatus.valueOf(pedidoDTO.getEstado()))
                .zonaId(pedidoDTO.getZonaEntrega())
                .fechaEntrega(DateUtil.parseDate(pedidoDTO.getFechaEntrega()))
                .requiereRefrigeracion(Boolean.parseBoolean(pedidoDTO.getRequiereRefrigeracion()))
                .posicion(pedidoDTO.getPosicion())
                .build();
    }
}
