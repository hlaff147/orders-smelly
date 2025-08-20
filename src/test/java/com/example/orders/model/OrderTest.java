package com.example.orders.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários para a entidade Order
 */
class OrderTest {

    @Test
    void shouldCreateOrderWithConstructor() {
        // Given
        Long id = 1L;
        String customerName = "João Silva";
        BigDecimal total = new BigDecimal("100.50");
        LocalDate orderDate = LocalDate.of(2024, 12, 15);

        // When
        Order order = new Order(id, customerName, total, orderDate);

        // Then
        assertThat(order.getId()).isEqualTo(id);
        assertThat(order.getCustomerName()).isEqualTo(customerName);
        assertThat(order.getTotal()).isEqualTo(total);
        assertThat(order.getOrderDate()).isEqualTo(orderDate);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.NEW);
    }

    @Test
    void shouldCreateEmptyOrder() {
        // When
        Order order = new Order();

        // Then
        assertThat(order.getId()).isNull();
        assertThat(order.getCustomerName()).isNull();
        assertThat(order.getTotal()).isNull();
        assertThat(order.getOrderDate()).isNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.NEW);
    }

    @Test
    void shouldSetAndGetAllFields() {
        // Given
        Order order = new Order();
        Long id = 2L;
        String customerName = "Maria Santos";
        BigDecimal total = new BigDecimal("250.75");
        LocalDate orderDate = LocalDate.of(2024, 12, 16);
        OrderStatus status = OrderStatus.PAID;

        // When
        order.setId(id);
        order.setCustomerName(customerName);
        order.setTotal(total);
        order.setOrderDate(orderDate);
        order.setStatus(status);

        // Then
        assertThat(order.getId()).isEqualTo(id);
        assertThat(order.getCustomerName()).isEqualTo(customerName);
        assertThat(order.getTotal()).isEqualTo(total);
        assertThat(order.getOrderDate()).isEqualTo(orderDate);
        assertThat(order.getStatus()).isEqualTo(status);
    }

    @Test
    void shouldGenerateToStringCorrectly() {
        // Given
        Long id = 1L;
        String customerName = "João Silva";
        BigDecimal total = new BigDecimal("100.50");
        LocalDate orderDate = LocalDate.of(2024, 12, 15);
        Order order = new Order(id, customerName, total, orderDate);

        // When
        String toString = order.toString();

        // Then
        assertThat(toString)
                .contains("Order{")
                .contains("id=1")
                .contains("customer='João Silva'")
                .contains("total=100.50")
                .contains("orderDate=2024-12-15")
                .contains("status=NEW");
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        Order order = new Order();

        // When/Then - não deve lançar exceções
        order.setId(null);
        order.setCustomerName(null);
        order.setTotal(null);
        order.setOrderDate(null);
        order.setStatus(null);

        assertThat(order.getId()).isNull();
        assertThat(order.getCustomerName()).isNull();
        assertThat(order.getTotal()).isNull();
        assertThat(order.getOrderDate()).isNull();
        assertThat(order.getStatus()).isNull();
    }
}
