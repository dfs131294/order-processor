package com.diego.orders.processor.application.usecase;

import com.diego.orders.processor.application.dto.OrderErrorDetailDTO;
import com.diego.orders.processor.application.dto.PedidoDTO;
import com.diego.orders.processor.application.dto.ResponseDTO;
import com.diego.orders.processor.application.usecase.OrderProcessUseCase;
import com.diego.orders.processor.domain.model.Pedido;
import com.diego.orders.processor.domain.port.out.PedidoRepositoryPort;
import com.diego.orders.processor.domain.service.PedidoDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderProcessUseCaseTest {

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @Mock
    private PedidoDomainService pedidoDomainService;

    @InjectMocks
    private OrderProcessUseCase useCase;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(useCase, "batchSize", 100);
    }

    @Test
    void shouldSaveValidOrders() {
        PedidoDTO pedido = validPedido();

        ResponseDTO response = useCase.process(List.of(pedido));

        verify(pedidoDomainService)
                .validatePedido(any(Pedido.class));

        verify(pedidoRepositoryPort).saveAll(anyList());

        assertEquals(response.getGuardados(), 1);
        assertEquals(response.getConError(), 0);
        assertEquals(response.getTotalProcesados(), 1);
    }

    @Test
    void shouldReturnInputValidationErrors() {
        PedidoDTO pedido = PedidoDTO.builder()
                .posicion(1)
                .numeroPedido(null)
                .clienteId(null)
                .fechaEntrega("abc")
                .estado("INVALID")
                .zonaEntrega(null)
                .requiereRefrigeracion("xyz")
                .build();

        ResponseDTO response = useCase.process(List.of(pedido));

        verifyNoInteractions(pedidoRepositoryPort);

        assertEquals(response.getGuardados(), 0);
        assertEquals(response.getConError(), 1);

        OrderErrorDetailDTO error = response.getDetalleErrores().get(0);

        assertTrue(error.getMotivo().contains("NUMERO_DE_PEDIDO_INVALIDO") &&
                error.getMotivo().contains("ESTADO_INVALIDO") &&
                error.getMotivo().contains("REQUIERE_REFRIGERACION_INVALIDO") &&
                error.getMotivo().contains("FECHA_INVALIDA") &&
                error.getMotivo().contains("CLIENTE_INVALIDO") &&
                error.getMotivo().contains("ZONA_INVALIDA"));
    }

    @Test
    void shouldDetectDuplicateOrdersInFile() {
        PedidoDTO p1 = validPedido();

        PedidoDTO p2 = validPedido();
        p2.setPosicion(2);

        ResponseDTO response =
                useCase.process(List.of(p1, p2));

        verifyNoInteractions(pedidoRepositoryPort);

        assertEquals(response.getConError(), 2);

        assertTrue(
                response.getDetalleErrores()
                        .stream()
                        .anyMatch(de -> de.getMotivo().contains("DUPLICADO_EN_ARCHIVO"))
        );
    }

    @Test
    void shouldReturnDomainValidationErrors() {
        doAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);

            pedido.getErrores()
                    .add("CLIENTE_NO_EXISTE");

            return null;
        }).when(pedidoDomainService)
                .validatePedido(any(Pedido.class));

        ResponseDTO response =
                useCase.process(List.of(validPedido()));

        verify(pedidoRepositoryPort, never())
                .saveAll(anyList());

        assertEquals(response.getGuardados(), 0);
        assertEquals(response.getConError(), 1);
    }

    @Test
    void shouldSaveOnlyValidOrders() {
        PedidoDTO valid = validPedido();

        PedidoDTO invalid = PedidoDTO.builder()
                .posicion(2)
                .numeroPedido(null)
                .build();

        ResponseDTO response =
                useCase.process(List.of(valid, invalid));

        verify(pedidoRepositoryPort)
                .saveAll(argThat(list -> list.size() == 1));

        assertEquals(response.getGuardados(), 1);
        assertEquals(response.getConError(), 1);
        assertEquals(response.getTotalProcesados(), 2);
    }

    @Test
    void shouldSortErrorsByLineNumber() {
        PedidoDTO p2 = PedidoDTO.builder()
                .posicion(2)
                .numeroPedido(null)
                .build();

        PedidoDTO p1 = PedidoDTO.builder()
                .posicion(1)
                .numeroPedido(null)
                .build();

        ResponseDTO response =
                useCase.process(List.of(p2, p1));

        assertEquals(response.getDetalleErrores().get(0).getNumeroLinea(), 1);
        assertEquals(response.getDetalleErrores().get(1).getNumeroLinea(), 2);
    }

    private PedidoDTO validPedido() {
        return PedidoDTO.builder()
                .posicion(1)
                .numeroPedido("ORD-1")
                .clienteId("CLIENTE-1")
                .fechaEntrega("2025-08-10")
                .estado("PENDIENTE")
                .zonaEntrega("LIMA")
                .requiereRefrigeracion("true")
                .build();
    }
}
