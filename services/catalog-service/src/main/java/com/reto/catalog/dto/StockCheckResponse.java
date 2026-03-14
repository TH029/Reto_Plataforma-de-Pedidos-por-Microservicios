package com.reto.catalog.dto;

public class StockCheckResponse {

    private Long productId;
    private Integer requestedQuantity;
    private Integer availableStock;
    private Boolean available;

    public StockCheckResponse() {
    }

    public StockCheckResponse(Long productId, Integer requestedQuantity, Integer availableStock, Boolean available) {
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
        this.available = available;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public Boolean getAvailable() {
        return available;
    }
}