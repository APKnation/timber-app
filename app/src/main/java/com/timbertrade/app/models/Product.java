package com.timbertrade.app.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Product implements Serializable {
    private String productId;
    private String sellerId;
    private String sellerName;
    private String title;
    private String description;
    private TimberCategory category;
    private double price;
    private String priceUnit; // per cubic meter, per ton, etc.
    private int quantity;
    private String quantityUnit;
    private String location;
    private List<String> imageUrls;
    private ProductStatus status;
    private boolean isVerified;
    private Date postedAt;
    private Date updatedAt;
    private double rating;
    private int reviewCount;
    
    public enum TimberCategory {
        MAHOGANY("Mahogany", "#8B4513"),
        TEAK("Teak", "#D2691E"),
        PINE("Pine", "#228B22"),
        OAK("Oak", "#8B4513"),
        CEDAR("Cedar", "#DEB887");
        
        private final String displayName;
        private final String color;
        
        TimberCategory(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getColor() {
            return color;
        }
    }
    
    public enum ProductStatus {
        AVAILABLE("Available"),
        SOLD("Sold"),
        RESERVED("Reserved"),
        IN_AUCTION("In Auction"),
        PENDING_VERIFICATION("Pending Verification");
        
        private final String displayName;
        
        ProductStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public Product() {
        // Default constructor
    }
    
    public Product(String productId, String sellerId, String sellerName, String title, 
                  String description, TimberCategory category, double price, String priceUnit,
                  int quantity, String quantityUnit, String location) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.priceUnit = priceUnit;
        this.quantity = quantity;
        this.quantityUnit = quantityUnit;
        this.location = location;
        this.status = ProductStatus.PENDING_VERIFICATION;
        this.isVerified = false;
        this.postedAt = new Date();
        this.updatedAt = new Date();
        this.rating = 0.0;
        this.reviewCount = 0;
    }
    
    // Getters and Setters
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
    
    public String getSellerName() {
        return sellerName;
    }
    
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public TimberCategory getCategory() {
        return category;
    }
    
    public void setCategory(TimberCategory category) {
        this.category = category;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getPriceUnit() {
        return priceUnit;
    }
    
    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getQuantityUnit() {
        return quantityUnit;
    }
    
    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public List<String> getImageUrls() {
        return imageUrls;
    }
    
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
    
    public ProductStatus getStatus() {
        return status;
    }
    
    public void setStatus(ProductStatus status) {
        this.status = status;
    }
    
    public boolean isVerified() {
        return isVerified;
    }
    
    public void setVerified(boolean verified) {
        isVerified = verified;
    }
    
    public Date getPostedAt() {
        return postedAt;
    }
    
    public void setPostedAt(Date postedAt) {
        this.postedAt = postedAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public double getRating() {
        return rating;
    }
    
    public void setRating(double rating) {
        this.rating = rating;
    }
    
    public int getReviewCount() {
        return reviewCount;
    }
    
    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}
