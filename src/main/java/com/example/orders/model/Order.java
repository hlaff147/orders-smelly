package com.example.orders.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade Order refatorada para usar tipos apropriados:
 * - BigDecimal para valores monetários (evita problemas de precisão com double)
 * - LocalDate para datas (type-safe e thread-safe)
 * - Enum para status (evita valores inválidos)
 */
public class Order {
    private Long id;
    private String customerName;
    private BigDecimal total; // Corrigido: BigDecimal para dinheiro
    private LocalDate orderDate; // Corrigido: LocalDate para data
    private OrderStatus status = OrderStatus.NEW; // Corrigido: Enum para status

    public Order() {
    }

    public Order(Long id, String customerName, BigDecimal total, LocalDate orderDate) {
        this.id = id;
        this.customerName = customerName;
        this.total = total;
        this.orderDate = orderDate;
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

    @Override
    public String toString() {
        return "Order{id=" + id + ", customer='" + customerName + "', total=" + total +
                ", orderDate=" + orderDate + ", status=" + status + "}";
    }
}
