package com.diego.orders.processor.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
public class Pedido {

    private UUID id;

    private String numeroPedido;

    private String clienteId;

    private String zonaId;

    private LocalDate fechaEntrega;

    private OrderStatus estado;

    private boolean requiereRefrigeracion;

    private int posicion;

    @Builder.Default
    private List<String> errores = new ArrayList<>();
}
