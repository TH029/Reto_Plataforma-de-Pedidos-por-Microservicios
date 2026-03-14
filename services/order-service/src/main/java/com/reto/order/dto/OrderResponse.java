package com.reto.order.dto;

import com.reto.order.entity.OrderStatus;
import java.time.LocalDateTime;

public class OrderResponse {

    private Long id;
    private Long productId;
    private Integer cantidad;
    private OrderStatus estado;
    private LocalDateTime fechaCreacion;

    public OrderResponse() {
    }

    public OrderResponse(Long id, Long productId, Integer cantidad, OrderStatus estado, LocalDateTime fechaCreacion) {
        this.id = id;
        this.productId = productId;
        this.cantidad = cantidad;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public OrderStatus getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}