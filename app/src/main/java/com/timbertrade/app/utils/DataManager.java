package com.timbertrade.app.utils;

import com.timbertrade.app.models.Order;
import com.timbertrade.app.models.InventoryItem;
import com.timbertrade.app.models.Report;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private List<Order> orders;
    private List<InventoryItem> inventoryItems;
    private List<Report> reports;
    
    private DataManager() {
        orders = new ArrayList<>();
        inventoryItems = new ArrayList<>();
        reports = new ArrayList<>();
        initializeDemoData();
    }
    
    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    private void initializeDemoData() {
        // Initialize demo orders
        orders.add(new Order("John Smith", "Oak Wood", 50, 45.50, "2024-03-15", "Premium quality oak"));
        orders.add(new Order("Mary Johnson", "Pine Wood", 100, 25.75, "2024-03-18", "Standard pine"));
        orders.add(new Order("Bob Wilson", "Mahogany", 25, 85.00, "2024-03-20", "High-end mahogany"));
        
        // Initialize demo inventory
        inventoryItems.add(new InventoryItem("Oak Wood", "Premium hardwood", 500, 45.50, "Timber Suppliers Ltd", "Warehouse A", "Hardwood"));
        inventoryItems.add(new InventoryItem("Pine Wood", "Softwood for construction", 1200, 25.75, "Forest Products Inc", "Warehouse B", "Softwood"));
        inventoryItems.add(new InventoryItem("Mahogany", "Luxury hardwood", 150, 85.00, "Exotic Woods Co", "Warehouse C", "Hardwood"));
        inventoryItems.add(new InventoryItem("Cedar", "Aromatic wood", 300, 35.25, "Mountain Timber", "Warehouse A", "Softwood"));
        
        // Initialize demo reports
        reports.add(new Report("Monthly Sales Report", "Sales", "March 2024", 15450.75, 45, "Oak Wood", "Strong sales performance this month with oak wood leading sales.", "Detailed breakdown of all sales transactions..."));
        reports.add(new Report("Inventory Status Report", "Inventory", "March 2024", 0, 0, "Pine Wood", "Current inventory levels are healthy across all categories.", "Complete inventory analysis..."));
        reports.add(new Report("Revenue Analysis", "Financial", "Q1 2024", 45675.25, 135, "Oak Wood", "Revenue growth of 15% compared to last quarter.", "Comprehensive financial analysis..."));
    }
    
    // Order CRUD operations
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }
    
    public Order getOrderById(String id) {
        for (Order order : orders) {
            if (order.getId().equals(id)) {
                return order;
            }
        }
        return null;
    }
    
    public void addOrder(Order order) {
        orders.add(order);
    }
    
    public void updateOrder(Order updatedOrder) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId().equals(updatedOrder.getId())) {
                orders.set(i, updatedOrder);
                break;
            }
        }
    }
    
    public void deleteOrder(String id) {
        orders.removeIf(order -> order.getId().equals(id));
    }
    
    // Inventory CRUD operations
    public List<InventoryItem> getAllInventoryItems() {
        return new ArrayList<>(inventoryItems);
    }
    
    public InventoryItem getInventoryItemById(String id) {
        for (InventoryItem item : inventoryItems) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }
    
    public void addInventoryItem(InventoryItem item) {
        inventoryItems.add(item);
    }
    
    public void updateInventoryItem(InventoryItem updatedItem) {
        for (int i = 0; i < inventoryItems.size(); i++) {
            if (inventoryItems.get(i).getId().equals(updatedItem.getId())) {
                inventoryItems.set(i, updatedItem);
                break;
            }
        }
    }
    
    public void deleteInventoryItem(String id) {
        inventoryItems.removeIf(item -> item.getId().equals(id));
    }
    
    // Report CRUD operations
    public List<Report> getAllReports() {
        return new ArrayList<>(reports);
    }
    
    public Report getReportById(String id) {
        for (Report report : reports) {
            if (report.getId().equals(id)) {
                return report;
            }
        }
        return null;
    }
    
    public void addReport(Report report) {
        reports.add(report);
    }
    
    public void updateReport(Report updatedReport) {
        for (int i = 0; i < reports.size(); i++) {
            if (reports.get(i).getId().equals(updatedReport.getId())) {
                reports.set(i, updatedReport);
                break;
            }
        }
    }
    
    public void deleteReport(String id) {
        reports.removeIf(report -> report.getId().equals(id));
    }
}
