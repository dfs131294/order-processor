package com.diego.orders.processor.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderErrorDetailDTO {

    private int numeroLinea;
    private String motivo;
}
