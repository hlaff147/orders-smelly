package com.example.orders.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO refatorado com encapsulamento correto e validações
 */
public class ApplyCouponRequest {

    @NotNull(message = "ID do pedido é obrigatório")
    @Positive(message = "ID do pedido deve ser positivo")
    private Long orderId;

    @NotNull(message = "Cupom é obrigatório")
    private String coupon;

    public ApplyCouponRequest() {
    }

    public ApplyCouponRequest(Long orderId, String coupon) {
        this.orderId = orderId;
        this.coupon = coupon;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }
}