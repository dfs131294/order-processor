package com.diego.orders.processor.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class Idempotency {

    private UUID id;
    private String idempotencyKey;
    private String fileHash;
}
