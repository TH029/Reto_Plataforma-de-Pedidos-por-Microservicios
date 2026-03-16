package com.reto.order.entity;

public class PedidoNoEncontradoException extends RuntimeException {
    public PedidoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public PedidoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
