package com.diego.orders.processor.application.adapter.in;

import com.diego.orders.processor.application.dto.ErrorResponseDTO;
import com.diego.orders.processor.application.dto.PedidoDTO;
import com.diego.orders.processor.application.dto.ResponseDTO;
import com.diego.orders.processor.application.exception.IdempotencyException;
import com.diego.orders.processor.application.model.Idempotency;
import com.diego.orders.processor.application.port.in.FileParser;
import com.diego.orders.processor.application.port.out.IdempotencyPort;
import com.diego.orders.processor.application.usecase.OrderProcessUseCase;
import com.diego.orders.processor.application.util.HashUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class OrderProcessController {

    private final FileParser<PedidoDTO> csvParser;
    private final OrderProcessUseCase orderProcessUseCase;
    private final IdempotencyPort idempotencyPort;

    @Operation(summary = "Process orders")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders processed successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error de idempotencia",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid File",
                                            value = """
                                                    {
                                                        "code": "CODE_02",
                                                        "message": "Archivo ya fue procesado anteriormente.",
                                                        "details": null,
                                                        "correlationId": "2ff8c27d-4ac7-4111-98a2-3fb112d3b731"
                                                    }
                                                    """
                                    )
                            }

                    )),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid File",
                                            value = """
                                                    {
                                                        "code": "CODE_01",
                                                        "message": "Required request header 'Idempotency-key' for method parameter type String is not present",
                                                        "details": null,
                                                        "correlationId": "41fa3b58-9249-4b41-8887-a692a646a520"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping(
            value = "/cargar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> load(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(name = "Idempotency-key") String idempotencyKey) throws IOException {
        String fileHash = HashUtil.sha256(getBytes(file));
        checkIdempotencyRequest(fileHash, idempotencyKey);
        List<PedidoDTO> orders = csvParser.parse(file.getInputStream());
        ResponseDTO response = orderProcessUseCase.process(orders);
        saveIdempotencyRequest(fileHash, idempotencyKey);
        return ResponseEntity.ok(response);
    }

    private void checkIdempotencyRequest(String fileHash, String idempotencyKey) {
        Optional<Idempotency> idempotency = idempotencyPort.find(idempotencyKey, fileHash);
        if (idempotency.isPresent()) {
            throw new IdempotencyException("Archivo ya fue procesado anteriormente.");
        }
    }

    private void saveIdempotencyRequest(String fileHash, String idempotencyKey) {
        Idempotency idempotency = Idempotency.builder()
                .id(UUID.randomUUID())
                .idempotencyKey(idempotencyKey)
                .fileHash(fileHash)
                .build();
        idempotencyPort.save(idempotency);
    }

    private byte[] getBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
