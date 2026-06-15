package com.diego.orders.processor.domain.service;

import com.diego.orders.processor.domain.model.Cliente;
import com.diego.orders.processor.domain.model.Pedido;
import com.diego.orders.processor.domain.port.out.ClienteRepositoryPort;
import com.diego.orders.processor.domain.port.out.PedidoRepositoryPort;
import com.diego.orders.processor.domain.port.out.ZonaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoDomainService {

    private static final ZoneId LIMA_ZONE = ZoneId.of("America/Lima");

    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ZonaRepositoryPort zonaRepositoryPort;

    public void validatePedido(Pedido order) {
        List<String> errors = new ArrayList<>();

        pedidoRepositoryPort.findByNumeroPedido(order.getNumeroPedido())
                .ifPresent(__ -> errors.add("DUPLICADO"));

        Optional<Cliente> cliente = clienteRepositoryPort.findByIdAndActivoTrue(order.getClienteId());
        if (cliente.isEmpty()) {
            errors.add("CLIENTE_NO_ENCONTRADO");
        }

        if (!validarFechaEntrega(order.getFechaEntrega())) {
            errors.add("FECHA_INVALIDA");
        }

        zonaRepositoryPort.findById(order.getZonaId())
                .ifPresentOrElse(
                        zona -> {
                            if (order.isRequiereRefrigeracion() && !zona.isSoporteRefrigeracion()) {
                                errors.add("CADENA_FRIO_NO_SOPORTADA");
                            }
                        },
                        () -> errors.add("ZONA_INVALIDA")
                );

        order.setErrores(errors);
    }

    private boolean validarFechaEntrega(LocalDate fechaEntrega) {
        LocalDate now = LocalDate.now(LIMA_ZONE);
        return fechaEntrega.isAfter(now);
    }
}
