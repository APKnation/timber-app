package com.timbertrade.app.models;

import java.io.Serializable;
import java.util.Date;

public class Bid implements Serializable {
    private String bidId;
    private String auctionId;
    private String bidderId;
    private String bidderName;
    private double bidAmount;
    private Date bidTime;
    
    public Bid() {
        this.bidTime = new Date();
    }
    
    public Bid(String auctionId, String bidderId, String bidderName, double bidAmount) {
        this();
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.bidderName = bidderName;
        this.bidAmount = bidAmount;
    }
    
    // Getters and Setters
    public String getBidId() {
        return bidId;
    }
    
    public void setBidId(String bidId) {
        this.bidId = bidId;
    }
    
    public String getAuctionId() {
        return auctionId;
    }
    
    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
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
    
    public double getBidAmount() {
        return bidAmount;
    }
    
    public void setBidAmount(double bidAmount) {
        this.bidAmount = bidAmount;
    }
    
    public Date getBidTime() {
        return bidTime;
    }
    
    public void setBidTime(Date bidTime) {
        this.bidTime = bidTime;
    }
}
