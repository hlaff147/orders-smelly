package com.example.orders.model;

/**
 * Enum para representar os status possíveis de um pedido
 * Substituindo o uso de String que era propenso a erros
 */
public enum OrderStatus {
    NEW("Novo"),
    PAID("Pago"),
    FULFILLED("Entregue"),
    CANCELLED("Cancelado");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
