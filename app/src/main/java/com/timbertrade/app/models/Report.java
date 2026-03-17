package com.timbertrade.app.models;

import java.io.Serializable;

public class Report implements Serializable {
    private String id;
    private String title;
    private String reportType;
    private String generatedDate;
    private String period;
    private double totalRevenue;
    private int totalOrders;
    private String topProduct;
    private String summary;
    private String details;
    
    public Report() {
        this.id = "RPT_" + System.currentTimeMillis();
        this.generatedDate = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
    }
    
    public Report(String title, String reportType, String period, double totalRevenue, int totalOrders, String topProduct, String summary, String details) {
        this();
        this.title = title;
        this.reportType = reportType;
        this.period = period;
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.topProduct = topProduct;
        this.summary = summary;
        this.details = details;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    
    public String getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(String generatedDate) { this.generatedDate = generatedDate; }
    
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
    
    public String getTopProduct() { return topProduct; }
    public void setTopProduct(String topProduct) { this.topProduct = topProduct; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
