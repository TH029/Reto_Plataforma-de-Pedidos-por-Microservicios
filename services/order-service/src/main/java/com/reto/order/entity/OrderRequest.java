package com.reto.order.entity;

import java.util.List;

public class OrderRequest {
    private Long usuarioId;
    private List<OrderItemRequest> items;

    // Constructores
    public OrderRequest() {}

    public OrderRequest(Long usuarioId, List<OrderItemRequest> items) {
        this.usuarioId = usuarioId;
        this.items = items;
    }

    // Getters y Setters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
