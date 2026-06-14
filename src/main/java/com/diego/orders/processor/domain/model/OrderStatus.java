package com.diego.orders.processor.domain.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OrderStatus {

    PENDIENTE,
    CONFIRMADO,
    ENTREGADO;

    public static boolean isValid(String value) {
        return Arrays.stream(values())
                .anyMatch(status -> status.name().equals(value));
    }
}
