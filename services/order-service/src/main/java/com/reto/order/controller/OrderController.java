package com.reto.order.controller;

import com.reto.order.dto.CreateOrderRequest;
import com.reto.order.dto.OrderResponse;
import com.reto.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> crearPedido(@RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.crearPedido(request);
        return ResponseEntity.ok(response);
    }
}