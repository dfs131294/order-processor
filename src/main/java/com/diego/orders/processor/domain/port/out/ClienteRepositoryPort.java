package com.diego.orders.processor.domain.port.out;

import com.diego.orders.processor.domain.model.Cliente;

import java.util.Optional;

public interface ClienteRepositoryPort {

    Cliente save(Cliente cliente);

    Optional<Cliente> findById(String id);

    Optional<Cliente> findByIdAndActivoTrue(String id);
}
