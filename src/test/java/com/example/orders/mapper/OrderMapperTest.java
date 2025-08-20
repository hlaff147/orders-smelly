package com.example.orders.mapper;

import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderResponse;
import com.example.orders.model.Order;
import com.example.orders.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes unitários para OrderMapper
 */
class OrderMapperTest {

    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        orderMapper = new OrderMapper();
    }

    @Test
    void shouldConvertCreateOrderRequestToEntity() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
                "João Silva",
                new BigDecimal("100.50"),
                "15-12-2024"
        );

        // When
        Order order = orderMapper.toEntity(request);

        // Then
        assertThat(order).isNotNull();
        assertThat(order.getId()).isNull(); // ID deve ser null para novo pedido
        assertThat(order.getCustomerName()).isEqualTo("João Silva");
        assertThat(order.getTotal()).isEqualTo(new BigDecimal("100.50"));
        assertThat(order.getOrderDate()).isEqualTo(LocalDate.of(2024, 12, 15));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.NEW);
    }

    @Test
    void shouldReturnNullWhenRequestIsNull() {
        // When
        Order order = orderMapper.toEntity(null);

        // Then
        assertThat(order).isNull();
    }

    @Test
    void shouldThrowExceptionForInvalidDateFormat() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
                "João Silva",
                new BigDecimal("100.50"),
                "2024-12-15" // formato errado (deve ser dd-MM-yyyy)
        );

        // When/Then
        assertThatThrownBy(() -> orderMapper.toEntity(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Data inválida. Use o formato dd-MM-yyyy: 2024-12-15");
    }

    @Test
    void shouldThrowExceptionForInvalidDate() {
        // Given
        CreateOrderRequest request = new CreateOrderRequest(
                "João Silva",
                new BigDecimal("100.50"),
                "32-13-2024" // data inválida
        );

        // When/Then
        assertThatThrownBy(() -> orderMapper.toEntity(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Data inválida. Use o formato dd-MM-yyyy: 32-13-2024");
    }

    @Test
    void shouldConvertOrderToResponse() {
        // Given
        Order order = new Order(
                1L,
                "Maria Santos",
                new BigDecimal("250.75"),
                LocalDate.of(2024, 12, 16)
        );
        order.setStatus(OrderStatus.PAID);

        // When
        OrderResponse response = orderMapper.toResponse(order);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerName()).isEqualTo("Maria Santos");
        assertThat(response.getTotal()).isEqualTo(new BigDecimal("250.75"));
        assertThat(response.getOrderDate()).isEqualTo(LocalDate.of(2024, 12, 16));
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldReturnNullWhenOrderIsNull() {
        // When
        OrderResponse response = orderMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    void shouldConvertOrderListToResponseList() {
        // Given
        Order order1 = new Order(1L, "João Silva", new BigDecimal("100.50"), LocalDate.of(2024, 12, 15));
        Order order2 = new Order(2L, "Maria Santos", new BigDecimal("250.75"), LocalDate.of(2024, 12, 16));
        order2.setStatus(OrderStatus.PAID);
        
        List<Order> orders = Arrays.asList(order1, order2);

        // When
        List<OrderResponse> responses = orderMapper.toResponseList(orders);

        // Then
        assertThat(responses).hasSize(2);
        
        OrderResponse response1 = responses.get(0);
        assertThat(response1.getId()).isEqualTo(1L);
        assertThat(response1.getCustomerName()).isEqualTo("João Silva");
        assertThat(response1.getTotal()).isEqualTo(new BigDecimal("100.50"));
        assertThat(response1.getStatus()).isEqualTo(OrderStatus.NEW);

        OrderResponse response2 = responses.get(1);
        assertThat(response2.getId()).isEqualTo(2L);
        assertThat(response2.getCustomerName()).isEqualTo("Maria Santos");
        assertThat(response2.getTotal()).isEqualTo(new BigDecimal("250.75"));
        assertThat(response2.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldReturnNullWhenOrderListIsNull() {
        // When
        List<OrderResponse> responses = orderMapper.toResponseList(null);

        // Then
        assertThat(responses).isNull();
    }

    @Test
    void shouldHandleEmptyOrderList() {
        // Given
        List<Order> orders = Arrays.asList();

        // When
        List<OrderResponse> responses = orderMapper.toResponseList(orders);

        // Then
        assertThat(responses).isEmpty();
    }

    @Test
    void shouldHandleOrderListWithNullElements() {
        // Given
        Order order1 = new Order(1L, "João Silva", new BigDecimal("100.50"), LocalDate.of(2024, 12, 15));
        List<Order> orders = Arrays.asList(order1, null);

        // When
        List<OrderResponse> responses = orderMapper.toResponseList(orders);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0)).isNotNull();
        assertThat(responses.get(1)).isNull();
    }

    @Test
    void shouldParseDifferentValidDateFormats() {
        // Given - teste com diferentes datas válidas
        CreateOrderRequest request1 = new CreateOrderRequest("Cliente 1", BigDecimal.TEN, "01-01-2024");
        CreateOrderRequest request2 = new CreateOrderRequest("Cliente 2", BigDecimal.TEN, "31-12-2024");
        CreateOrderRequest request3 = new CreateOrderRequest("Cliente 3", BigDecimal.TEN, "29-02-2024"); // ano bissexto

        // When
        Order order1 = orderMapper.toEntity(request1);
        Order order2 = orderMapper.toEntity(request2);
        Order order3 = orderMapper.toEntity(request3);

        // Then
        assertThat(order1.getOrderDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(order2.getOrderDate()).isEqualTo(LocalDate.of(2024, 12, 31));
        assertThat(order3.getOrderDate()).isEqualTo(LocalDate.of(2024, 2, 29));
    }
}
