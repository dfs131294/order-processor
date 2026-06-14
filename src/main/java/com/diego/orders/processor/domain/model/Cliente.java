package com.diego.orders.processor.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class Cliente {

    private String id;

    private boolean activo;
}
