package com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.repository;

import com.diego.orders.processor.domain.model.Zona;
import com.diego.orders.processor.domain.port.out.ZonaRepositoryPort;
import com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.entity.ZonaEntity;
import com.diego.orders.processor.infrastructure.adapters.out.persistance.mapper.ZonaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ZonaRepositoryAdapter implements ZonaRepositoryPort {

    private final ZonaJpaRepository repository;

    @Override
    public Zona save(Zona zona) {
        ZonaEntity saved = repository.save(ZonaMapper.toEntity(zona));
        return ZonaMapper.toDomain(saved);
    }

    @Override
    public Optional<Zona> findById(String id) {
        return repository.findById(id)
                .map(ZonaMapper::toDomain);
    }
}
