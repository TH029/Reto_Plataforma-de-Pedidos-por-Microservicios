package com.reto.order.dto;

public class OrderCreatedEvent {
    private Long productId;
    private Integer cantidad;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(Long productId, Integer cantidad) {
        this.productId = productId;
        this.cantidad = cantidad;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
