package com.example.orders.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO refatorado com encapsulamento correto e validações
 */
public class FulfillRequest {

    @NotNull(message = "ID do pedido é obrigatório")
    @Positive(message = "ID do pedido deve ser positivo")
    private Long orderId;

    public FulfillRequest() {
    }

    public FulfillRequest(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}