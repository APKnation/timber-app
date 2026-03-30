package com.timbertrade.app.services;

import android.content.Context;
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
import com.google.firebase.firestore.WriteBatch;
import com.timbertrade.app.models.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductService {
    private static final String TAG = "ProductService";
    private static ProductService instance;
    private final FirebaseFirestore db;
    private final ExecutorService executor;
    
    public interface ProductCallback {
        void onSuccess(List<Product> products);
        void onError(String error);
    }
    
    public interface SingleProductCallback {
        void onSuccess(Product product);
        void onError(String error);
    }
    
    public interface OperationCallback {
        void onSuccess();
        void onError(String error);
    }
    
    private ProductService() {
        db = FirebaseFirestore.getInstance();
        executor = Executors.newSingleThreadExecutor();
    }
    
    public static synchronized ProductService getInstance() {
        if (instance == null) {
            instance = new ProductService();
        }
        return instance;
    }
    
    // Create new product
    public void createProduct(Product product, OperationCallback callback) {
        try {
            // Validate product
            if (!validateProduct(product)) {
                callback.onError("Invalid product data");
                return;
            }
            
            // Set timestamps
            product.setPostedAt(new java.util.Date());
            product.setUpdatedAt(new java.util.Date());
            
            // Add to Firestore
            db.collection("products")
                    .add(product)
                    .addOnSuccessListener(documentReference -> {
                        // Update product with generated ID
                        product.setProductId(documentReference.getId());
                        
                        // Update the document with the ID
                        documentReference.update("productId", documentReference.getId())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Product created successfully: " + documentReference.getId());
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to update product ID", e);
                                    callback.onError("Failed to save product: " + e.getMessage());
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to create product", e);
                        callback.onError("Failed to create product: " + e.getMessage());
                    });
                    
        } catch (Exception e) {
            Log.e(TAG, "Error creating product", e);
            callback.onError("Error creating product: " + e.getMessage());
        }
    }
    
    // Get all products
    public void getAllProducts(ProductCallback callback) {
        db.collection("products")
                .whereEqualTo("status", "AVAILABLE")
                .orderBy("postedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            product.setProductId(doc.getId());
                            products.add(product);
                        }
                    }
                    Log.d(TAG, "Loaded " + products.size() + " products");
                    callback.onSuccess(products);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load products", e);
                    callback.onError("Failed to load products: " + e.getMessage());
                });
    }
    
    // Get products by seller
    public void getProductsBySeller(String sellerId, ProductCallback callback) {
        db.collection("products")
                .whereEqualTo("sellerId", sellerId)
                .orderBy("postedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            product.setProductId(doc.getId());
                            products.add(product);
                        }
                    }
                    Log.d(TAG, "Loaded " + products.size() + " products for seller: " + sellerId);
                    callback.onSuccess(products);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load seller products", e);
                    callback.onError("Failed to load products: " + e.getMessage());
                });
    }
    
    // Get product by ID
    public void getProductById(String productId, SingleProductCallback callback) {
        db.collection("products")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null) {
                            product.setProductId(documentSnapshot.getId());
                            callback.onSuccess(product);
                        } else {
                            callback.onError("Failed to parse product data");
                        }
                    } else {
                        callback.onError("Product not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get product", e);
                    callback.onError("Failed to get product: " + e.getMessage());
                });
    }
    
    // Update product
    public void updateProduct(Product product, OperationCallback callback) {
        try {
            // Validate product
            if (!validateProduct(product)) {
                callback.onError("Invalid product data");
                return;
            }
            
            // Update timestamp
            product.setUpdatedAt(new java.util.Date());
            
            Map<String, Object> updates = new HashMap<>();
            updates.put("title", product.getTitle());
            updates.put("description", product.getDescription());
            updates.put("category", product.getCategory());
            updates.put("price", product.getPrice());
            updates.put("priceUnit", product.getPriceUnit());
            updates.put("quantity", product.getQuantity());
            updates.put("quantityUnit", product.getQuantityUnit());
            updates.put("location", product.getLocation());
            updates.put("imageUrls", product.getImageUrls());
            updates.put("status", product.getStatus());
            updates.put("updatedAt", product.getUpdatedAt());
            
            db.collection("products")
                    .document(product.getProductId())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Product updated successfully: " + product.getProductId());
                        callback.onSuccess();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to update product", e);
                        callback.onError("Failed to update product: " + e.getMessage());
                    });
                    
        } catch (Exception e) {
            Log.e(TAG, "Error updating product", e);
            callback.onError("Error updating product: " + e.getMessage());
        }
    }
    
    // Delete product
    public void deleteProduct(String productId, OperationCallback callback) {
        db.collection("products")
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Product deleted successfully: " + productId);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete product", e);
                    callback.onError("Failed to delete product: " + e.getMessage());
                });
    }
    
    // Search products
    public void searchProducts(String query, String category, String location, double minPrice, double maxPrice, ProductCallback callback) {
        Query productsQuery = db.collection("products")
                .whereEqualTo("status", "AVAILABLE");
        
        // Add filters
        if (category != null && !category.isEmpty()) {
            productsQuery = productsQuery.whereEqualTo("category", category);
        }
        
        if (location != null && !location.isEmpty()) {
            productsQuery = productsQuery.whereEqualTo("location", location);
        }
        
        if (minPrice >= 0) {
            productsQuery = productsQuery.whereGreaterThanOrEqualTo("price", minPrice);
        }
        
        if (maxPrice > 0) {
            productsQuery = productsQuery.whereLessThanOrEqualTo("price", maxPrice);
        }
        
        productsQuery.orderBy("postedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            product.setProductId(doc.getId());
                            
                            // Text search filter (client-side for now)
                            if (query == null || query.isEmpty() || 
                                product.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                                product.getDescription().toLowerCase().contains(query.toLowerCase())) {
                                products.add(product);
                            }
                        }
                    }
                    Log.d(TAG, "Search found " + products.size() + " products");
                    callback.onSuccess(products);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to search products", e);
                    callback.onError("Failed to search products: " + e.getMessage());
                });
    }
    
    // Update product status
    public void updateProductStatus(String productId, Product.ProductStatus status, OperationCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("updatedAt", new java.util.Date());
        
        db.collection("products")
                .document(productId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Product status updated: " + productId + " -> " + status);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update product status", e);
                    callback.onError("Failed to update product status: " + e.getMessage());
                });
    }
    
    // Get featured products
    public void getFeaturedProducts(ProductCallback callback) {
        db.collection("products")
                .whereEqualTo("status", "AVAILABLE")
                .whereEqualTo("isVerified", true)
                .orderBy("rating", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            product.setProductId(doc.getId());
                            products.add(product);
                        }
                    }
                    Log.d(TAG, "Loaded " + products.size() + " featured products");
                    callback.onSuccess(products);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load featured products", e);
                    callback.onError("Failed to load featured products: " + e.getMessage());
                });
    }
    
    // Get products by category
    public void getProductsByCategory(String category, ProductCallback callback) {
        db.collection("products")
                .whereEqualTo("category", category)
                .whereEqualTo("status", "AVAILABLE")
                .orderBy("postedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            product.setProductId(doc.getId());
                            products.add(product);
                        }
                    }
                    Log.d(TAG, "Loaded " + products.size() + " products in category: " + category);
                    callback.onSuccess(products);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load products by category", e);
                    callback.onError("Failed to load products: " + e.getMessage());
                });
    }
    
    // Validate product data
    private boolean validateProduct(Product product) {
        if (product == null) return false;
        
        if (product.getTitle() == null || product.getTitle().trim().isEmpty()) return false;
        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) return false;
        if (product.getPrice() <= 0) return false;
        if (product.getQuantity() <= 0) return false;
        if (product.getSellerId() == null || product.getSellerId().trim().isEmpty()) return false;
        if (product.getCategory() == null) return false;
        
        return true;
    }
    
    // Get product statistics
    public void getProductStatistics(String sellerId, OnCompleteListener<Map<String, Object>> callback) {
        db.collection("products")
                .whereEqualTo("sellerId", sellerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Object> stats = new HashMap<>();
                    int totalProducts = queryDocumentSnapshots.size();
                    int availableProducts = 0;
                    int soldProducts = 0;
                    double totalValue = 0;
                    
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            if (product.getStatus() == Product.ProductStatus.AVAILABLE) {
                                availableProducts++;
                            } else if (product.getStatus() == Product.ProductStatus.SOLD) {
                                soldProducts++;
                            }
                            totalValue += product.getPrice() * product.getQuantity();
                        }
                    }
                    
                    stats.put("totalProducts", totalProducts);
                    stats.put("availableProducts", availableProducts);
                    stats.put("soldProducts", soldProducts);
                    stats.put("totalValue", totalValue);
                    
                    callback.onComplete(Tasks.forResult(stats));
                })
                .addOnFailureListener(e -> {
                    callback.onComplete(Tasks.forException(e));
                });
    }
}
