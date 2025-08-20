package com.example.orders.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para criação de pedidos com validações apropriadas
 * Encapsulamento correto com getters/setters ao invés de campos públicos
 */
public class CreateOrderRequest {

    @NotBlank(message = "Nome do cliente é obrigatório")
    private String customerName;

    @NotNull(message = "Total é obrigatório")
    @Positive(message = "Total deve ser positivo")
    private BigDecimal total;

    @NotBlank(message = "Data é obrigatória")
    private String orderDate; // Recebe como String e converte no service

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(String customerName, BigDecimal total, String orderDate) {
        this.customerName = customerName;
        this.total = total;
        this.orderDate = orderDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
}
