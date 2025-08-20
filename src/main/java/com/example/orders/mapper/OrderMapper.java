package com.example.orders.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderResponse;
import com.example.orders.model.Order;

/**
 * Mapper para conversão entre DTOs e entidades
 * Centraliza a lógica de conversão e formatação
 */
@Component
public class OrderMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Converte CreateOrderRequest para Order
     */
    public Order toEntity(CreateOrderRequest request) {
        if (request == null) {
            return null;
        }

        LocalDate orderDate = parseDate(request.getOrderDate());

        return new Order(null, request.getCustomerName(), request.getTotal(), orderDate);
    }

    /**
     * Converte Order para OrderResponse
     */
    public OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }

        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getTotal(),
                order.getOrderDate(),
                order.getStatus());
    }

    /**
     * Converte lista de Orders para lista de OrderResponses
     */
    public List<OrderResponse> toResponseList(List<Order> orders) {
        if (orders == null) {
            return null;
        }

        return orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Converte string de data para LocalDate
     * Lança exceção com mensagem clara em caso de erro
     */
    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida. Use o formato dd-MM-yyyy: " + dateStr, e);
        }
    }
}
