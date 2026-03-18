package com.reto.catalog.dto;

public class OrderCancelledEvent {
    private Long productId;
    private Integer cantidad;

    public OrderCancelledEvent() {}

    public OrderCancelledEvent(Long productId, Integer cantidad) {
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
