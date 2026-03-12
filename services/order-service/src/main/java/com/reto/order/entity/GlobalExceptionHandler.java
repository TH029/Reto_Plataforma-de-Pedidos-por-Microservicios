package com.reto.order.entity;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones cuando no se encuentra un producto
     */
    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarProductoNoEncontrado(ProductoNoEncontradoException e) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", "Producto no encontrado");
        respuesta.put("mensaje", e.getMessage());
        respuesta.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    /**
     * Maneja excepciones cuando no hay stock suficiente
     */
    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> manejarStockInsuficiente(StockInsuficienteException e) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", "Stock insuficiente");
        respuesta.put("mensaje", e.getMessage());
        respuesta.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    /**
     * Maneja excepciones cuando no se encuentra un pedido
     */
    @ExceptionHandler(PedidoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarPedidoNoEncontrado(PedidoNoEncontradoException e) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", "Pedido no encontrado");
        respuesta.put("mensaje", e.getMessage());
        respuesta.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    /**
     * Maneja excepciones genéricas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarExcepcionGeneral(Exception e) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("error", "Error interno");
        respuesta.put("mensaje", e.getMessage());
        respuesta.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
    }
}
