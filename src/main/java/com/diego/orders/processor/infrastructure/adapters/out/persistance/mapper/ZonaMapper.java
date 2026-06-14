package com.diego.orders.processor.infrastructure.adapters.out.persistance.mapper;

import com.diego.orders.processor.domain.model.Zona;
import com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.entity.ZonaEntity;

public class ZonaMapper {

    public static Zona toDomain(ZonaEntity zonaEntity) {
        return new Zona(
                zonaEntity.getId(),
                zonaEntity.isSoporteRefrigeracion()
        );
    }

    public static ZonaEntity toEntity(Zona zona) {
        ZonaEntity zonaEntity = new ZonaEntity();

        zonaEntity.setId(zona.getId());
        zonaEntity.setSoporteRefrigeracion(zona.isSoporteRefrigeracion());

        return zonaEntity;
    }
}
