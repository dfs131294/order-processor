package com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.repository;

import com.diego.orders.processor.domain.model.Cliente;
import com.diego.orders.processor.domain.port.out.ClienteRepositoryPort;
import com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.entity.ClienteEntity;
import com.diego.orders.processor.infrastructure.adapters.out.persistance.mapper.ClienteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository repository;

    @Override
    public Cliente save(Cliente cliente) {
        ClienteEntity saved = repository.save(ClienteMapper.toEntity(cliente));
        return ClienteMapper.toDomain(saved);
    }

    @Override
    public Optional<Cliente> findById(String id) {
        return repository.findById(id)
                .map(ClienteMapper::toDomain);
    }
}
