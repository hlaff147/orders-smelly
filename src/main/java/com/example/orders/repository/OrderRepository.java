package com.example.orders.repository;

import com.example.orders.model.Order;
import java.util.*;

// BAD: estado estático global e não thread-safe
public class OrderRepository {
    private static final Map<Integer, Order> DB = new HashMap<>();
    private static int SEQ = 1;

    public Order save(Order o) {
        if (o.getId() == 0) {
            o.setId(SEQ++);
        }
        DB.put(o.getId(), o);
        return o;
    }

    public Optional<Order> findById(int id) {
        return Optional.ofNullable(DB.get(id));
    }

    public List<Order> findAll() {
        return new ArrayList<>(DB.values());
    }

    public void clear() { DB.clear(); }
}
