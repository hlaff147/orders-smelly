package com.example.orders.model;

public class ApplyCouponRequest {
    public Integer orderId; // BAD: públicos
    public String coupon;
}