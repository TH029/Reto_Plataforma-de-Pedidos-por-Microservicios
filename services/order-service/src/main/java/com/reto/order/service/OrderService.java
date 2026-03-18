package com.reto.order.service;
import com.reto.order.client.CatalogClient;
import com.reto.order.config.RabbitMQConfig;
import com.reto.order.dto.CreateOrderRequest;
import com.reto.order.dto.OrderCreatedEvent;
import com.reto.order.dto.OrderResponse;
import com.reto.order.dto.StockCheckResponse;
import com.reto.order.entity.OrderEntity;
import com.reto.order.entity.OrderStatus;
import com.reto.order.exception.InsufficientStockException;
import com.reto.order.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;
    private final RabbitTemplate rabbitTemplate;

    public OrderService(OrderRepository orderRepository, CatalogClient catalogClient, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
        this.rabbitTemplate = rabbitTemplate;
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
        order.setUsuarioId(1L); // Temporarily hardcoded to bypass DB constraint
        order.setEstado(OrderStatus.CREATED);

        OrderEntity savedOrder = orderRepository.save(order);

        // Publicar evento en RabbitMQ para descontar stock de forma asíncrona
        System.out.println("Enviando mensaje a RabbitMQ para producto: " + savedOrder.getProductId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_STOCK,
                new OrderCreatedEvent(savedOrder.getProductId(), savedOrder.getCantidad())
        );

        return new OrderResponse(
                savedOrder.getId(),
                savedOrder.getProductId(),
                savedOrder.getCantidad(),
                savedOrder.getUsuarioId(),
                savedOrder.getEstado(),
                savedOrder.getFechaCreacion()
        );
    }

    public List<OrderResponse> obtenerTodos() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse obtenerPorId(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));
        return mapToResponse(order);
    }

    public OrderResponse cancelarPedido(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + id));

        // Regla: Solo cancelar si está CREATED o PENDIENTE
        if (order.getEstado() != OrderStatus.CREATED && order.getEstado() != OrderStatus.PENDIENTE) {
            throw new RuntimeException("No se puede cancelar el pedido porque ya está en estado: " + order.getEstado());
        }

        order.setEstado(OrderStatus.CANCELADO);
        OrderEntity updatedOrder = orderRepository.save(order);

        return mapToResponse(updatedOrder);
    }

    private OrderResponse mapToResponse(OrderEntity order) {
        return new OrderResponse(
                order.getId(),
                order.getProductId(),
                order.getCantidad(),
                order.getUsuarioId(),
                order.getEstado(),
                order.getFechaCreacion()
        );
    }
}
