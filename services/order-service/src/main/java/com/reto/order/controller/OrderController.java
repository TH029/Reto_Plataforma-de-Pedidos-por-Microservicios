package com.reto.order.controller;

import com.reto.order.entity.OrderRequest;
import com.reto.order.entity.OrderResponse;
import com.reto.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedidos")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * POST /pedidos
     * Crear un nuevo pedido
     * Request body: { "usuarioId": 1, "items": [{"productoId": 5, "cantidad": 2}] }
     */
    @PostMapping
    public ResponseEntity<OrderResponse> crearPedido(@RequestBody OrderRequest orderRequest) {
        OrderResponse orden = orderService.crearPedido(orderRequest);
        return ResponseEntity.ok(orden);
    }

    /**
     * GET /pedidos/{id}
     * Obtener detalles de un pedido específico
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> obtenerPedido(@PathVariable Long id) {
        OrderResponse orden = orderService.obtenerPedido(id);
        return ResponseEntity.ok(orden);
    }

    /**
     * GET /pedidos/usuario/{usuarioId}
     * Listar todos los pedidos de un usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<OrderResponse>> listarPedidosPorUsuario(@PathVariable Long usuarioId) {
        List<OrderResponse> ordenes = orderService.listarPedidosPorUsuario(usuarioId);
        return ResponseEntity.ok(ordenes);
    }

    /**
     * PUT /pedidos/{id}
     * Actualizar el estado de un pedido
     * Request body: { "estado": "CONFIRMED" }
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        
        String nuevoEstado = body.get("estado");
        if (nuevoEstado == null || nuevoEstado.isEmpty()) {
            throw new RuntimeException("El estado no puede estar vacío");
        }

        OrderResponse orden = orderService.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok(orden);
    }
}


