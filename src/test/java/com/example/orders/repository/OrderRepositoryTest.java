package com.example.orders.repository;

import com.example.orders.model.Order;
import com.example.orders.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários para OrderRepository incluindo testes de thread-safety
 */
class OrderRepositoryTest {

    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new OrderRepository();
        orderRepository.clear(); // Limpa estado entre testes
    }

    @Test
    void shouldSaveNewOrder() {
        // Given
        Order order = new Order(null, "João Silva", new BigDecimal("100.50"), LocalDate.now());

        // When
        Order savedOrder = orderRepository.save(order);

        // Then
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getId()).isEqualTo(1L);
        assertThat(savedOrder.getCustomerName()).isEqualTo("João Silva");
        assertThat(savedOrder.getTotal()).isEqualTo(new BigDecimal("100.50"));
    }

    @Test
    void shouldUpdateExistingOrder() {
        // Given
        Order order = new Order(null, "João Silva", new BigDecimal("100.50"), LocalDate.now());
        Order savedOrder = orderRepository.save(order);
        Long orderId = savedOrder.getId();

        // When - atualiza o pedido
        savedOrder.setCustomerName("João Silva Santos");
        savedOrder.setTotal(new BigDecimal("150.75"));
        savedOrder.setStatus(OrderStatus.PAID);
        Order updatedOrder = orderRepository.save(savedOrder);

        // Then
        assertThat(updatedOrder.getId()).isEqualTo(orderId); // ID não mudou
        assertThat(updatedOrder.getCustomerName()).isEqualTo("João Silva Santos");
        assertThat(updatedOrder.getTotal()).isEqualTo(new BigDecimal("150.75"));
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldGenerateSequentialIds() {
        // Given
        Order order1 = new Order(null, "Cliente 1", BigDecimal.TEN, LocalDate.now());
        Order order2 = new Order(null, "Cliente 2", BigDecimal.ONE, LocalDate.now());
        Order order3 = new Order(null, "Cliente 3", new BigDecimal("5.00"), LocalDate.now());

        // When
        Order saved1 = orderRepository.save(order1);
        Order saved2 = orderRepository.save(order2);
        Order saved3 = orderRepository.save(order3);

        // Then
        assertThat(saved1.getId()).isEqualTo(1L);
        assertThat(saved2.getId()).isEqualTo(2L);
        assertThat(saved3.getId()).isEqualTo(3L);
    }

    @Test
    void shouldFindOrderById() {
        // Given
        Order order = new Order(null, "Maria Santos", new BigDecimal("250.75"), LocalDate.now());
        Order savedOrder = orderRepository.save(order);

        // When
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

        // Then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getId()).isEqualTo(savedOrder.getId());
        assertThat(foundOrder.get().getCustomerName()).isEqualTo("Maria Santos");
        assertThat(foundOrder.get().getTotal()).isEqualTo(new BigDecimal("250.75"));
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        // When
        Optional<Order> foundOrder = orderRepository.findById(999L);

        // Then
        assertThat(foundOrder).isEmpty();
    }

    @Test
    void shouldFindAllOrders() {
        // Given
        Order order1 = new Order(null, "Cliente 1", BigDecimal.TEN, LocalDate.now());
        Order order2 = new Order(null, "Cliente 2", BigDecimal.ONE, LocalDate.now());
        
        orderRepository.save(order1);
        orderRepository.save(order2);

        // When
        List<Order> allOrders = orderRepository.findAll();

        // Then
        assertThat(allOrders).hasSize(2);
        assertThat(allOrders).extracting(Order::getCustomerName)
                .containsExactly("Cliente 1", "Cliente 2");
    }

    @Test
    void shouldReturnEmptyListWhenNoOrders() {
        // When
        List<Order> allOrders = orderRepository.findAll();

        // Then
        assertThat(allOrders).isEmpty();
    }

    @Test
    void shouldCheckIfOrderExists() {
        // Given
        Order order = new Order(null, "João Silva", BigDecimal.TEN, LocalDate.now());
        Order savedOrder = orderRepository.save(order);

        // When/Then
        assertThat(orderRepository.existsById(savedOrder.getId())).isTrue();
        assertThat(orderRepository.existsById(999L)).isFalse();
    }

    @Test
    void shouldClearAllOrders() {
        // Given
        orderRepository.save(new Order(null, "Cliente 1", BigDecimal.TEN, LocalDate.now()));
        orderRepository.save(new Order(null, "Cliente 2", BigDecimal.ONE, LocalDate.now()));
        
        assertThat(orderRepository.findAll()).hasSize(2);

        // When
        orderRepository.clear();

        // Then
        assertThat(orderRepository.findAll()).isEmpty();
        
        // Sequence deve resetar também
        Order newOrder = orderRepository.save(new Order(null, "Novo Cliente", BigDecimal.TEN, LocalDate.now()));
        assertThat(newOrder.getId()).isEqualTo(1L);
    }

    @Test
    void shouldBeThreadSafe() throws InterruptedException {
        // Given
        final int numberOfThreads = 10;
        final int ordersPerThread = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // When - múltiplas threads salvando pedidos concorrentemente
        CompletableFuture<Void>[] futures = new CompletableFuture[numberOfThreads];
        
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            futures[i] = CompletableFuture.runAsync(() -> {
                for (int j = 0; j < ordersPerThread; j++) {
                    Order order = new Order(
                            null, 
                            "Cliente-" + threadId + "-" + j, 
                            new BigDecimal("10.00"), 
                            LocalDate.now()
                    );
                    orderRepository.save(order);
                }
            }, executorService);
        }

        try {
            CompletableFuture.allOf(futures).get();
        } catch (Exception e) {
            throw new RuntimeException("Erro nos testes de concorrência", e);
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        // Then
        List<Order> allOrders = orderRepository.findAll();
        assertThat(allOrders).hasSize(numberOfThreads * ordersPerThread);
        
        // Verifica que todos os IDs são únicos
        List<Long> ids = allOrders.stream().map(Order::getId).sorted().toList();
        for (int i = 0; i < ids.size(); i++) {
            assertThat(ids.get(i)).isEqualTo((long) (i + 1));
        }
    }

    @Test
    void shouldHandleNullIdInSave() {
        // Given
        Order order = new Order();
        order.setId(null);
        order.setCustomerName("Teste");
        order.setTotal(BigDecimal.TEN);
        order.setOrderDate(LocalDate.now());

        // When
        Order savedOrder = orderRepository.save(order);

        // Then
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getId()).isEqualTo(1L);
    }

    @Test
    void shouldHandleZeroIdInSave() {
        // Given
        Order order = new Order();
        order.setId(0L);
        order.setCustomerName("Teste");
        order.setTotal(BigDecimal.TEN);
        order.setOrderDate(LocalDate.now());

        // When
        Order savedOrder = orderRepository.save(order);

        // Then
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getId()).isEqualTo(1L);
    }

    @Test
    void shouldPreserveOrdersOnFind() {
        // Given - salva pedidos em ordem específica
        Order order1 = orderRepository.save(new Order(null, "Primeiro", BigDecimal.TEN, LocalDate.now()));
        Order order2 = orderRepository.save(new Order(null, "Segundo", BigDecimal.ONE, LocalDate.now()));
        Order order3 = orderRepository.save(new Order(null, "Terceiro", new BigDecimal("5.00"), LocalDate.now()));

        // When
        Order found1 = orderRepository.findById(order1.getId()).orElse(null);
        Order found2 = orderRepository.findById(order2.getId()).orElse(null);
        Order found3 = orderRepository.findById(order3.getId()).orElse(null);

        // Then - pedidos encontrados mantêm seus dados
        assertThat(found1).isNotNull();
        assertThat(found1.getCustomerName()).isEqualTo("Primeiro");
        assertThat(found2).isNotNull();
        assertThat(found2.getCustomerName()).isEqualTo("Segundo");
        assertThat(found3).isNotNull();
        assertThat(found3.getCustomerName()).isEqualTo("Terceiro");
    }
}
