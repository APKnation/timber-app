package com.timbertrade.app.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "orders")
public class Order implements Serializable {
    @PrimaryKey
    @NonNull
    private String orderId;
    private String productId;
    private String buyerId;
    private String sellerId;
    private String customerName;
    private String woodType;
    private int quantity;
    private double price;
    private double totalPrice;
    private OrderStatus status;
    private String orderDate;
    private String deliveryDate;
    private String notes;
    private Date createdAt;
    private Date updatedAt;
    private Date confirmedAt;
    private Date shippedAt;
    private Date deliveredAt;
    private Date cancelledAt;
    private String cancelReason;
    private String trackingNumber;
    
    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }
    
    public Order() {
        this.orderId = "ORD_" + System.currentTimeMillis();
        this.status = OrderStatus.PENDING;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.orderDate = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
    }
    
    public Order(String customerName, String woodType, int quantity, double price, String deliveryDate, String notes) {
        this();
        this.customerName = customerName;
        this.woodType = woodType;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = price * quantity;
        this.deliveryDate = deliveryDate;
        this.notes = notes;
    }
    
    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getBuyerId() {
        return buyerId;
    }
    
    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }
    
    public String getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getWoodType() {
        return woodType;
    }
    
    public void setWoodType(String woodType) {
        this.woodType = woodType;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public String getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
    
    public String getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Date getConfirmedAt() {
        return confirmedAt;
    }
    
    public void setConfirmedAt(Date confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
    
    public Date getShippedAt() {
        return shippedAt;
    }
    
    public void setShippedAt(Date shippedAt) {
        this.shippedAt = shippedAt;
    }
    
    public Date getDeliveredAt() {
        return deliveredAt;
    }
    
    public void setDeliveredAt(Date deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    
    public Date getCancelledAt() {
        return cancelledAt;
    }
    
    public void setCancelledAt(Date cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
    
    public String getCancelReason() {
        return cancelReason;
    }
    
    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
    
    public String getTrackingNumber() {
        return trackingNumber;
    }
    
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    
    // Legacy compatibility
    public String getId() {
        return orderId;
    }
    
    public void setId(String id) {
        this.orderId = id;
    }
}
