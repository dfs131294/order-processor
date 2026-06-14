package com.diego.orders.processor.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class ErrorResponseDTO {

    private String code;
    private String message;
    private List<String> details[];
    private String correlationId;
}
