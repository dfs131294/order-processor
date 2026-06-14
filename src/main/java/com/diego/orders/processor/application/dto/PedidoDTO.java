package com.diego.orders.processor.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PedidoDTO {

    private String numeroPedido;
    private String clienteId;
    private String fechaEntrega;
    private String estado;
    private String zonaEntrega;
    private String requiereRefrigeracion;
    private int posicion;
}
