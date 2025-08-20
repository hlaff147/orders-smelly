package com.example.orders.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.orders.dto.ApiResponse;
import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderResponse;
import com.example.orders.model.ApplyCouponRequest;
import com.example.orders.model.FulfillRequest;
import com.example.orders.service.OrderService;

import jakarta.validation.Valid;

/**
 * Controller refatorado com boas práticas:
 * - Injeção de dependência correta
 * - Responses HTTP apropriados (não apenas 200)
 * - Uso de DTOs
 * - Separação de responsabilidades
 * - Tratamento de erros adequado
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Cria um novo pedido
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            OrderResponse order = orderService.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Pedido criado com sucesso", order));
        } catch (IllegalArgumentException e) {
            logger.warn("Erro na criação do pedido: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro interno na criação do pedido", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erro interno do servidor"));
        }
    }

    /**
     * Lista todos os pedidos
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        try {
            List<OrderResponse> orders = orderService.getAllOrders();
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (Exception e) {
            logger.error("Erro ao buscar pedidos", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erro interno do servidor"));
        }
    }

    /**
     * Busca pedido por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        try {
            Optional<OrderResponse> order = orderService.getOrderById(id);
            if (order.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(order.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar pedido ID: {}", id, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erro interno do servidor"));
        }
    }

    /**
     * Aplica cupom de desconto
     */
    @PostMapping("/apply-coupon")
    public ResponseEntity<ApiResponse<BigDecimal>> applyCoupon(@Valid @RequestBody ApplyCouponRequest request) {
        try {
            BigDecimal newTotal = orderService.applyCoupon(request.getOrderId(), request.getCoupon());
            return ResponseEntity.ok(ApiResponse.success("Cupom aplicado com sucesso", newTotal));
        } catch (IllegalArgumentException e) {
            logger.warn("Erro na aplicação do cupom: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro interno na aplicação do cupom", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erro interno do servidor"));
        }
    }

    /**
     * Processa entrega do pedido
     */
    @PostMapping("/fulfill")
    public ResponseEntity<ApiResponse<String>> fulfillOrder(@Valid @RequestBody FulfillRequest request) {
        try {
            String result = orderService.fulfillOrder(request.getOrderId());
            return ResponseEntity.ok(ApiResponse.success("Entrega processada com sucesso", result));
        } catch (IllegalArgumentException e) {
            logger.warn("Erro no processamento da entrega: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            logger.warn("Estado inválido para entrega: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro interno no processamento da entrega", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Erro interno do servidor"));
        }
    }
}
