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
import com.timbertrade.app.models.Auction;
import com.timbertrade.app.models.Bid;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuctionService {
    private static final String TAG = "AuctionService";
    private static AuctionService instance;
    private final FirebaseFirestore db;
    private final ExecutorService executor;
    
    public interface AuctionCallback {
        void onSuccess(List<Auction> auctions);
        void onError(String error);
    }
    
    public interface SingleAuctionCallback {
        void onSuccess(Auction auction);
        void onError(String error);
    }
    
    public interface BidCallback {
        void onSuccess(Bid bid);
        void onError(String error);
    }
    
    public interface OperationCallback {
        void onSuccess();
        void onError(String error);
    }
    
    private AuctionService() {
        db = FirebaseFirestore.getInstance();
        executor = Executors.newSingleThreadExecutor();
    }
    
    public static synchronized AuctionService getInstance() {
        if (instance == null) {
            instance = new AuctionService();
        }
        return instance;
    }
    
    // Create new auction
    public void createAuction(Auction auction, OperationCallback callback) {
        try {
            // Validate auction
            if (!validateAuction(auction)) {
                callback.onError("Invalid auction data");
                return;
            }
            
            // Set timestamps
            auction.setCreatedAt(new Date());
            auction.setUpdatedAt(new Date());
            
            // Add to Firestore
            db.collection("auctions")
                    .add(auction)
                    .addOnSuccessListener(documentReference -> {
                        // Update auction with generated ID
                        auction.setAuctionId(documentReference.getId());
                        
                        // Update the document with the ID
                        documentReference.update("auctionId", documentReference.getId())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Auction created successfully: " + documentReference.getId());
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to update auction ID", e);
                                    callback.onError("Failed to save auction: " + e.getMessage());
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to create auction", e);
                        callback.onError("Failed to create auction: " + e.getMessage());
                    });
                    
        } catch (Exception e) {
            Log.e(TAG, "Error creating auction", e);
            callback.onError("Error creating auction: " + e.getMessage());
        }
    }
    
    // Get all auctions
    public void getAllAuctions(AuctionCallback callback) {
        db.collection("auctions")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Auction> auctions = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Auction auction = doc.toObject(Auction.class);
                        if (auction != null) {
                            auction.setAuctionId(doc.getId());
                            auctions.add(auction);
                        }
                    }
                    Log.d(TAG, "Loaded " + auctions.size() + " auctions");
                    callback.onSuccess(auctions);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load auctions", e);
                    callback.onError("Failed to load auctions: " + e.getMessage());
                });
    }
    
    // Get active auctions
    public void getActiveAuctions(AuctionCallback callback) {
        db.collection("auctions")
                .whereEqualTo("status", "ACTIVE")
                .whereGreaterThanOrEqualTo("endTime", new Date())
                .orderBy("endTime", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Auction> auctions = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Auction auction = doc.toObject(Auction.class);
                        if (auction != null) {
                            auction.setAuctionId(doc.getId());
                            auctions.add(auction);
                        }
                    }
                    Log.d(TAG, "Loaded " + auctions.size() + " active auctions");
                    callback.onSuccess(auctions);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load active auctions", e);
                    callback.onError("Failed to load auctions: " + e.getMessage());
                });
    }
    
    // Get auction by ID
    public void getAuctionById(String auctionId, SingleAuctionCallback callback) {
        db.collection("auctions")
                .document(auctionId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Auction auction = documentSnapshot.toObject(Auction.class);
                        if (auction != null) {
                            auction.setAuctionId(documentSnapshot.getId());
                            callback.onSuccess(auction);
                        } else {
                            callback.onError("Failed to parse auction data");
                        }
                    } else {
                        callback.onError("Auction not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get auction", e);
                    callback.onError("Failed to get auction: " + e.getMessage());
                });
    }
    
    // Place bid (with transaction for safety)
    public void placeBid(String auctionId, String bidderId, String bidderName, double bidAmount, BidCallback callback) {
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) {
                try {
                    // Get auction document
                    DocumentSnapshot auctionDoc = transaction.get(db.collection("auctions").document(auctionId));
                    if (!auctionDoc.exists()) {
                        throw new RuntimeException("Auction not found");
                    }
                    
                    Auction auction = auctionDoc.toObject(Auction.class);
                    auction.setAuctionId(auctionId);
                    
                    // Validate auction state
                    if (auction.getStatus() != Auction.AuctionStatus.ACTIVE) {
                        throw new RuntimeException("Auction is not active");
                    }
                    
                    if (new Date().after(auction.getEndTime())) {
                        throw new RuntimeException("Auction has ended");
                    }
                    
                    // Validate bid amount
                    if (bidAmount <= auction.getCurrentBid() + auction.getMinBidIncrement()) {
                        throw new RuntimeException("Bid amount too low. Minimum bid: " + 
                                (auction.getCurrentBid() + auction.getMinBidIncrement()));
                    }
                
                // Check if bidder is not the seller
                if (bidderId.equals(auction.getSellerId())) {
                    throw new RuntimeException("Seller cannot bid on their own auction");
                }
                
                // Create bid
                Bid bid = new Bid();
                bid.setBidId(db.collection("auctions").document(auctionId).collection("bids").document().getId());
                bid.setAuctionId(auctionId);
                bid.setBidderId(bidderId);
                bid.setBidderName(bidderName);
                bid.setBidAmount(bidAmount);
                bid.setBidTime(new Date());
                
                // Update auction with new bid
                Map<String, Object> auctionUpdates = new HashMap<>();
                auctionUpdates.put("currentBid", bidAmount);
                auctionUpdates.put("currentBidderId", bidderId);
                auctionUpdates.put("currentBidderName", bidderName);
                auctionUpdates.put("bidCount", auction.getBidCount() + 1);
                auctionUpdates.put("updatedAt", new Date());
                
                transaction.update(db.collection("auctions").document(auctionId), auctionUpdates);
                
                // Add bid to subcollection
                transaction.set(db.collection("auctions").document(auctionId).collection("bids").document(bid.getBidId()), bid);
                
                return null;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).addOnSuccessListener(aVoid -> {
            // Get the created bid to return
            db.collection("auctions").document(auctionId).collection("bids")
                    .orderBy("bidTime", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Bid bid = queryDocumentSnapshots.getDocuments().get(0).toObject(Bid.class);
                            if (bid != null) {
                                bid.setBidId(queryDocumentSnapshots.getDocuments().get(0).getId());
                                Log.d(TAG, "Bid placed successfully: " + bid.getBidId());
                                callback.onSuccess(bid);
                            } else {
                                callback.onError("Failed to parse bid data");
                            }
                        } else {
                            callback.onError("Failed to retrieve bid");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to get bid", e);
                        callback.onError("Failed to retrieve bid: " + e.getMessage());
                    });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to place bid", e);
            callback.onError("Failed to place bid: " + e.getMessage());
        });
    }
    
    // Get bids for auction
    public void getAuctionBids(String auctionId, AuctionService.BidCallback callback) {
        db.collection("auctions").document(auctionId).collection("bids")
                .orderBy("bidAmount", Query.Direction.DESCENDING)
                .orderBy("bidTime", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Bid> bids = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Bid bid = doc.toObject(Bid.class);
                        if (bid != null) {
                            bid.setBidId(doc.getId());
                            bids.add(bid);
                        }
                    }
                    Log.d(TAG, "Loaded " + bids.size() + " bids for auction: " + auctionId);
                    
                    // Return the highest bid as success
                    if (!bids.isEmpty()) {
                        callback.onSuccess(bids.get(0));
                    } else {
                        callback.onError("No bids found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load auction bids", e);
                    callback.onError("Failed to load bids: " + e.getMessage());
                });
    }
    
    // Get user's bids
    public void getUserBids(String userId, AuctionCallback callback) {
        db.collection("auctions")
                .whereEqualTo("currentBidderId", userId)
                .whereEqualTo("status", "ACTIVE")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Auction> auctions = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Auction auction = doc.toObject(Auction.class);
                        if (auction != null) {
                            auction.setAuctionId(doc.getId());
                            auctions.add(auction);
                        }
                    }
                    Log.d(TAG, "Loaded " + auctions.size() + " auctions with user bids");
                    callback.onSuccess(auctions);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load user bids", e);
                    callback.onError("Failed to load bids: " + e.getMessage());
                });
    }
    
    // End auction and determine winner
    public void endAuction(String auctionId, OperationCallback callback) {
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) {
                try {
                    DocumentSnapshot auctionDoc = transaction.get(db.collection("auctions").document(auctionId));
                    if (!auctionDoc.exists()) {
                        throw new RuntimeException("Auction not found");
                    }
                    
                    Auction auction = auctionDoc.toObject(Auction.class);
                    auction.setAuctionId(auctionId);
                    
                    if (auction.getStatus() != Auction.AuctionStatus.ACTIVE) {
                        throw new RuntimeException("Auction is not active");
                    }
                    
                    // Update auction status
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("status", Auction.AuctionStatus.ENDED);
                    updates.put("actualEndTime", new Date());
                    updates.put("updatedAt", new Date());
                    
                    transaction.update(db.collection("auctions").document(auctionId), updates);
                    
                    return null;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Auction ended successfully: " + auctionId);
            callback.onSuccess();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to end auction", e);
            callback.onError("Failed to end auction: " + e.getMessage());
        });
    }
    
    // Get auctions by seller
    public void getAuctionsBySeller(String sellerId, AuctionCallback callback) {
        db.collection("auctions")
                .whereEqualTo("sellerId", sellerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Auction> auctions = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Auction auction = doc.toObject(Auction.class);
                        if (auction != null) {
                            auction.setAuctionId(doc.getId());
                            auctions.add(auction);
                        }
                    }
                    Log.d(TAG, "Loaded " + auctions.size() + " auctions for seller: " + sellerId);
                    callback.onSuccess(auctions);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to load seller auctions", e);
                    callback.onError("Failed to load auctions: " + e.getMessage());
                });
    }
    
    // Update auction status
    public void updateAuctionStatus(String auctionId, Auction.AuctionStatus status, OperationCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("updatedAt", new Date());
        
        if (status == Auction.AuctionStatus.ENDED) {
            updates.put("actualEndTime", new Date());
        }
        
        db.collection("auctions")
                .document(auctionId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Auction status updated: " + auctionId + " -> " + status);
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update auction status", e);
                    callback.onError("Failed to update auction status: " + e.getMessage());
                });
    }
    
    // Validate auction data
    private boolean validateAuction(Auction auction) {
        if (auction == null) return false;
        
        if (auction.getTitle() == null || auction.getTitle().trim().isEmpty()) return false;
        if (auction.getDescription() == null || auction.getDescription().trim().isEmpty()) return false;
        if (auction.getStartingPrice() <= 0) return false;
        if (auction.getMinBidIncrement() <= 0) return false;
        if (auction.getSellerId() == null || auction.getSellerId().trim().isEmpty()) return false;
        if (auction.getEndTime() == null || auction.getEndTime().before(new Date())) return false;
        
        return true;
    }
    
    // Get auction statistics
    public void getAuctionStatistics(String sellerId, OnCompleteListener<Map<String, Object>> callback) {
        db.collection("auctions")
                .whereEqualTo("sellerId", sellerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Map<String, Object> stats = new HashMap<>();
                    int totalAuctions = queryDocumentSnapshots.size();
                    int activeAuctions = 0;
                    int endedAuctions = 0;
                    double totalValue = 0;
                    
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Auction auction = doc.toObject(Auction.class);
                        if (auction != null) {
                            if (auction.getStatus() == Auction.AuctionStatus.ACTIVE) {
                                activeAuctions++;
                            } else if (auction.getStatus() == Auction.AuctionStatus.ENDED) {
                                endedAuctions++;
                            }
                            totalValue += auction.getCurrentBid();
                        }
                    }
                    
                    stats.put("totalAuctions", totalAuctions);
                    stats.put("activeAuctions", activeAuctions);
                    stats.put("endedAuctions", endedAuctions);
                    stats.put("totalValue", totalValue);
                    
                    callback.onComplete(Tasks.forResult(stats));
                })
                .addOnFailureListener(e -> {
                    callback.onComplete(Tasks.forException(e));
                });
    }
}
