package com.diego.orders.processor.domain.port.out;

import com.diego.orders.processor.domain.model.Zona;

import java.util.Optional;

public interface ZonaRepositoryPort {

    Zona save(Zona zona);

    Optional<Zona> findById(String id);
}
