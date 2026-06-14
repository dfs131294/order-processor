package com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "zonas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ZonaEntity {

    @Id
    private String id;

    @Column(name = "soporte_refrigeracion")
    private boolean soporteRefrigeracion;
}