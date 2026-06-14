package com.diego.orders.processor.application.adapter.in;

import com.diego.orders.processor.application.dto.PedidoDTO;
import com.diego.orders.processor.application.dto.ResponseDTO;
import com.diego.orders.processor.application.model.Idempotency;
import com.diego.orders.processor.application.port.in.FileParser;
import com.diego.orders.processor.application.port.out.IdempotencyPort;
import com.diego.orders.processor.application.usecase.OrderProcessUseCase;
import com.diego.orders.processor.infrastructure.adapters.in.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(OrderProcessController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrderProcessControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileParser<PedidoDTO> csvParser;

    @MockBean
    private OrderProcessUseCase orderProcessUseCase;

    @MockBean
    private IdempotencyPort idempotencyPort;

    @MockBean
    private JwtService jwtService;

    @Test
    void shouldProcessMultipartFile() throws Exception {
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "orders.csv",
                        "text/csv",
                        "content".getBytes());

        when(idempotencyPort.find(anyString(), anyString()))
                .thenReturn(Optional.empty());

        when(csvParser.parse(any(InputStream.class)))
                .thenReturn(List.of());

        when(orderProcessUseCase.process(anyList()))
                .thenReturn(
                        ResponseDTO.builder()
                                .guardados(1)
                                .totalProcesados(1)
                                .conError(0)
                                .build());

        mockMvc.perform(
                        multipart("/pedidos/cargar")
                                .file(file)
                                .header(
                                        "Idempotency-key",
                                        "IDEMP-1"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.guardados")
                                .value(1))
                .andExpect(
                        jsonPath("$.totalProcesados")
                                .value(1))
                .andExpect(
                        jsonPath("$.conError")
                                .value(0));
    }

    @Test
    void shouldReturnInternalErrorWhenHeaderMissing()
            throws Exception {
        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "orders.csv",
                        "text/csv",
                        "content".getBytes());

        mockMvc.perform(
                        multipart("/pedidos/cargar")
                                .file(file))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturn400WhenRequestAlreadyProcessed()
            throws Exception {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "orders.csv",
                        "text/csv",
                        "content".getBytes());

        when(idempotencyPort.find(anyString(), anyString()))
                .thenReturn(
                        Optional.of(
                                Idempotency.builder()
                                        .build()));

        mockMvc.perform(
                        multipart("/pedidos/cargar")
                                .file(file)
                                .header(
                                        "Idempotency-key",
                                        "IDEMP-1"))
                .andExpect(status().isBadRequest());
    }
}
