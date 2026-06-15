package com.diego.orders.processor.application.usecase;

import com.diego.orders.processor.application.dto.OrderErrorDetailDTO;
import com.diego.orders.processor.application.dto.PedidoDTO;
import com.diego.orders.processor.application.dto.ResponseDTO;
import com.diego.orders.processor.application.mapper.ErrorDetailMapper;
import com.diego.orders.processor.application.mapper.PedidoMapper;
import com.diego.orders.processor.application.util.BatchProcessor;
import com.diego.orders.processor.application.util.DateUtil;
import com.diego.orders.processor.domain.model.OrderStatus;
import com.diego.orders.processor.domain.model.Pedido;
import com.diego.orders.processor.domain.port.out.PedidoRepositoryPort;
import com.diego.orders.processor.domain.service.PedidoDomainService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProcessUseCase implements OrderProcessBaseUseCase {

    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final PedidoDomainService pedidoDomainService;

    @Value("${app.batch-process.batch-size}")
    private int batchSize;

    public ResponseDTO process(List<PedidoDTO> pedidosDtos) {
        List<OrderErrorDetailDTO> errorDetails = new ArrayList<>();
        AtomicInteger savedCount = new AtomicInteger(0);

        BatchProcessor.run(pedidosDtos, batchSize, pedidosDtosChunk -> {
            List<Pedido> pedidosValidos = validate(errorDetails, pedidosDtosChunk);

            if (!CollectionUtils.isEmpty(pedidosValidos)) {
                pedidoRepositoryPort.saveAll(pedidosValidos);
            }

            savedCount.getAndAccumulate(pedidosValidos.size(), Integer::sum);
        });

        return ResponseDTO.builder()
                .guardados(savedCount.get())
                .totalProcesados(pedidosDtos.size())
                .conError(errorDetails.size())
                .detalleErrores(errorDetails)
                .build();
    }

    private List<Pedido> validate(List<OrderErrorDetailDTO> errorDetails, List<PedidoDTO> pedidosDto) {
        List<PedidoErrorWrapper> ordersDecorated = pedidosDto.stream()
                .map(order -> getOrderInputErrors(order, pedidosDto))
                .toList();

        List<PedidoErrorWrapper> validOrdersDecorated = getValidOrdersDecorated(ordersDecorated);
        List<PedidoErrorWrapper> invalidOrdersDecorated = getInvalidOrdersDecorated(ordersDecorated);

        List<OrderErrorDetailDTO> errorDetailDTOS = mapDecoratedToErrorDetail(invalidOrdersDecorated);
        errorDetails.addAll(errorDetailDTOS);

        List<Pedido> pedidos = mapToDomainOrder(validOrdersDecorated);

        pedidos.forEach(pedidoDomainService::validatePedido);

        List<Pedido> pedidosValidos = getValidOrders(pedidos);
        List<Pedido> pedidosInvalidos = getInvalidOrders(pedidos);

        List<OrderErrorDetailDTO> domainErrorDetailDTOS = mapToOrderErrorDetail(pedidosInvalidos);
        errorDetails.addAll(domainErrorDetailDTOS);
        sortByLineNumber(errorDetails);
        return pedidosValidos;
    }

    private void sortByLineNumber(List<OrderErrorDetailDTO> domainErrorDetailDTOS) {
        domainErrorDetailDTOS.sort(Comparator.comparing(OrderErrorDetailDTO::getNumeroLinea));
    }

    private List<Pedido> mapToDomainOrder(List<PedidoErrorWrapper> validOrdersDecorated) {
        return validOrdersDecorated.stream()
                .map(validatedOrder -> PedidoMapper.toDomain(validatedOrder.getPedido()))
                .toList();
    }

    private List<OrderErrorDetailDTO> mapToOrderErrorDetail(List<Pedido> invalidOrders) {
        return invalidOrders.stream()
                .map(ErrorDetailMapper::toErrorDetail)
                .toList();
    }

    private List<OrderErrorDetailDTO> mapDecoratedToErrorDetail(List<PedidoErrorWrapper> invalidOrdersDecorated) {
        return invalidOrdersDecorated.stream()
                .map(decorator ->
                        OrderErrorDetailDTO.builder()
                                .numeroLinea(decorator.getPedido().getPosicion())
                                .motivo(String.join(", ", decorator.getErrors()))
                                .build()
                )
                .toList();
    }

    private PedidoErrorWrapper getOrderInputErrors(PedidoDTO pedidoDTO, List<PedidoDTO> orders) {
        List<String> inputErrors = validateOrderInput(pedidoDTO, orders);
        return PedidoErrorWrapper.builder()
                .pedido(pedidoDTO)
                .errors(inputErrors)
                .build();
    }

    private List<String> validateOrderInput(PedidoDTO pedido, List<PedidoDTO> pedidos) {
        List<String> errors = new ArrayList<>();

        if (!StringUtils.hasText(pedido.getNumeroPedido())) {
            errors.add("NUMERO_DE_PEDIDO_INVALIDO");
        } else {
            List<PedidoDTO> _pedidoDTOs = pedidos.stream()
                    .filter(p -> pedido.getNumeroPedido().equals(p.getNumeroPedido()))
                    .toList();
            if (_pedidoDTOs.size() >= 2) {
                errors.add("DUPLICADO_EN_ARCHIVO");
            }
        }

        if (!StringUtils.hasText(pedido.getEstado()) || !OrderStatus.isValid(pedido.getEstado())) {
            errors.add("ESTADO_INVALIDO");
        }

        if (!StringUtils.hasText(pedido.getRequiereRefrigeracion())
                || (!pedido.getRequiereRefrigeracion().equalsIgnoreCase("true")
                && !pedido.getRequiereRefrigeracion().equalsIgnoreCase("false"))) {
            errors.add("REQUIERE_REFRIGERACION_INVALIDO");
        }

        if (!StringUtils.hasText(pedido.getFechaEntrega()) || !validarFechaEntrega(pedido.getFechaEntrega())) {
            errors.add("FECHA_INVALIDA");
        }

        if (!StringUtils.hasText(pedido.getClienteId())) {
            errors.add("CLIENTE_INVALIDO");
        }

        if (!StringUtils.hasText(pedido.getZonaEntrega())) {
            errors.add("ZONA_INVALIDA");
        }

        return errors;
    }

    private List<PedidoErrorWrapper> getValidOrdersDecorated(List<PedidoErrorWrapper> pedidos) {
        return pedidos.stream()
                .filter(pedido -> CollectionUtils.isEmpty(pedido.getErrors()))
                .toList();
    }

    private List<Pedido> getValidOrders(List<Pedido> pedidos) {
        return pedidos.stream()
                .filter(pedido -> CollectionUtils.isEmpty(pedido.getErrores()))
                .toList();
    }

    private List<PedidoErrorWrapper> getInvalidOrdersDecorated(List<PedidoErrorWrapper> pedidos) {
        return pedidos.stream()
                .filter(pedido -> !CollectionUtils.isEmpty(pedido.getErrors()))
                .toList();
    }

    private List<Pedido> getInvalidOrders(List<Pedido> pedidos) {
        return pedidos.stream()
                .filter(pedido -> !CollectionUtils.isEmpty(pedido.getErrores()))
                .toList();
    }

    private boolean validarFechaEntrega(String fechaEntrega) {
        LocalDate parsedFechaEntrega = DateUtil.parseDate(fechaEntrega);
        return parsedFechaEntrega != null;
    }

    @AllArgsConstructor
    @Builder
    @Getter
    static class PedidoErrorWrapper {

        private PedidoDTO pedido;
        private List<String> errors = new ArrayList<>();
    }
}