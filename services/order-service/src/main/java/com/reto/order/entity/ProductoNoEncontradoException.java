package com.reto.order.entity;

public class ProductoNoEncontradoException extends RuntimeException {
    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public ProductoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
