package com.timbertrade.app.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "inventory")
public class InventoryItem implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    private String woodType;
    private String description;
    private int quantity;
    private double pricePerUnit;
    private String supplier;
    private String lastUpdated;
    private String location;
    private String category;
    
    public InventoryItem() {
        this.id = "INV_" + System.currentTimeMillis();
        this.lastUpdated = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
    }
    
    public InventoryItem(String woodType, String description, int quantity, double pricePerUnit, String supplier, String location, String category) {
        this();
        this.woodType = woodType;
        this.description = description;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.supplier = supplier;
        this.location = location;
        this.category = category;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getWoodType() { return woodType; }
    public void setWoodType(String woodType) { this.woodType = woodType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit = pricePerUnit; }
    
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    
    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getProductName() { return woodType; }
    
    public void setItemId(String id) { this.id = id; }
    
    public double getUnitPrice() { return pricePerUnit; }
    
    public void setCreatedAt(Date date) { this.lastUpdated = java.text.DateFormat.getDateTimeInstance().format(date); }
    
    public void setUpdatedAt(Date date) { this.lastUpdated = java.text.DateFormat.getDateTimeInstance().format(date); }
    
    public String getSellerId() { return supplier; } // Using supplier field as sellerId for compatibility
    
    public String getUpdatedAt() { return lastUpdated; }
    
    public String getItemId() { return id; }
    
    public double getTotalValue() {
        return quantity * pricePerUnit;
    }
}
