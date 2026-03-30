package com.timbertrade.app.services;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FirebaseStorageService {
    private static final String TAG = "FirebaseStorageService";
    private static FirebaseStorageService instance;
    private final FirebaseStorage storage;
    private final StorageReference storageRef;
    
    private FirebaseStorageService() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }
    
    public static synchronized FirebaseStorageService getInstance() {
        if (instance == null) {
            instance = new FirebaseStorageService();
        }
        return instance;
    }
    
    public interface ImageUploadCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
        void onProgress(int progress);
    }
    
    public void uploadTimberImage(Context context, Uri imageUri, String productId, ImageUploadCallback callback) {
        try {
            // Create a unique filename
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "timber_images/" + productId + "/" + timestamp + ".jpg";
            
            StorageReference imageRef = storageRef.child(fileName);
            
            Log.d(TAG, "Starting upload: " + fileName);
            
            UploadTask uploadTask = imageRef.putFile(imageUri);
            
            // Listen for upload progress
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                callback.onProgress((int) progress);
            });
            
            // Handle successful upload
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                Log.d(TAG, "Upload successful");
                
                // Get download URL
                imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    String imageUrl = downloadUri.toString();
                    Log.d(TAG, "Image URL: " + imageUrl);
                    callback.onSuccess(imageUrl);
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get download URL", e);
                    callback.onError("Failed to get image URL: " + e.getMessage());
                });
            });
            
            // Handle failed upload
            uploadTask.addOnFailureListener(e -> {
                Log.e(TAG, "Upload failed", e);
                callback.onError("Upload failed: " + e.getMessage());
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error uploading image", e);
            callback.onError("Error uploading image: " + e.getMessage());
        }
    }
    
    public void uploadTimberImageFromPath(Context context, String imagePath, String productId, ImageUploadCallback callback) {
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Uri imageUri = Uri.fromFile(imageFile);
                uploadTimberImage(context, imageUri, productId, callback);
            } else {
                callback.onError("Image file not found");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing image file", e);
            callback.onError("Error processing image: " + e.getMessage());
        }
    }
    
    public void deleteImage(String imageUrl, ImageUploadCallback callback) {
        try {
            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            
            imageRef.delete().addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Image deleted successfully");
                callback.onSuccess(imageUrl);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to delete image", e);
                callback.onError("Failed to delete image: " + e.getMessage());
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error deleting image", e);
            callback.onError("Error deleting image: " + e.getMessage());
        }
    }
}
