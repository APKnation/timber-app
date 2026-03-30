package com.timbertrade.app.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.timbertrade.app.models.Order;
import com.timbertrade.app.models.Product;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderService {
    private static final String TAG = "OrderService";
    private static OrderService instance;
    private final FirebaseFirestore db;
    private final ExecutorService executor;
    
    public interface OrderCallback {
        void onSuccess(List<Order> orders);
        void onError(String error);
    }
    
    public interface SingleOrderCallback {
        void onSuccess(Order order);
        void onError(String error);
    }
    
    public interface OperationCallback {
        void onSuccess();
        void onError(String error);
    }
    
    private OrderService() {
        db = FirebaseFirestore.getInstance();
        executor = Executors.newSingleThreadExecutor();
    }
    
    public static synchronized OrderService getInstance() {
        if (instance == null) {
            instance = new OrderService();
        }
        return instance;
    }
    
    // Create new order
    public void createOrder(Order order, OperationCallback callback) {
        try {
            // Validate order
            if (!validateOrder(order)) {
                callback.onError("Invalid order data");
                return;
            }
            
            // Set timestamps
            order.setCreatedAt(new Date());
            order.setUpdatedAt(new Date());
            
            // Generate order ID
            order.setOrderId(generateOrderId());
            
            // Add to Firestore with transaction to update product quantity
            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) {
                    try {
                        // Get product document
                        DocumentSnapshot productDoc = transaction.get(db.collection("products").document(order.getProductId()));
                        if (!productDoc.exists()) {
                            throw new RuntimeException("Product not found");
                        }
                        
                        Product product = productDoc.toObject(Product.class);
                        if (product == null) {
                            throw new RuntimeException("Failed to parse product data");
                        }
                    
                    // Check product availability
                    if (product.getStatus() != Product.ProductStatus.AVAILABLE) {
                        throw new RuntimeException("Product is not available");
                    }
                    
                    // Check if enough quantity is available
                    if (product.getQuantity() < order.getQuantity()) {
                        throw new RuntimeException("Not enough quantity available. Available: " + product.getQuantity());
                    }
                    
                    // Update product quantity
                    Map<String, Object> productUpdates = new HashMap<>();
                    productUpdates.put("quantity", product.getQuantity() - order.getQuantity());
                    productUpdates.put("updatedAt", new Date());
                    
                    // If quantity becomes 0, mark as sold
                    if (product.getQuantity() - order.getQuantity() == 0) {
                        productUpdates.put("status", Product.ProductStatus.SOLD);
                    }
                    
                    transaction.update(db.collection("products").document(order.getProductId()), productUpdates);
                    
                    // Create order
                    transaction.set(db.collection("orders").document(order.getOrderId()), order);
                    
                    return null;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }).addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Order created successfully: " + order.getOrderId());
                callback.onSuccess();
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to create order", e);
                callback.onError("Failed to create order: " + e.getMessage());
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating order", e);
            callback.onError("Error creating order: " + e.getMessage());
        }
    }
    
    // Get all orders
    public void getAllOrders(OrderCallback callback) {
        db.collection("orders")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Order> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Order order = doc.toObject(Order.class);
                        if (order != null) {
                            order.setOrderId(doc.getId());
                            orders.add(order);
                        }
                    }
                    Log.d(TAG, "Loaded " + orders.size() + " orders");
                    callback.onSuccess(orders);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load orders", e);
                    callback.onError("Failed to load orders: " + e.getMessage());
                });
    }
    
    // Get orders by buyer
    public void getOrdersByBuyer(String buyerId, OrderCallback callback) {
        db.collection("orders")
                .whereEqualTo("buyerId", buyerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Order> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Order order = doc.toObject(Order.class);
                        if (order != null) {
                            order.setOrderId(doc.getId());
                            orders.add(order);
                        }
                    }
                    Log.d(TAG, "Loaded " + orders.size() + " orders for buyer: " + buyerId);
                    callback.onSuccess(orders);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load buyer orders", e);
                    callback.onError("Failed to load orders: " + e.getMessage());
                });
    }
    
    // Get orders by seller
    public void getOrdersBySeller(String sellerId, OrderCallback callback) {
        db.collection("orders")
                .whereEqualTo("sellerId", sellerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Order> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Order order = doc.toObject(Order.class);
                        if (order != null) {
                            order.setOrderId(doc.getId());
                            orders.add(order);
                        }
                    }
                    Log.d(TAG, "Loaded " + orders.size() + " orders for seller: " + sellerId);
                    callback.onSuccess(orders);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load seller orders", e);
                    callback.onError("Failed to load orders: " + e.getMessage());
                });
    }
    
    // Get order by ID
    public void getOrderById(String orderId, SingleOrderCallback callback) {
        db.collection("orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Order order = documentSnapshot.toObject(Order.class);
                        if (order != null) {
                            order.setOrderId(documentSnapshot.getId());
                            callback.onSuccess(order);
                        } else {
                            callback.onError("Failed to parse order data");
                        }
                    } else {
                        callback.onError("Order not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get order", e);
                    callback.onError("Failed to get order: " + e.getMessage());
                });
    }
    
    // Update order status
    public void updateOrderStatus(String orderId, Order.OrderStatus status, OperationCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("updatedAt", new Date());
        
        // Add status-specific timestamps
        if (status == Order.OrderStatus.CONFIRMED) {
            updates.put("confirmedAt", new Date());
        } else if (status == Order.OrderStatus.SHIPPED) {
            updates.put("shippedAt", new Date());
        } else if (status == Order.OrderStatus.DELIVERED) {
            updates.put("deliveredAt", new Date());
        } else if (status == Order.OrderStatus.CANCELLED) {
            updates.put("cancelledAt", new Date());
            updates.put("cancelReason", "Cancelled by user");
        }
        
        db.collection("orders")
                .document(orderId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Order status updated: " + orderId + " -> " + status);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update order status", e);
                    callback.onError("Failed to update order status: " + e.getMessage());
                });
    }
    
    // Cancel order (with product quantity restoration)
    public void cancelOrder(String orderId, String reason, OperationCallback callback) {
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) {
                try {
                    // Get order document
                    DocumentSnapshot orderDoc = transaction.get(db.collection("orders").document(orderId));
                    if (!orderDoc.exists()) {
                        throw new RuntimeException("Order not found");
                    }
                    
                    Order order = orderDoc.toObject(Order.class);
                    if (order == null) {
                        throw new RuntimeException("Failed to parse order data");
                    }
                    
                    // Check if order can be cancelled
                    if (order.getStatus() == Order.OrderStatus.DELIVERED || 
                        order.getStatus() == Order.OrderStatus.CANCELLED) {
                        throw new RuntimeException("Order cannot be cancelled");
                    }
                    
                    // Get product document
                    DocumentSnapshot productDoc = transaction.get(db.collection("products").document(order.getProductId()));
                    if (!productDoc.exists()) {
                        throw new RuntimeException("Product not found");
                    }
                    
                    Product product = productDoc.toObject(Product.class);
                    if (product == null) {
                        throw new RuntimeException("Failed to parse product data");
                    }
                
                // Restore product quantity
                Map<String, Object> productUpdates = new HashMap<>();
                productUpdates.put("quantity", product.getQuantity() + order.getQuantity());
                productUpdates.put("status", Product.ProductStatus.AVAILABLE);
                productUpdates.put("updatedAt", new Date());
                
                transaction.update(db.collection("products").document(order.getProductId()), productUpdates);
                
                // Update order status
                Map<String, Object> orderUpdates = new HashMap<>();
                orderUpdates.put("status", Order.OrderStatus.CANCELLED);
                orderUpdates.put("cancelReason", reason);
                orderUpdates.put("cancelledAt", new Date());
                orderUpdates.put("updatedAt", new Date());
                
                transaction.update(db.collection("orders").document(orderId), orderUpdates);
                
                return null;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Order cancelled successfully: " + orderId);
            callback.onSuccess();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to cancel order", e);
            callback.onError("Failed to cancel order: " + e.getMessage());
        });
    }
    
    // Confirm order
    public void confirmOrder(String orderId, OperationCallback callback) {
        updateOrderStatus(orderId, Order.OrderStatus.CONFIRMED, callback);
    }
    
    // Mark order as shipped
    public void shipOrder(String orderId, String trackingNumber, OperationCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", Order.OrderStatus.SHIPPED);
        updates.put("trackingNumber", trackingNumber);
        updates.put("shippedAt", new Date());
        updates.put("updatedAt", new Date());
        
        db.collection("orders")
                .document(orderId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Order shipped: " + orderId);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to ship order", e);
                    callback.onError("Failed to ship order: " + e.getMessage());
                });
    }
    
    // Mark order as delivered
    public void deliverOrder(String orderId, OperationCallback callback) {
        updateOrderStatus(orderId, Order.OrderStatus.DELIVERED, callback);
    }
    
    // Get pending orders for seller
    public void getPendingOrders(String sellerId, OrderCallback callback) {
        db.collection("orders")
                .whereEqualTo("sellerId", sellerId)
                .whereEqualTo("status", Order.OrderStatus.PENDING)
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Order> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Order order = doc.toObject(Order.class);
                        if (order != null) {
                            order.setOrderId(doc.getId());
                            orders.add(order);
                        }
                    }
                    Log.d(TAG, "Loaded " + orders.size() + " pending orders for seller: " + sellerId);
                    callback.onSuccess(orders);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load pending orders", e);
                    callback.onError("Failed to load orders: " + e.getMessage());
                });
    }
    
    // Get order statistics
    public void getOrderStatistics(String userId, boolean isSeller, OnCompleteListener<Map<String, Object>> callback) {
        String field = isSeller ? "sellerId" : "buyerId";
        
        db.collection("orders")
                .whereEqualTo(field, userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Object> stats = new HashMap<>();
                    int totalOrders = queryDocumentSnapshots.size();
                    int pendingOrders = 0;
                    int confirmedOrders = 0;
                    int shippedOrders = 0;
                    int deliveredOrders = 0;
                    int cancelledOrders = 0;
                    double totalValue = 0;
                    
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Order order = doc.toObject(Order.class);
                        if (order != null) {
                            switch (order.getStatus()) {
                                case PENDING:
                                    pendingOrders++;
                                    break;
                                case CONFIRMED:
                                    confirmedOrders++;
                                    break;
                                case SHIPPED:
                                    shippedOrders++;
                                    break;
                                case DELIVERED:
                                    deliveredOrders++;
                                    totalValue += order.getTotalPrice();
                                    break;
                                case CANCELLED:
                                    cancelledOrders++;
                                    break;
                            }
                        }
                    }
                    
                    stats.put("totalOrders", totalOrders);
                    stats.put("pendingOrders", pendingOrders);
                    stats.put("confirmedOrders", confirmedOrders);
                    stats.put("shippedOrders", shippedOrders);
                    stats.put("deliveredOrders", deliveredOrders);
                    stats.put("cancelledOrders", cancelledOrders);
                    stats.put("totalValue", totalValue);
                    
                    callback.onComplete(Tasks.forResult(stats));
                })
                .addOnFailureListener(e -> {
                    callback.onComplete(Tasks.forException(e));
                });
    }
    
    // Generate unique order ID
    private String generateOrderId() {
        return "ORD-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
    
    // Validate order data
    private boolean validateOrder(Order order) {
        if (order == null) return false;
        
        if (order.getProductId() == null || order.getProductId().trim().isEmpty()) return false;
        if (order.getBuyerId() == null || order.getBuyerId().trim().isEmpty()) return false;
        if (order.getSellerId() == null || order.getSellerId().trim().isEmpty()) return false;
        if (order.getQuantity() <= 0) return false;
        if (order.getPrice() <= 0) return false;
        if (order.getCustomerName() == null || order.getCustomerName().trim().isEmpty()) return false;
        if (order.getDeliveryDate() == null) return false;
        
        return true;
    }
}
