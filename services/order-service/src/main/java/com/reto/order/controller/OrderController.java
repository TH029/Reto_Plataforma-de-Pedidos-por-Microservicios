package com.reto.order.controller;

import com.reto.order.dto.CreateOrderRequest;
import com.reto.order.dto.OrderResponse;
import com.reto.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> crearPedido(
            @RequestBody CreateOrderRequest request,
            @RequestHeader(value = "CorrelationId", required = false) String correlationId) {

        OrderResponse response = orderService.crearPedido(request, correlationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> obtenerTodos() {
        return ResponseEntity.ok(orderService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.obtenerPorId(id));
    }
}
