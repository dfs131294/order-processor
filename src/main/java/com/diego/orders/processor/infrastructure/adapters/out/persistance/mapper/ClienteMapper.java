package com.diego.orders.processor.infrastructure.adapters.out.persistance.mapper;

import com.diego.orders.processor.domain.model.Cliente;
import com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.entity.ClienteEntity;

public class ClienteMapper {

    public static Cliente toDomain(ClienteEntity clienteEntity) {
        return new Cliente(
                clienteEntity.getId(),
                clienteEntity.isActivo()
        );
    }

    public static ClienteEntity toEntity(Cliente cliente) {
        ClienteEntity clienteEntity = new ClienteEntity();

        clienteEntity.setId(cliente.getId());
        clienteEntity.setActivo(cliente.isActivo());

        return clienteEntity;
    }
}
