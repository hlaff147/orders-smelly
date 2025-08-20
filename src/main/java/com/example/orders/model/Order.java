package com.example.orders.model;

public class Order {
    private int id;
    private String customerName;
    private double total; // BAD money
    private String date; // BAD date handling
    private String status = "NEW"; // NEW, PAID, FULFILLED

    public Order(){}

    public Order(int id, String customerName, double total, String date) {
        this.id = id;
        this.customerName = customerName;
        this.total = total;
        this.date = date;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Order{id=" + id + ", customer='" + customerName + "', total=" + total + ", date='" + date + "', status='" + status + "'}";
    }
}
