package com.diego.orders.processor.application.adapter.in;

import com.diego.orders.processor.application.dto.PedidoDTO;
import com.diego.orders.processor.application.dto.ResponseDTO;
import com.diego.orders.processor.application.exception.IdempotencyException;
import com.diego.orders.processor.application.model.Idempotency;
import com.diego.orders.processor.application.port.in.FileParser;
import com.diego.orders.processor.application.port.out.IdempotencyPort;
import com.diego.orders.processor.application.usecase.OrderProcessUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderProcessControllerTest {

    @Mock
    private FileParser<PedidoDTO> csvParser;

    @Mock
    private OrderProcessUseCase orderProcessUseCase;

    @Mock
    private IdempotencyPort idempotencyPort;

    @InjectMocks
    private OrderProcessController controller;

    @Test
    void shouldProcessFileAndSaveIdempotency() throws Exception {

        MockMultipartFile file = csvFile();

        List<PedidoDTO> pedidos = List.of(PedidoDTO.builder().build());

        ResponseDTO response =
                ResponseDTO.builder()
                        .guardados(1)
                        .build();

        when(idempotencyPort.find(anyString(), anyString()))
                .thenReturn(Optional.empty());

        when(csvParser.parse(any(InputStream.class)))
                .thenReturn(pedidos);

        when(orderProcessUseCase.process(pedidos))
                .thenReturn(response);

        ResponseEntity<ResponseDTO> result =
                controller.load(
                        file,
                        "IDEMP-1");

        assertEquals(result.getStatusCode().value(), 200);
        assertEquals(result.getBody(), response);

        verify(csvParser)
                .parse(any(InputStream.class));

        verify(orderProcessUseCase)
                .process(pedidos);

        verify(idempotencyPort)
                .save(any(Idempotency.class));
    }

    @Test
    void shouldThrowIdempotencyException() {

        MockMultipartFile file = csvFile();

        when(idempotencyPort.find(anyString(), anyString()))
                .thenReturn(
                        Optional.of(
                                Idempotency.builder()
                                        .build()));

        assertThrows(
                IdempotencyException.class,
                () -> controller.load(file, "IDEMP-1"));

        verifyNoInteractions(
                csvParser,
                orderProcessUseCase);

        verify(idempotencyPort, never())
                .save(any());
    }

    @Test
    void shouldPropagateParserException() throws Exception {

        MockMultipartFile file = csvFile();

        when(idempotencyPort.find(anyString(), anyString()))
                .thenReturn(Optional.empty());

        when(csvParser.parse(any(InputStream.class)))
                .thenThrow(
                        new RuntimeException("CSV error"));

        assertThrows(
                RuntimeException.class,
                () -> controller.load(file, "IDEMP-1"));

        verify(idempotencyPort, never())
                .save(any());
    }

    private MockMultipartFile csvFile() {
        return new MockMultipartFile(
                "file",
                "orders.csv",
                "text/csv",
                """
                        numeroPedido,clienteId
                        ORD-1,CLIENTE-1
                        """.getBytes()
        );
    }
}
