package com.timbertrade.app.models;

import java.io.Serializable;

public class Order implements Serializable {
    private String id;
    private String customerName;
    private String woodType;
    private int quantity;
    private double price;
    private String status;
    private String orderDate;
    private String deliveryDate;
    private String notes;
    
    public Order() {
        this.id = "ORD_" + System.currentTimeMillis();
        this.orderDate = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
        this.status = "Pending";
    }
    
    public Order(String customerName, String woodType, int quantity, double price, String deliveryDate, String notes) {
        this();
        this.customerName = customerName;
        this.woodType = woodType;
        this.quantity = quantity;
        this.price = price;
        this.deliveryDate = deliveryDate;
        this.notes = notes;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getWoodType() { return woodType; }
    public void setWoodType(String woodType) { this.woodType = woodType; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    
    public String getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public double getTotalPrice() {
        return quantity * price;
    }
}
