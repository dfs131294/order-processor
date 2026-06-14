package com.diego.orders.processor.domain.service;

import com.diego.orders.processor.domain.model.Cliente;
import com.diego.orders.processor.domain.model.Pedido;
import com.diego.orders.processor.domain.model.Zona;
import com.diego.orders.processor.domain.port.out.ClienteRepositoryPort;
import com.diego.orders.processor.domain.port.out.PedidoRepositoryPort;
import com.diego.orders.processor.domain.port.out.ZonaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoDomainServiceTest {

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private ZonaRepositoryPort zonaRepositoryPort;

    @InjectMocks
    private PedidoDomainService service;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = Pedido.builder()
                .numeroPedido("ORD-1")
                .clienteId("CLIENTE-1")
                .zonaId("ZONA-1")
                .fechaEntrega(LocalDate.now().plusDays(1))
                .requiereRefrigeracion(false)
                .build();
    }

    @Test
    void shouldHaveNoErrorsWhenPedidoIsValid() {

        Cliente cliente = Cliente.builder().id("CLIENTE-1").build();

        Zona zona = Zona.builder()
                .id("ZONA-1")
                .soporteRefrigeracion(true)
                .build();

        when(pedidoRepositoryPort.findByNumeroPedido("ORD-1"))
                .thenReturn(Optional.empty());

        when(clienteRepositoryPort.findById("CLIENTE-1"))
                .thenReturn(Optional.of(cliente));

        when(zonaRepositoryPort.findById("ZONA-1"))
                .thenReturn(Optional.of(zona));

        service.validatePedido(pedido);

        assertTrue(pedido.getErrores().isEmpty());
    }

    @Test
    void shouldAddDuplicateError() {

        when(pedidoRepositoryPort.findByNumeroPedido("ORD-1"))
                .thenReturn(Optional.of(mock(Pedido.class)));

        when(clienteRepositoryPort.findById(anyString()))
                .thenReturn(Optional.of(mock(Cliente.class)));

        when(zonaRepositoryPort.findById(anyString()))
                .thenReturn(Optional.of(mock(Zona.class)));

        service.validatePedido(pedido);

        assertTrue(pedido.getErrores().stream().anyMatch(e -> e.contains("DUPLICADO")));
    }

    @Test
    void shouldAddClientNotFoundError() {

        when(pedidoRepositoryPort.findByNumeroPedido(anyString()))
                .thenReturn(Optional.empty());

        when(clienteRepositoryPort.findById(anyString()))
                .thenReturn(Optional.empty());

        when(zonaRepositoryPort.findById(anyString()))
                .thenReturn(Optional.of(mock(Zona.class)));

        service.validatePedido(pedido);

        assertTrue(pedido.getErrores().stream().anyMatch(e -> e.contains("CLIENTE_NO_ENCONTRADO")));
    }

    @Test
    void shouldAddInvalidDateError() {

        pedido.setFechaEntrega(LocalDate.now().minusDays(1));

        when(pedidoRepositoryPort.findByNumeroPedido(anyString()))
                .thenReturn(Optional.empty());

        when(clienteRepositoryPort.findById(anyString()))
                .thenReturn(Optional.of(mock(Cliente.class)));

        when(zonaRepositoryPort.findById(anyString()))
                .thenReturn(Optional.of(mock(Zona.class)));

        service.validatePedido(pedido);

        assertTrue(pedido.getErrores().stream().anyMatch(e -> e.contains("FECHA_INVALIDA")));
    }

    @Test
    void shouldAddInvalidZoneError() {

        when(pedidoRepositoryPort.findByNumeroPedido(anyString()))
                .thenReturn(Optional.empty());

        when(clienteRepositoryPort.findById(anyString()))
                .thenReturn(Optional.of(mock(Cliente.class)));

        when(zonaRepositoryPort.findById(anyString()))
                .thenReturn(Optional.empty());

        service.validatePedido(pedido);

        assertTrue(pedido.getErrores().stream().anyMatch(e -> e.contains("ZONA_INVALIDA")));
    }

    @Test
    void shouldAddColdChainError() {

        pedido.setRequiereRefrigeracion(true);

        Zona zona = Zona.builder()
                .id("ZONA-1")
                .soporteRefrigeracion(false)
                .build();

        when(pedidoRepositoryPort.findByNumeroPedido(anyString()))
                .thenReturn(Optional.empty());

        when(clienteRepositoryPort.findById(anyString()))
                .thenReturn(Optional.of(mock(Cliente.class)));

        when(zonaRepositoryPort.findById(anyString()))
                .thenReturn(Optional.of(zona));

        service.validatePedido(pedido);

        assertTrue(pedido.getErrores().stream().anyMatch(e -> e.contains("CADENA_FRIO_NO_SOPORTADA")));
    }

    @Test
    void shouldCollectMultipleErrors() {

        pedido.setFechaEntrega(LocalDate.now().minusDays(1));
        pedido.setRequiereRefrigeracion(true);

        Zona zona = Zona.builder()
                .id("ZONA-1")
                .soporteRefrigeracion(false)
                .build();

        when(pedidoRepositoryPort.findByNumeroPedido(anyString()))
                .thenReturn(Optional.of(mock(Pedido.class)));

        when(clienteRepositoryPort.findById(anyString()))
                .thenReturn(Optional.empty());

        when(zonaRepositoryPort.findById(anyString()))
                .thenReturn(Optional.of(zona));

        service.validatePedido(pedido);

        assertTrue(pedido.getErrores().stream().anyMatch(e -> e.contains("CLIENTE_NO_ENCONTRADO")
                || e.contains("DUPLICADO")
                || e.contains("FECHA_INVALIDA")
                || e.contains("CADENA_FRIO_NO_SOPORTADA")
        ));
    }
}
