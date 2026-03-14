package com.reto.order.service;

import com.reto.order.dto.CreateOrderRequest;
import com.reto.order.dto.OrderResponse;
import com.reto.order.entity.OrderEntity;
import com.reto.order.entity.OrderStatus;
import com.reto.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse crearPedido(CreateOrderRequest request) {
        OrderEntity order = new OrderEntity();
        order.setProductId(request.getProductId());
        order.setCantidad(request.getCantidad());
        order.setEstado(OrderStatus.CREATED);

        OrderEntity savedOrder = orderRepository.save(order);

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getProductId(),
                savedOrder.getCantidad(),
                savedOrder.getEstado(),
                savedOrder.getFechaCreacion()
        );
    }
}