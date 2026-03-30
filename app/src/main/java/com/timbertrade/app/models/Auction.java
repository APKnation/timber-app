package com.timbertrade.app.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity(tableName = "models")
public class Auction implements Serializable {
    private String auctionId;
    private String productId;
    private Product product;
    private String sellerId;
    private String sellerName;
    private String title;
    private String description;
    private String location;
    private String category;
    private double reservePrice;
    private double startingPrice;
    private double minBidIncrement;
    private double currentBid;
    private String currentBidderId;
    private String currentBidderName;
    private Date startTime;
    private Date endTime;
    private Date actualEndTime;
    private AuctionStatus status;
    private List<Bid> bidHistory;
    private int bidCount;
    private Date createdAt;
    
    public enum AuctionStatus {
        UPCOMING("Upcoming"),
        ACTIVE("Active"),
        ENDED("Ended"),
        CANCELLED("Cancelled");
        
        private final String displayName;
        
        AuctionStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public static class Bid {
        private String bidId;
        private String bidderId;
        private String bidderName;
        private double amount;
        private Date timestamp;
        
        public Bid() {
            // Default constructor
        }
        
        public Bid(String bidId, String bidderId, String bidderName, double amount) {
            this.bidId = bidId;
            this.bidderId = bidderId;
            this.bidderName = bidderName;
            this.amount = amount;
            this.timestamp = new Date();
        }
        
        // Getters and Setters
        public String getBidId() {
            return bidId;
        }
        
        public void setBidId(String bidId) {
            this.bidId = bidId;
        }
        
        public String getBidderId() {
            return bidderId;
        }
        
        public void setBidderId(String bidderId) {
            this.bidderId = bidderId;
        }
        
        public String getBidderName() {
            return bidderName;
        }
        
        public void setBidderName(String bidderName) {
            this.bidderName = bidderName;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public void setAmount(double amount) {
            this.amount = amount;
        }
        
        public Date getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }
    }
    
    public Auction() {
        // Default constructor
    }
    
    public Auction(String auctionId, String productId, Product product, String sellerId, 
                  String sellerName, double reservePrice, Date startTime, Date endTime) {
        this.auctionId = auctionId;
        this.productId = productId;
        this.product = product;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.reservePrice = reservePrice;
        this.currentBid = 0.0;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AuctionStatus.UPCOMING;
        this.bidCount = 0;
        this.createdAt = new Date();
    }
    
    // Getters and Setters
    public String getAuctionId() {
        return auctionId;
    }
    
    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
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
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public double getReservePrice() {
        return reservePrice;
    }
    
    public void setReservePrice(double reservePrice) {
        this.reservePrice = reservePrice;
    }
    
    public double getStartingPrice() {
        return startingPrice;
    }
    
    public void setStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
    }
    
    public double getMinBidIncrement() {
        return minBidIncrement;
    }
    
    public void setMinBidIncrement(double minBidIncrement) {
        this.minBidIncrement = minBidIncrement;
    }
    
    public void setReservePrice(double reservePrice) {
        this.reservePrice = reservePrice;
    }
    
    public double getCurrentBid() {
        return currentBid;
    }
    
    public void setCurrentBid(double currentBid) {
        this.currentBid = currentBid;
    }
    
    public String getCurrentBidderId() {
        return currentBidderId;
    }
    
    public void setCurrentBidderId(String currentBidderId) {
        this.currentBidderId = currentBidderId;
    }
    
    public String getCurrentBidderName() {
        return currentBidderName;
    }
    
    public void setCurrentBidderName(String currentBidderName) {
        this.currentBidderName = currentBidderName;
    }
    
    public Date getStartTime() {
        return startTime;
    }
    
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    public Date getEndTime() {
        return endTime;
    }
    
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    
    public AuctionStatus getStatus() {
        return status;
    }
    
    public void setStatus(AuctionStatus status) {
        this.status = status;
    }
    
    public List<Bid> getBidHistory() {
        return bidHistory;
    }
    
    public void setBidHistory(List<Bid> bidHistory) {
        this.bidHistory = bidHistory;
    }
    
    public int getBidCount() {
        return bidCount;
    }
    
    public void setBidCount(int bidCount) {
        this.bidCount = bidCount;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getActualEndTime() {
        return actualEndTime;
    }
    
    public void setActualEndTime(Date actualEndTime) {
        this.actualEndTime = actualEndTime;
    }
}
