package com.reto.order.entity;

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }

    public StockInsuficienteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
