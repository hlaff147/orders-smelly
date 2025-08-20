package com.example.orders.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.example.orders.model.Order;

/**
 * Repository refatorado para ser thread-safe e usar Spring
 * - ConcurrentHashMap para thread-safety
 * - AtomicLong para sequência thread-safe
 * - Anotação @Repository para injeção de dependência
 */
@Repository
public class OrderRepository {

    // Corrigido: ConcurrentHashMap para thread-safety
    private final Map<Long, Order> database = new ConcurrentHashMap<>();

    // Corrigido: AtomicLong para sequência thread-safe
    private final AtomicLong sequence = new AtomicLong(1);

    /**
     * Salva um pedido no repositório
     * Gera ID automaticamente se for um novo pedido
     */
    public Order save(Order order) {
        if (order.getId() == null || order.getId() == 0L) {
            order.setId(sequence.getAndIncrement());
        }
        database.put(order.getId(), order);
        return order;
    }

    /**
     * Busca pedido por ID
     */
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    /**
     * Retorna todos os pedidos
     */
    public List<Order> findAll() {
        return new ArrayList<>(database.values());
    }

    /**
     * Limpa todos os pedidos (útil para testes)
     */
    public void clear() {
        database.clear();
        sequence.set(1);
    }

    /**
     * Verifica se existe pedido com o ID
     */
    public boolean existsById(Long id) {
        return database.containsKey(id);
    }
}
