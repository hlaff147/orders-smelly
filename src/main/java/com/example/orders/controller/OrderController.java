package com.example.orders.controller;

import com.example.orders.model.ApplyCouponRequest;
import com.example.orders.model.FulfillRequest;
import com.example.orders.model.Order;
import com.example.orders.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// BAD: Controller faz regra de negócio e retorna String com erros 200
@RestController
public class OrderController {

    private OrderService service = new OrderService(); // BAD: sem IoC

    @PostMapping("/makeOrder")
    public String make(@RequestParam String customerName,
            @RequestParam double total,
            @RequestParam String date) {
        if (customerName == null || customerName.isEmpty()) {
            return "error: invalid customer"; // deveria ser 400
        }
        Order o = service.create(customerName, total, date);
        return "ok:" + o.toString();
    }

    @GetMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Order> all() {
        return service.getRepo().findAll(); // vaza entidade
    }

    @PostMapping(value = "/applyCoupon", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String coupon(@RequestBody ApplyCouponRequest req) {
        double t = service.applyCoupon(req.orderId, req.coupon);
        return "newTotal=" + t; // sem formatação
    }

    @PostMapping(value = "/fulfill", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String fulfill(@RequestBody FulfillRequest req) {
        return service.fulfill(req.orderId);
    }
}
