package com.timbertrade.app.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "models")
public class Payment implements Serializable {
    private String paymentId;
    private String transactionId;
    private String userId;
    private String userName;
    private String recipientId;
    private String recipientName;
    private double amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentType paymentType;
    private PaymentStatus status;
    private String description;
    private String relatedProductId;
    private String relatedAuctionId;
    private String phoneNumber; // For mobile money
    private String referenceNumber;
    private Date processedAt;
    private Date createdAt;
    private String failureReason;
    
    public enum PaymentMethod {
        MPESA("M-Pesa"),
        AIRTEL_MONEY("Airtel Money"),
        TIGO_PESA("Tigo Pesa"),
        BANK_TRANSFER("Bank Transfer"),
        CASH("Cash");
        
        private final String displayName;
        
        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum PaymentType {
        PRODUCT_PURCHASE("Product Purchase"),
        AUCTION_PAYMENT("Auction Payment"),
        SERVICE_FEE("Service Fee"),
        VERIFICATION_FEE("Verification Fee"),
        REFUND("Refund");
        
        private final String displayName;
        
        PaymentType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum PaymentStatus {
        PENDING("Pending"),
        PROCESSING("Processing"),
        COMPLETED("Completed"),
        FAILED("Failed"),
        CANCELLED("Cancelled"),
        REFUNDED("Refunded");
        
        private final String displayName;
        
        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public Payment() {
        // Default constructor
    }
    
    public Payment(String paymentId, String userId, String userName, String recipientId, 
                  String recipientName, double amount, PaymentMethod paymentMethod, 
                  PaymentType paymentType, String description) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.userName = userName;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.amount = amount;
        this.currency = "TZS"; // Tanzanian Shillings
        this.paymentMethod = paymentMethod;
        this.paymentType = paymentType;
        this.status = PaymentStatus.PENDING;
        this.description = description;
        this.createdAt = new Date();
    }
    
    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getRecipientId() {
        return recipientId;
    }
    
    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
    
    public String getRecipientName() {
        return recipientName;
    }
    
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public PaymentType getPaymentType() {
        return paymentType;
    }
    
    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getRelatedProductId() {
        return relatedProductId;
    }
    
    public void setRelatedProductId(String relatedProductId) {
        this.relatedProductId = relatedProductId;
    }
    
    public String getRelatedAuctionId() {
        return relatedAuctionId;
    }
    
    public void setRelatedAuctionId(String relatedAuctionId) {
        this.relatedAuctionId = relatedAuctionId;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    public Date getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getFailureReason() {
        return failureReason;
    }
    
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
