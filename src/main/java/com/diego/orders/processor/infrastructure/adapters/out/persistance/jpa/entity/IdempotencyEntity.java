package com.diego.orders.processor.infrastructure.adapters.out.persistance.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cargas_idempotencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyEntity {

    @Id
    private UUID id;

    @Column(name = "idempotency_key")
    private String idempotencyKey;

    @Column(name = "archivo_hash")
    private String fileHash;

    private LocalDateTime createdAt;
}
