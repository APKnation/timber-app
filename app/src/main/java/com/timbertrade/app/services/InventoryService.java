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
import com.timbertrade.app.models.InventoryItem;
import com.timbertrade.app.models.Product;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InventoryService {
    private static final String TAG = "InventoryService";
    private static InventoryService instance;
    private final FirebaseFirestore db;
    private final ExecutorService executor;
    
    public interface InventoryCallback {
        void onSuccess(List<InventoryItem> items);
        void onError(String error);
    }
    
    public interface SingleInventoryCallback {
        void onSuccess(InventoryItem item);
        void onError(String error);
    }
    
    public interface OperationCallback {
        void onSuccess();
        void onError(String error);
    }
    
    private InventoryService() {
        db = FirebaseFirestore.getInstance();
        executor = Executors.newSingleThreadExecutor();
    }
    
    public static synchronized InventoryService getInstance() {
        if (instance == null) {
            instance = new InventoryService();
        }
        return instance;
    }
    
    // Create inventory item
    public void createInventoryItem(InventoryItem item, OperationCallback callback) {
        try {
            // Validate item
            if (!validateInventoryItem(item)) {
                callback.onError("Invalid inventory item data");
                return;
            }
            
            // Set timestamps
            item.setCreatedAt(new Date());
            item.setUpdatedAt(new Date());
            
            // Add to Firestore
            db.collection("inventory")
                    .add(item)
                    .addOnSuccessListener(documentReference -> {
                        // Update item with generated ID
                        item.setItemId(documentReference.getId());
                        
                        // Update the document with the ID
                        documentReference.update("itemId", documentReference.getId())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Inventory item created successfully: " + documentReference.getId());
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to update inventory item ID", e);
                                    callback.onError("Failed to save inventory item: " + e.getMessage());
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to create inventory item", e);
                        callback.onError("Failed to create inventory item: " + e.getMessage());
                    });
                    
        } catch (Exception e) {
            Log.e(TAG, "Error creating inventory item", e);
            callback.onError("Error creating inventory item: " + e.getMessage());
        }
    }
    
    // Get all inventory items
    public void getAllInventoryItems(InventoryCallback callback) {
        db.collection("inventory")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<InventoryItem> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        InventoryItem item = doc.toObject(InventoryItem.class);
                        if (item != null) {
                            item.setItemId(doc.getId());
                            items.add(item);
                        }
                    }
                    Log.d(TAG, "Loaded " + items.size() + " inventory items");
                    callback.onSuccess(items);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load inventory items", e);
                    callback.onError("Failed to load inventory items: " + e.getMessage());
                });
    }
    
    // Get inventory items by seller
    public void getInventoryBySeller(String sellerId, InventoryCallback callback) {
        db.collection("inventory")
                .whereEqualTo("sellerId", sellerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<InventoryItem> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        InventoryItem item = doc.toObject(InventoryItem.class);
                        if (item != null) {
                            item.setItemId(doc.getId());
                            items.add(item);
                        }
                    }
                    Log.d(TAG, "Loaded " + items.size() + " inventory items for seller: " + sellerId);
                    callback.onSuccess(items);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load seller inventory", e);
                    callback.onError("Failed to load inventory items: " + e.getMessage());
                });
    }
    
    // Get inventory item by ID
    public void getInventoryItemById(String itemId, SingleInventoryCallback callback) {
        db.collection("inventory")
                .document(itemId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        InventoryItem item = documentSnapshot.toObject(InventoryItem.class);
                        if (item != null) {
                            item.setItemId(documentSnapshot.getId());
                            callback.onSuccess(item);
                        } else {
                            callback.onError("Failed to parse inventory item data");
                        }
                    } else {
                        callback.onError("Inventory item not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get inventory item", e);
                    callback.onError("Failed to get inventory item: " + e.getMessage());
                });
    }
    
    // Update inventory item
    public void updateInventoryItem(InventoryItem item, OperationCallback callback) {
        try {
            // Validate item
            if (!validateInventoryItem(item)) {
                callback.onError("Invalid inventory item data");
                return;
            }
            
            // Update timestamp
            item.setUpdatedAt(new Date());
            
            Map<String, Object> updates = new HashMap<>();
            updates.put("productName", item.getProductName());
            updates.put("category", item.getCategory());
            updates.put("quantity", item.getQuantity());
            updates.put("unitPrice", item.getUnitPrice());
            updates.put("location", item.getLocation());
            updates.put("supplier", item.getSupplier());
            updates.put("description", item.getDescription());
            updates.put("updatedAt", item.getUpdatedAt());
            
            db.collection("inventory")
                    .document(item.getItemId())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Inventory item updated successfully: " + item.getItemId());
                        callback.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to update inventory item", e);
                        callback.onError("Failed to update inventory item: " + e.getMessage());
                    });
                    
        } catch (Exception e) {
            Log.e(TAG, "Error updating inventory item", e);
            callback.onError("Error updating inventory item: " + e.getMessage());
        }
    }
    
    // Add stock to inventory item
    public void addStock(String itemId, int quantity, String reason, OperationCallback callback) {
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws Exception {
                DocumentSnapshot itemDoc = transaction.get(db.collection("inventory").document(itemId));
                if (!itemDoc.exists()) {
                    throw new Exception("Inventory item not found");
                }
                
                InventoryItem item = itemDoc.toObject(InventoryItem.class);
                if (item == null) {
                    throw new Exception("Failed to parse inventory item data");
                }
                
                // Update quantity
                Map<String, Object> updates = new HashMap<>();
                updates.put("quantity", item.getQuantity() + quantity);
                updates.put("updatedAt", new Date());
                
                transaction.update(db.collection("inventory").document(itemId), updates);
                
                // Add stock movement record
                Map<String, Object> movement = new HashMap<>();
                movement.put("itemId", itemId);
                movement.put("type", "ADD");
                movement.put("quantity", quantity);
                movement.put("reason", reason);
                movement.put("timestamp", new Date());
                movement.put("previousQuantity", item.getQuantity());
                movement.put("newQuantity", item.getQuantity() + quantity);
                
                transaction.set(db.collection("inventory").document(itemId).collection("movements").document(), movement);
                
                return null;
            }
        }).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Stock added successfully: " + itemId + " +" + quantity);
            callback.onSuccess();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to add stock", e);
            callback.onError("Failed to add stock: " + e.getMessage());
        });
    }
    
    // Remove stock from inventory item
    public void removeStock(String itemId, int quantity, String reason, OperationCallback callback) {
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws Exception {
                DocumentSnapshot itemDoc = transaction.get(db.collection("inventory").document(itemId));
                if (!itemDoc.exists()) {
                    throw new Exception("Inventory item not found");
                }
                
                InventoryItem item = itemDoc.toObject(InventoryItem.class);
                if (item == null) {
                    throw new Exception("Failed to parse inventory item data");
                }
                
                // Check if enough stock is available
                if (item.getQuantity() < quantity) {
                    throw new Exception("Insufficient stock. Available: " + item.getQuantity());
                }
                
                // Update quantity
                Map<String, Object> updates = new HashMap<>();
                updates.put("quantity", item.getQuantity() - quantity);
                updates.put("updatedAt", new Date());
                
                transaction.update(db.collection("inventory").document(itemId), updates);
                
                // Add stock movement record
                Map<String, Object> movement = new HashMap<>();
                movement.put("itemId", itemId);
                movement.put("type", "REMOVE");
                movement.put("quantity", quantity);
                movement.put("reason", reason);
                movement.put("timestamp", new Date());
                movement.put("previousQuantity", item.getQuantity());
                movement.put("newQuantity", item.getQuantity() - quantity);
                
                transaction.set(db.collection("inventory").document(itemId).collection("movements").document(), movement);
                
                return null;
            }
        }).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Stock removed successfully: " + itemId + " -" + quantity);
            callback.onSuccess();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to remove stock", e);
            callback.onError("Failed to remove stock: " + e.getMessage());
        });
    }
    
    // Delete inventory item
    public void deleteInventoryItem(String itemId, OperationCallback callback) {
        db.collection("inventory")
                .document(itemId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Inventory item deleted successfully: " + itemId);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete inventory item", e);
                    callback.onError("Failed to delete inventory item: " + e.getMessage());
                });
    }
    
    // Get low stock items
    public void getLowStockItems(String sellerId, int threshold, InventoryCallback callback) {
        db.collection("inventory")
                .whereEqualTo("sellerId", sellerId)
                .whereLessThan("quantity", threshold)
                .orderBy("quantity", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<InventoryItem> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        InventoryItem item = doc.toObject(InventoryItem.class);
                        if (item != null) {
                            item.setItemId(doc.getId());
                            items.add(item);
                        }
                    }
                    Log.d(TAG, "Loaded " + items.size() + " low stock items for seller: " + sellerId);
                    callback.onSuccess(items);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load low stock items", e);
                    callback.onError("Failed to load inventory items: " + e.getMessage());
                });
    }
    
    // Get stock movements for item
    public void getStockMovements(String itemId, OnCompleteListener<List<Map<String, Object>>> callback) {
        db.collection("inventory").document(itemId).collection("movements")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> movements = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        movements.add(doc.getData());
                    }
                    Log.d(TAG, "Loaded " + movements.size() + " stock movements for item: " + itemId);
                    callback.onComplete(Tasks.forResult(movements));
                })
                .addOnFailureListener(e -> {
                    callback.onComplete(Tasks.forException(e));
                });
    }
    
    // Get inventory statistics
    public void getInventoryStatistics(String sellerId, OnCompleteListener<Map<String, Object>> callback) {
        db.collection("inventory")
                .whereEqualTo("sellerId", sellerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Object> stats = new HashMap<>();
                    int totalItems = queryDocumentSnapshots.size();
                    int totalQuantity = 0;
                    double totalValue = 0;
                    int lowStockCount = 0;
                    final int LOW_STOCK_THRESHOLD = 10;
                    
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        InventoryItem item = doc.toObject(InventoryItem.class);
                        if (item != null) {
                            totalQuantity += item.getQuantity();
                            totalValue += item.getQuantity() * item.getUnitPrice();
                            if (item.getQuantity() < LOW_STOCK_THRESHOLD) {
                                lowStockCount++;
                            }
                        }
                    }
                    
                    stats.put("totalItems", totalItems);
                    stats.put("totalQuantity", totalQuantity);
                    stats.put("totalValue", totalValue);
                    stats.put("lowStockCount", lowStockCount);
                    
                    callback.onComplete(Tasks.forResult(stats));
                })
                .addOnFailureListener(e -> {
                    callback.onComplete(Tasks.forException(e));
                });
    }
    
    // Search inventory items
    public void searchInventoryItems(String sellerId, String query, String category, InventoryCallback callback) {
        Query inventoryQuery = db.collection("inventory")
                .whereEqualTo("sellerId", sellerId);
        
        // Add category filter
        if (category != null && !category.isEmpty()) {
            inventoryQuery = inventoryQuery.whereEqualTo("category", category);
        }
        
        inventoryQuery.orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<InventoryItem> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        InventoryItem item = doc.toObject(InventoryItem.class);
                        if (item != null) {
                            item.setItemId(doc.getId());
                            
                            // Text search filter (client-side for now)
                            if (query == null || query.isEmpty() || 
                                item.getProductName().toLowerCase().contains(query.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(query.toLowerCase())) {
                                items.add(item);
                            }
                        }
                    }
                    Log.d(TAG, "Search found " + items.size() + " inventory items");
                    callback.onSuccess(items);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to search inventory items", e);
                    callback.onError("Failed to search inventory items: " + e.getMessage());
                });
    }
    
    // Validate inventory item data
    private boolean validateInventoryItem(InventoryItem item) {
        if (item == null) return false;
        
        if (item.getProductName() == null || item.getProductName().trim().isEmpty()) return false;
        if (item.getCategory() == null || item.getCategory().trim().isEmpty()) return false;
        if (item.getQuantity() < 0) return false;
        if (item.getUnitPrice() < 0) return false;
        if (item.getSellerId() == null || item.getSellerId().trim().isEmpty()) return false;
        
        return true;
    }
}
