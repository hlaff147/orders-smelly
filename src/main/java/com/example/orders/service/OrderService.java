package com.example.orders.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderResponse;
import com.example.orders.mapper.OrderMapper;
import com.example.orders.model.Order;
import com.example.orders.model.OrderStatus;
import com.example.orders.repository.OrderRepository;
import com.example.orders.util.LegacyFormat;

/**
 * Service refatorado com todas as boas práticas:
 * - Anotação @Service para injeção de dependência
 * - Logging apropriado ao invés de System.out.println
 * - Validações adequadas
 * - Tratamento de erros com exceções específicas
 * - Uso de DTOs ao invés de expor entidades
 */
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final LegacyFormat legacyFormat;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, LegacyFormat legacyFormat) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.legacyFormat = legacyFormat;
    }

    /**
     * Cria um novo pedido com validações apropriadas
     */
    public OrderResponse createOrder(CreateOrderRequest request) {
        logger.info("Criando pedido para cliente: {}, total: {}",
                request.getCustomerName(), request.getTotal());

        // Validações já são feitas pelas anotações do DTO via @Valid
        Order order = orderMapper.toEntity(request);
        Order savedOrder = orderRepository.save(order);

        logger.info("Pedido criado com ID: {}", savedOrder.getId());
        return orderMapper.toResponse(savedOrder);
    }

    /**
     * Aplica cupom de desconto ao pedido
     */
    public BigDecimal applyCoupon(Long orderId, String coupon) {
        logger.info("Aplicando cupom '{}' ao pedido ID: {}", coupon, orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com ID: " + orderId));

        BigDecimal discount = calculateDiscount(order.getTotal(), coupon);
        BigDecimal newTotal = order.getTotal().subtract(discount);

        // Garante que o total não seja negativo
        if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
            newTotal = BigDecimal.ZERO;
        }

        order.setTotal(newTotal);
        orderRepository.save(order);

        logger.info("Cupom aplicado. Novo total: {}", newTotal);
        return newTotal;
    }

    /**
     * Calcula desconto baseado no cupom
     * Refatorado para ser mais flexível e testável
     */
    private BigDecimal calculateDiscount(BigDecimal total, String coupon) {
        if (coupon == null || coupon.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        try {
            if (coupon.startsWith("OFF")) {
                int percentage = Integer.parseInt(coupon.replace("OFF", ""));
                if (percentage < 0 || percentage > 100) {
                    throw new IllegalArgumentException("Percentual de desconto inválido: " + percentage);
                }
                return total.multiply(BigDecimal.valueOf(percentage)).divide(BigDecimal.valueOf(100));
            } else if (coupon.startsWith("VALOR")) {
                BigDecimal fixedDiscount = new BigDecimal(coupon.replace("VALOR", ""));
                return fixedDiscount.min(total); // Não pode ser maior que o total
            }
        } catch (NumberFormatException e) {
            logger.warn("Cupom inválido: {}", coupon);
            throw new IllegalArgumentException("Formato de cupom inválido: " + coupon);
        }

        throw new IllegalArgumentException("Tipo de cupom não reconhecido: " + coupon);
    }

    /**
     * Processa entrega do pedido
     */
    public String fulfillOrder(Long orderId) {
        logger.info("Processando entrega do pedido ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com ID: " + orderId));

        // Validação de negócio: só entrega se estiver pago ou grátis
        if (order.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            logger.info("Pedido gratuito, liberando entrega diretamente");
            order.setStatus(OrderStatus.FULFILLED);
        } else if (order.getStatus() == OrderStatus.PAID) {
            logger.info("Pedido pago, processando entrega");
            order.setStatus(OrderStatus.FULFILLED);
        } else {
            throw new IllegalStateException(
                    "Pedido deve estar pago antes da entrega. Status atual: " + order.getStatus());
        }

        orderRepository.save(order);

        String formattedTotal = legacyFormat.formatMoney(order.getTotal());
        String result = formattedTotal + " | " + order.getStatus().getDescription();

        logger.info("Entrega processada para pedido {}: {}", orderId, result);
        return result;
    }

    /**
     * Retorna todos os pedidos
     */
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orderMapper.toResponseList(orders);
    }

    /**
     * Busca pedido por ID
     */
    public Optional<OrderResponse> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toResponse);
    }
}
