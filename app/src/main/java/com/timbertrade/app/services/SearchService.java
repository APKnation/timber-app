package com.timbertrade.app.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.timbertrade.app.models.Product;
import com.timbertrade.app.models.Auction;
import com.timbertrade.app.models.InventoryItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchService {
    private static final String TAG = "SearchService";
    private static SearchService instance;
    private final FirebaseFirestore db;
    private final ExecutorService executor;
    
    public interface SearchCallback<T> {
        void onSuccess(List<T> results);
        void onError(String error);
    }
    
    public interface CombinedSearchCallback {
        void onSuccess(List<Product> products, List<Auction> auctions);
        void onError(String error);
    }
    
    private SearchService() {
        db = FirebaseFirestore.getInstance();
        executor = Executors.newSingleThreadExecutor();
    }
    
    public static synchronized SearchService getInstance() {
        if (instance == null) {
            instance = new SearchService();
        }
        return instance;
    }
    
    // Search products with advanced filters
    public void searchProducts(SearchFilters filters, SearchCallback<Product> callback) {
        Query productsQuery = db.collection("products")
                .whereEqualTo("status", "AVAILABLE");
        
        // Apply filters
        if (filters.category != null && !filters.category.isEmpty()) {
            productsQuery = productsQuery.whereEqualTo("category", filters.category);
        }
        
        if (filters.location != null && !filters.location.isEmpty()) {
            productsQuery = productsQuery.whereEqualTo("location", filters.location);
        }
        
        if (filters.minPrice >= 0) {
            productsQuery = productsQuery.whereGreaterThanOrEqualTo("price", filters.minPrice);
        }
        
        if (filters.maxPrice > 0) {
            productsQuery = productsQuery.whereLessThanOrEqualTo("price", filters.maxPrice);
        }
        
        if (filters.minQuantity > 0) {
            productsQuery = productsQuery.whereGreaterThanOrEqualTo("quantity", filters.minQuantity);
        }
        
        if (filters.sellerId != null && !filters.sellerId.isEmpty()) {
            productsQuery = productsQuery.whereEqualTo("sellerId", filters.sellerId);
        }
        
        // Apply sorting
        if (filters.sortBy != null) {
            switch (filters.sortBy) {
                case "price_low":
                    productsQuery = productsQuery.orderBy("price", Query.Direction.ASCENDING);
                    break;
                case "price_high":
                    productsQuery = productsQuery.orderBy("price", Query.Direction.DESCENDING);
                    break;
                case "newest":
                    productsQuery = productsQuery.orderBy("postedAt", Query.Direction.DESCENDING);
                    break;
                case "oldest":
                    productsQuery = productsQuery.orderBy("postedAt", Query.Direction.ASCENDING);
                    break;
                case "rating":
                    productsQuery = productsQuery.orderBy("rating", Query.Direction.DESCENDING);
                    break;
                default:
                    productsQuery = productsQuery.orderBy("postedAt", Query.Direction.DESCENDING);
                    break;
            }
        } else {
            productsQuery = productsQuery.orderBy("postedAt", Query.Direction.DESCENDING);
        }
        
        // Apply limit
        if (filters.limit > 0) {
            productsQuery = productsQuery.limit(filters.limit);
        }
        
        productsQuery.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            product.setProductId(doc.getId());
                            
                            // Apply text search filter (client-side)
                            if (filters.query == null || filters.query.isEmpty() || 
                                matchesTextSearch(product, filters.query)) {
                                products.add(product);
                            }
                        }
                    }
                    Log.d(TAG, "Product search found " + products.size() + " results");
                    callback.onSuccess(products);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to search products", e);
                    callback.onError("Failed to search products: " + e.getMessage());
                });
    }
    
    // Search auctions with filters
    public void searchAuctions(SearchFilters filters, SearchCallback<Auction> callback) {
        Query auctionsQuery = db.collection("auctions")
                .whereEqualTo("status", "ACTIVE");
        
        // Apply filters
        if (filters.category != null && !filters.category.isEmpty()) {
            auctionsQuery = auctionsQuery.whereEqualTo("category", filters.category);
        }
        
        if (filters.location != null && !filters.location.isEmpty()) {
            auctionsQuery = auctionsQuery.whereEqualTo("location", filters.location);
        }
        
        if (filters.minPrice >= 0) {
            auctionsQuery = auctionsQuery.whereGreaterThanOrEqualTo("startingPrice", filters.minPrice);
        }
        
        if (filters.maxPrice > 0) {
            auctionsQuery = auctionsQuery.whereLessThanOrEqualTo("startingPrice", filters.maxPrice);
        }
        
        if (filters.sellerId != null && !filters.sellerId.isEmpty()) {
            auctionsQuery = auctionsQuery.whereEqualTo("sellerId", filters.sellerId);
        }
        
        // Apply sorting
        if (filters.sortBy != null) {
            switch (filters.sortBy) {
                case "price_low":
                    auctionsQuery = auctionsQuery.orderBy("currentBid", Query.Direction.ASCENDING);
                    break;
                case "price_high":
                    auctionsQuery = auctionsQuery.orderBy("currentBid", Query.Direction.DESCENDING);
                    break;
                case "ending_soon":
                    auctionsQuery = auctionsQuery.orderBy("endTime", Query.Direction.ASCENDING);
                    break;
                case "newest":
                    auctionsQuery = auctionsQuery.orderBy("createdAt", Query.Direction.DESCENDING);
                    break;
                default:
                    auctionsQuery = auctionsQuery.orderBy("endTime", Query.Direction.ASCENDING);
                    break;
            }
        } else {
            auctionsQuery = auctionsQuery.orderBy("endTime", Query.Direction.ASCENDING);
        }
        
        // Apply limit
        if (filters.limit > 0) {
            auctionsQuery = auctionsQuery.limit(filters.limit);
        }
        
        auctionsQuery.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Auction> auctions = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Auction auction = doc.toObject(Auction.class);
                        if (auction != null) {
                            auction.setAuctionId(doc.getId());
                            
                            // Apply text search filter (client-side)
                            if (filters.query == null || filters.query.isEmpty() || 
                                matchesTextSearch(auction, filters.query)) {
                                auctions.add(auction);
                            }
                        }
                    }
                    Log.d(TAG, "Auction search found " + auctions.size() + " results");
                    callback.onSuccess(auctions);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to search auctions", e);
                    callback.onError("Failed to search auctions: " + e.getMessage());
                });
    }
    
    // Combined search (products + auctions)
    public void searchAll(SearchFilters filters, CombinedSearchCallback callback) {
        searchProducts(filters, new SearchCallback<Product>() {
            @Override
            public void onSuccess(List<Product> products) {
                searchAuctions(filters, new SearchCallback<Auction>() {
                    @Override
                    public void onSuccess(List<Auction> auctions) {
                        callback.onSuccess(products, auctions);
                    }
                    
                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    // Search inventory items
    public void searchInventoryItems(String sellerId, SearchFilters filters, SearchCallback<InventoryItem> callback) {
        Query inventoryQuery = db.collection("inventory")
                .whereEqualTo("sellerId", sellerId);
        
        // Apply filters
        if (filters.category != null && !filters.category.isEmpty()) {
            inventoryQuery = inventoryQuery.whereEqualTo("category", filters.category);
        }
        
        if (filters.location != null && !filters.location.isEmpty()) {
            inventoryQuery = inventoryQuery.whereEqualTo("location", filters.location);
        }
        
        if (filters.minPrice >= 0) {
            inventoryQuery = inventoryQuery.whereGreaterThanOrEqualTo("unitPrice", filters.minPrice);
        }
        
        if (filters.maxPrice > 0) {
            inventoryQuery = inventoryQuery.whereLessThanOrEqualTo("unitPrice", filters.maxPrice);
        }
        
        if (filters.minQuantity > 0) {
            inventoryQuery = inventoryQuery.whereGreaterThanOrEqualTo("quantity", filters.minQuantity);
        }
        
        // Apply sorting
        if (filters.sortBy != null) {
            switch (filters.sortBy) {
                case "price_low":
                    inventoryQuery = inventoryQuery.orderBy("unitPrice", Query.Direction.ASCENDING);
                    break;
                case "price_high":
                    inventoryQuery = inventoryQuery.orderBy("unitPrice", Query.Direction.DESCENDING);
                    break;
                case "quantity_low":
                    inventoryQuery = inventoryQuery.orderBy("quantity", Query.Direction.ASCENDING);
                    break;
                case "quantity_high":
                    inventoryQuery = inventoryQuery.orderBy("quantity", Query.Direction.DESCENDING);
                    break;
                case "newest":
                    inventoryQuery = inventoryQuery.orderBy("createdAt", Query.Direction.DESCENDING);
                    break;
                default:
                    inventoryQuery = inventoryQuery.orderBy("createdAt", Query.Direction.DESCENDING);
                    break;
            }
        } else {
            inventoryQuery = inventoryQuery.orderBy("createdAt", Query.Direction.DESCENDING);
        }
        
        // Apply limit
        if (filters.limit > 0) {
            inventoryQuery = inventoryQuery.limit(filters.limit);
        }
        
        inventoryQuery.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<InventoryItem> items = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        InventoryItem item = doc.toObject(InventoryItem.class);
                        if (item != null) {
                            item.setItemId(doc.getId());
                            
                            // Apply text search filter (client-side)
                            if (filters.query == null || filters.query.isEmpty() || 
                                matchesTextSearch(item, filters.query)) {
                                items.add(item);
                            }
                        }
                    }
                    Log.d(TAG, "Inventory search found " + items.size() + " results");
                    callback.onSuccess(items);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to search inventory", e);
                    callback.onError("Failed to search inventory: " + e.getMessage());
                });
    }
    
    // Get search suggestions
    public void getSearchSuggestions(String query, OnCompleteListener<List<String>> callback) {
        // This would typically use a more sophisticated approach
        // For now, we'll return popular categories and locations
        List<String> suggestions = new ArrayList<>();
        
        // Add common categories
        suggestions.add("Hardwood");
        suggestions.add("Softwood");
        suggestions.add("Exotic");
        suggestions.add("Treated");
        suggestions.add("Rough Sawn");
        suggestions.add("Planed");
        
        // Add common locations (you'd get these from your data)
        suggestions.add("Dar es Salaam");
        suggestions.add("Arusha");
        suggestions.add("Mwanza");
        suggestions.add("Dodoma");
        suggestions.add("Tanga");
        
        // Filter suggestions based on query
        if (query != null && !query.isEmpty()) {
            List<String> filtered = new ArrayList<>();
            for (String suggestion : suggestions) {
                if (suggestion.toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(suggestion);
                }
            }
            callback.onComplete(Tasks.forResult(filtered));
        } else {
            callback.onComplete(Tasks.forResult(suggestions));
        }
    }
    
    // Get popular searches
    public void getPopularSearches(OnCompleteListener<List<String>> callback) {
        List<String> popularSearches = new ArrayList<>();
        popularSearches.add("Hardwood timber");
        popularSearches.add("Pine wood");
        popularSearches.add("Teak furniture");
        popularSearches.add("Construction timber");
        popularSearches.add("Dar es Salaam");
        popularSearches.add("Bulk orders");
        
        callback.onComplete(Tasks.forResult(popularSearches));
    }
    
    // Helper method to check if item matches text search
    private boolean matchesTextSearch(Product product, String query) {
        if (query == null || query.isEmpty()) return true;
        
        String lowerQuery = query.toLowerCase();
        return product.getTitle().toLowerCase().contains(lowerQuery) ||
               product.getDescription().toLowerCase().contains(lowerQuery) ||
               product.getCategory().toString().toLowerCase().contains(lowerQuery) ||
               product.getLocation().toLowerCase().contains(lowerQuery);
    }
    
    private boolean matchesTextSearch(Auction auction, String query) {
        if (query == null || query.isEmpty()) return true;
        
        String lowerQuery = query.toLowerCase();
        return auction.getTitle().toLowerCase().contains(lowerQuery) ||
               auction.getDescription().toLowerCase().contains(lowerQuery) ||
               auction.getCategory().toString().toLowerCase().contains(lowerQuery) ||
               auction.getLocation().toLowerCase().contains(lowerQuery);
    }
    
    private boolean matchesTextSearch(InventoryItem item, String query) {
        if (query == null || query.isEmpty()) return true;
        
        String lowerQuery = query.toLowerCase();
        return item.getProductName().toLowerCase().contains(lowerQuery) ||
               item.getDescription().toLowerCase().contains(lowerQuery) ||
               item.getCategory().toLowerCase().contains(lowerQuery) ||
               item.getLocation().toLowerCase().contains(lowerQuery);
    }
    
    // Search filters class
    public static class SearchFilters {
        public String query;
        public String category;
        public String location;
        public double minPrice = -1;
        public double maxPrice = -1;
        public int minQuantity = -1;
        public String sellerId;
        public String sortBy;
        public int limit = -1;
        
        public SearchFilters() {}
        
        public SearchFilters setQuery(String query) {
            this.query = query;
            return this;
        }
        
        public SearchFilters setCategory(String category) {
            this.category = category;
            return this;
        }
        
        public SearchFilters setLocation(String location) {
            this.location = location;
            return this;
        }
        
        public SearchFilters setPriceRange(double min, double max) {
            this.minPrice = min;
            this.maxPrice = max;
            return this;
        }
        
        public SearchFilters setMinQuantity(int minQuantity) {
            this.minQuantity = minQuantity;
            return this;
        }
        
        public SearchFilters setSellerId(String sellerId) {
            this.sellerId = sellerId;
            return this;
        }
        
        public SearchFilters setSortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }
        
        public SearchFilters setLimit(int limit) {
            this.limit = limit;
            return this;
        }
    }
}
