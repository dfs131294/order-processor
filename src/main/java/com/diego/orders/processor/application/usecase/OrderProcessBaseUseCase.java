package com.diego.orders.processor.application.usecase;

import com.diego.orders.processor.application.dto.PedidoDTO;
import com.diego.orders.processor.application.dto.ResponseDTO;

import java.util.List;

public interface OrderProcessBaseUseCase {

    ResponseDTO process(List<PedidoDTO> orders);
}
