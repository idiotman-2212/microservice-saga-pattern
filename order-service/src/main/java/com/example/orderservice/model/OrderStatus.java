package com.example.orderservice.model;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    PAYMENT_COMPLETED,
    PAYMENT_FAILED,
    INVENTORY_PENDING,
    INVENTORY_COMPLETED,
    INVENTORY_FAILED,
    COMPLETED,
    CANCELLED
}
