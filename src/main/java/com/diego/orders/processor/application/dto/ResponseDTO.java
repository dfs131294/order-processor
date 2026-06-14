package com.diego.orders.processor.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ResponseDTO {

    private int totalProcesados;
    private int guardados;
    private int conError;
    private List<OrderErrorDetailDTO> detalleErrores;
}
