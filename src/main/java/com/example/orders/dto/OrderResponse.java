package com.example.orders.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.orders.model.OrderStatus;

/**
 * DTO de resposta para pedidos
 * Evita vazar a entidade diretamente para o cliente
 */
public class OrderResponse {
    private Long id;
    private String customerName;
    private BigDecimal total;
    private LocalDate orderDate;
    private OrderStatus status;

    public OrderResponse() {
    }

    public OrderResponse(Long id, String customerName, BigDecimal total, LocalDate orderDate, OrderStatus status) {
        this.id = id;
        this.customerName = customerName;
        this.total = total;
        this.orderDate = orderDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
