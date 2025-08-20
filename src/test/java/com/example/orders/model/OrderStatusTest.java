package com.example.orders.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários para o enum OrderStatus
 */
class OrderStatusTest {

    @Test
    void shouldHaveCorrectValues() {
        // When/Then
        assertThat(OrderStatus.values()).hasSize(4);
        assertThat(OrderStatus.values()).containsExactly(
                OrderStatus.NEW,
                OrderStatus.PAID,
                OrderStatus.FULFILLED,
                OrderStatus.CANCELLED
        );
    }

    @Test
    void shouldHaveCorrectDescriptions() {
        // When/Then
        assertThat(OrderStatus.NEW.getDescription()).isEqualTo("Novo");
        assertThat(OrderStatus.PAID.getDescription()).isEqualTo("Pago");
        assertThat(OrderStatus.FULFILLED.getDescription()).isEqualTo("Entregue");
        assertThat(OrderStatus.CANCELLED.getDescription()).isEqualTo("Cancelado");
    }

    @Test
    void shouldBeOrderedCorrectly() {
        // When/Then - ordem representa o fluxo natural do pedido
        assertThat(OrderStatus.NEW.ordinal()).isEqualTo(0);
        assertThat(OrderStatus.PAID.ordinal()).isEqualTo(1);
        assertThat(OrderStatus.FULFILLED.ordinal()).isEqualTo(2);
        assertThat(OrderStatus.CANCELLED.ordinal()).isEqualTo(3);
    }

    @Test
    void shouldParseFromString() {
        // When/Then
        assertThat(OrderStatus.valueOf("NEW")).isEqualTo(OrderStatus.NEW);
        assertThat(OrderStatus.valueOf("PAID")).isEqualTo(OrderStatus.PAID);
        assertThat(OrderStatus.valueOf("FULFILLED")).isEqualTo(OrderStatus.FULFILLED);
        assertThat(OrderStatus.valueOf("CANCELLED")).isEqualTo(OrderStatus.CANCELLED);
    }
}
