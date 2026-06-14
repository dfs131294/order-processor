package com.diego.orders.processor.application.dto;

import lombok.Data;

@Data
public class PedidoDTO {

    private String numeroPedido;
    private String clienteId;
    private String fechaEntrega;
    private String estado;
    private String zonaEntrega;
    private String requiereRefrigeracion;
    private int posicion;
}
