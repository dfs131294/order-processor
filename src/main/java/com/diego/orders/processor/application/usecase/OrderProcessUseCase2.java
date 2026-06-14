package com.diego.orders.processor.application.usecase;

import com.diego.orders.processor.application.dto.OrderErrorDetailDTO;
import com.diego.orders.processor.application.dto.PedidoDTO;
import com.diego.orders.processor.application.dto.ResponseDTO;
import com.diego.orders.processor.application.mapper.PedidoMapper;
import com.diego.orders.processor.application.util.BatchProcessor;
import com.diego.orders.processor.application.util.DateUtil;
import com.diego.orders.processor.domain.model.Cliente;
import com.diego.orders.processor.domain.model.OrderStatus;
import com.diego.orders.processor.domain.model.Pedido;
import com.diego.orders.processor.domain.port.out.ClienteRepositoryPort;
import com.diego.orders.processor.domain.port.out.PedidoRepositoryPort;
import com.diego.orders.processor.domain.port.out.ZonaRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProcessUseCase2 implements OrderProcessBaseUseCase {

    private static final ZoneId LIMA_ZONE = ZoneId.of("America/Lima");

    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ZonaRepositoryPort zonaRepositoryPort;

    public ResponseDTO process(List<PedidoDTO> orders) {
        List<ValidatorDecorator> invalidOrders = new ArrayList<>();
        AtomicInteger savedCount = new AtomicInteger(0);
        int batchSize = 5;

        BatchProcessor.run(orders, batchSize, ordersChunk -> {
            List<ValidatorDecorator> ordersInputValidated = ordersChunk.stream()
                    .map(order -> getOrderInputErrors(order, ordersChunk))
                    .toList();

            List<ValidatorDecorator> validOrdersDecorated = getValidOrdersDecorated(ordersInputValidated);
            List<ValidatorDecorator> invalidOrdersDecorated = getInvalidOrdersDecorated(ordersInputValidated);
            invalidOrders.addAll(invalidOrdersDecorated);

            List<Pedido> domainOrders = validOrdersDecorated.stream()
                    .map(validatedOrder -> PedidoMapper.toDomain((PedidoDTO) validatedOrder.getPedido()))
                    .toList();

            List<ValidatorDecorator> domainOrdersValidated = domainOrders.stream()
                    .map(this::getOrderErrors)
                    .toList();

            List<ValidatorDecorator> validDomainOrdersDecorated = getValidOrdersDecorated(domainOrdersValidated);
            List<ValidatorDecorator> invalidDomainOrdersDecorated = getInvalidOrdersDecorated(domainOrdersValidated);
            invalidOrders.addAll(invalidDomainOrdersDecorated);

            List<Pedido> validDomainOrders = validDomainOrdersDecorated.stream()
                    .map(orderValidated -> (Pedido) orderValidated.getPedido())
                    .toList();

            if (!CollectionUtils.isEmpty(validDomainOrders)) {
                pedidoRepositoryPort.saveAll(validDomainOrders);
            }

            savedCount.getAndAccumulate(validDomainOrders.size(), Integer::sum);
        });

        return ResponseDTO.builder()
                .guardados(savedCount.get())
                .totalProcesados(orders.size())
                .conError(invalidOrders.size())
                .detalleErrores(mapToErrorDetail(invalidOrders))
                .build();
    }

    public ValidatorDecorator getOrderInputErrors(PedidoDTO order, List<PedidoDTO> orders) {
        List<String> inputErrors = validateOrderInput(order, orders);

        return ValidatorDecorator.builder()
                .pedido(order)
                .posicion(order.getPosicion())
                .errors(inputErrors)
                .build();
    }

    public ValidatorDecorator getOrderErrors(Pedido order) {
        List<String> errors = validateOrder(order);

        return ValidatorDecorator.builder()
                .pedido(order)
                .posicion(order.getPosicion())
                .errors(errors)
                .build();
    }

    /*
    public ValidatorDecorator validateOrder(PedidoDTO order, List<PedidoDTO> orders) {
        ValidatorDecorator orderValidatorDecorator = ValidatorDecorator.builder()
                .pedido(order)
                .build();

        if (!StringUtils.hasText(order.getNumeroPedido())) {
            orderValidatorDecorator.addError("NUMERO DE PEDIDO INVALIDO");
        } else {
            List<PedidoDTO> _orders = orders.stream()
                    .filter(_order -> order.getNumeroPedido().equals(_order.getNumeroPedido()))
                    .toList();
            if (_orders.size() >= 2) {
                orderValidatorDecorator.addError("DUPLICADO EN ARCHIVO");
            }

            pedidoRepositoryPort.findByNumeroPedido(order.getNumeroPedido())
                    .ifPresent(__ -> orderValidatorDecorator.addError("DUPLICADO"));

        }

        if (!StringUtils.hasText(order.getEstado()) || !OrderStatus.isValid(order.getEstado())) {
            orderValidatorDecorator.addError("ESTADO_INVALIDO");
        }

        if (!StringUtils.hasText(order.getRequiereRefrigeracion())
                || (!order.getRequiereRefrigeracion().equalsIgnoreCase("true")
                && !order.getRequiereRefrigeracion().equalsIgnoreCase("false"))) {
            orderValidatorDecorator.addError("REQUIERE_REFRIGERACION_INVALIDO");
        }

        if (!StringUtils.hasText(order.getFechaEntrega()) || !validarFechaEntrega(order.getFechaEntrega())) {
            orderValidatorDecorator.addError("FECHA_INVALIDA");
        }

        if (!StringUtils.hasText(order.getClienteId())) {
            orderValidatorDecorator.addError("CLIENTE_INVALIDO");
        }

        if (!StringUtils.hasText(order.getZonaEntrega())) {
            orderValidatorDecorator.addError("ZONA_INVALIDA");
        } else {
            zonaRepositoryPort.findById(order.getZonaEntrega())
                    .ifPresentOrElse(
                            zona -> {
                                if (order.isRequiereRefrigeracion() && !zona.isSoporteRefrigeracion()) {
                                    errors.add("CADENA_FRIO_NO_SOPORTADA");
                                }
                            },
                            () -> errors.add("ZONA_INVALIDA")
                    );
        }

        return orderValidatorDecorator;
    }
*/

    public List<String> validateOrderInput(PedidoDTO order, List<PedidoDTO> orders) {
        List<String> errors = new ArrayList<>();

        if (!StringUtils.hasText(order.getNumeroPedido())) {
            errors.add("NUMERO DE PEDIDO INVALIDO");
        } else {
            List<PedidoDTO> _orders = orders.stream()
                    .filter(_order -> order.getNumeroPedido().equals(_order.getNumeroPedido()))
                    .toList();
            if (_orders.size() >= 2) {
                errors.add("DUPLICADO EN ARCHIVO");
            }
        }

        if (!StringUtils.hasText(order.getEstado()) || !OrderStatus.isValid(order.getEstado())) {
            errors.add("ESTADO_INVALIDO");
        }

        if (!StringUtils.hasText(order.getRequiereRefrigeracion())
                || (!order.getRequiereRefrigeracion().equalsIgnoreCase("true")
                && !order.getRequiereRefrigeracion().equalsIgnoreCase("false"))) {
            errors.add("REQUIERE_REFRIGERACION_INVALIDO");
        }

        if (!StringUtils.hasText(order.getFechaEntrega()) || !validarFechaEntrega(order.getFechaEntrega())) {
            errors.add("FECHA_INVALIDA");
        }

        if (!StringUtils.hasText(order.getClienteId())) {
            errors.add("CLIENTE_INVALIDO");
        }

        if (!StringUtils.hasText(order.getZonaEntrega())) {
            errors.add("ZONA_INVALIDA");
        }

        return errors;
    }

    public List<String> validateOrder(Pedido order) {
        List<String> errors = new ArrayList<>();

        pedidoRepositoryPort.findByNumeroPedido(order.getNumeroPedido())
                .ifPresent(__ -> errors.add("DUPLICADO"));

        Optional<Cliente> cliente = clienteRepositoryPort.findById(order.getClienteId());
        if (cliente.isEmpty()) {
            errors.add("CLIENTE_NO_ENCONTRADO");
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

        return errors;
    }

    private List<ValidatorDecorator> getValidOrdersDecorated(List<ValidatorDecorator> ordersValidated) {
        return ordersValidated.stream()
                .filter(orderValidated -> CollectionUtils.isEmpty(orderValidated.getErrors()))
                .toList();
    }

    private List<ValidatorDecorator> getInvalidOrdersDecorated(List<ValidatorDecorator> ordersValidated) {
        return ordersValidated.stream()
                .filter(orderValidated -> !CollectionUtils.isEmpty(orderValidated.getErrors()))
                .toList();
    }

    private List<OrderErrorDetailDTO> mapToErrorDetail(List<ValidatorDecorator> invalidOrders) {
        return invalidOrders.stream()
                .map(orderValidated ->
                        OrderErrorDetailDTO.builder()
                                .numeroLinea(orderValidated.getPosicion())
                                .motivo(String.join(", ", orderValidated.getErrors()))
                                .build()
                )
                .toList();
    }

    private boolean validarFechaEntrega(String fechaEntrega) {
        LocalDate parsedFechaEntrega = DateUtil.parseDate(fechaEntrega);
        return parsedFechaEntrega != null && validarFechaEntrega(parsedFechaEntrega);
    }

    private boolean validarFechaEntrega(LocalDate fechaEntrega) {
        LocalDate now = LocalDate.now(LIMA_ZONE);
        return fechaEntrega.isAfter(now);
    }

    @AllArgsConstructor
    @Builder
    @Getter
    static class ValidatorDecorator<T> {

        private T pedido;
        private List<String> errors = new ArrayList<>();
        private int posicion;

        public boolean hasError(String error) {
            return errors.stream()
                    .anyMatch(e -> e.equalsIgnoreCase(error));
        }

        public void addError(String error) {
            errors.add(error);
        }
    }
}
/*CLIENTE_NO_ENCONTRADO,
        ZONA_INVALIDA, FECHA_INVALIDA, ESTADO_INVALIDO, DUPLICADO,
        CADENA_FRIO_NO_SOPORTADA).*/
