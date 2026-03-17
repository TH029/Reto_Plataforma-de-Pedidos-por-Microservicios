package com.reto.order.entity;

public enum OrderStatus {
    PENDIENTE,
    PROCESANDO,
    ENVIADO,
    ENTREGADO,
    CANCELADO,
    COMPLETADO,
    CREATED // Mantener por compatibilidad actual
}