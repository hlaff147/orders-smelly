package com.example.orders.service;

import com.example.orders.model.Order;
import com.example.orders.repository.OrderRepository;
import com.example.orders.util.LegacyFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

// BAD: sem @Service, sem interface, formatação estática insegura
public class OrderService {

    private final OrderRepository repo = new OrderRepository();
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MM-yyyy"); // BAD: não thread-safe

    public Order create(String customerName, double total, String dateStr) {
        System.out.println("Creating order " + customerName + " total " + total + " on " + dateStr);
        // BAD: sem validação
        try {
            Date d = SDF.parse(dateStr); // só pra 'validar' a data
            Thread.sleep(40); // BAD: simular IO
        } catch (ParseException | InterruptedException e) {
            // engole a exceção
        }
        Order o = new Order(0, customerName, total, dateStr);
        return repo.save(o);
    }

    public double applyCoupon(int orderId, String coupon) {
        Optional<Order> opt = repo.findById(orderId);
        Order o = opt.get(); // BAD: get() direto
        // BAD: cupons hardcoded e regras burras
        if (coupon != null && coupon.startsWith("OFF")) {
            try {
                int off = Integer.parseInt(coupon.replace("OFF", ""));
                double newTotal = o.getTotal() - (o.getTotal() * off / 100.0);
                o.setTotal(newTotal);
            } catch (Exception e) {
                o.setTotal(o.getTotal() - 1.0); // gambiarra
            }
        }
        repo.save(o);
        return o.getTotal();
    }

    public String fulfill(int orderId) {
        Order o = repo.findById(orderId).get();
        if (o.getTotal() <= 0) {
            // OK, libera grátis?
            o.setStatus("FULFILLED");
        } else {
            // BAD: sem pagamento real, muda status direto
            o.setStatus("FULFILLED");
        }
        repo.save(o);
        return LegacyFormat.money(o.getTotal()) + " | " + o.getStatus();
    }

    public OrderRepository getRepo() { return repo; }
}

