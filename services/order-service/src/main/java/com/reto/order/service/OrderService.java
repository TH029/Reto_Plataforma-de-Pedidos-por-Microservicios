package com.reto.order.service;

import com.reto.order.client.CatalogClient;
import com.reto.order.dto.CreateOrderRequest;
import com.reto.order.dto.OrderResponse;
import com.reto.order.dto.StockCheckResponse;
import com.reto.order.entity.OrderEntity;
import com.reto.order.entity.OrderStatus;
import com.reto.order.exception.InsufficientStockException;
import com.reto.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;

    public OrderService(OrderRepository orderRepository, CatalogClient catalogClient) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
    }

    public OrderResponse crearPedido(CreateOrderRequest request, String correlationId) {
        StockCheckResponse stockResponse = catalogClient.checkStock(
                request.getProductId(),
                request.getCantidad(),
                correlationId
        );

        if (stockResponse == null || !Boolean.TRUE.equals(stockResponse.getAvailable())) {
            throw new InsufficientStockException(
                    "No hay stock suficiente para el producto " + request.getProductId()
            );
        }

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