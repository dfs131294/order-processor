package com.diego.orders.processor.application.port.out;

import com.diego.orders.processor.application.model.Idempotency;

import java.util.Optional;

public interface IdempotencyPort {

    Optional<Idempotency> find(String key, String hash);

    Idempotency save(Idempotency idempotency);
}
