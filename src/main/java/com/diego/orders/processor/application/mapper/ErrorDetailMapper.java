package com.diego.orders.processor.application.mapper;

import com.diego.orders.processor.application.dto.OrderErrorDetailDTO;
import com.diego.orders.processor.domain.model.Pedido;

public class ErrorDetailMapper {

    public static OrderErrorDetailDTO toErrorDetail(Pedido pedido) {
        return OrderErrorDetailDTO.builder()
                .numeroLinea(pedido.getPosicion())
                .motivo(String.join(", ", pedido.getErrores()))
                .build();
    }
}
