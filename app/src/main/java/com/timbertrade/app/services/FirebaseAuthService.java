package com.timbertrade.app.services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.timbertrade.app.models.User;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAuthService {
    private static final String TAG = "FirebaseAuthService";
    private static FirebaseAuthService instance;
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private Context context;
    
    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String error);
    }
    
    public interface EmailVerificationCallback {
        void onSuccess();
        void onError(String error);
    }
    
    public interface PasswordResetCallback {
        void onSuccess();
        void onError(String error);
    }
    
    private FirebaseAuthService() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
    
    public static synchronized FirebaseAuthService getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthService();
        }
        return instance;
    }
    
    public void setContext(Context context) {
        this.context = context;
    }
    
    // Check if user is currently logged in
    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }
    
    // Get current user
    public FirebaseUser getCurrentFirebaseUser() {
        return mAuth.getCurrentUser();
    }
    
    // Get current user profile
    public void getCurrentUser(AuthCallback callback) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            callback.onError("No user logged in");
            return;
        }
        
        // Get user profile from Firestore
        db.collection("users").document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            callback.onSuccess(user);
                        } else {
                            callback.onError("Failed to parse user data");
                        }
                    } else {
                        callback.onError("User profile not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting user profile", e);
                    callback.onError("Failed to get user profile: " + e.getMessage());
                });
    }
    
    // Register new user
    public void registerUser(String email, String password, String fullName, String phoneNumber, 
                           String role, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User registration successful");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        
                        if (firebaseUser != null) {
                            // Create user profile
                            User user = new User(
                                firebaseUser.getUid(),
                                email,
                                fullName,
                                phoneNumber,
                                role,
                                System.currentTimeMillis()
                            );
                            
                            // Save user to Firestore
                            db.collection("users").document(firebaseUser.getUid())
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "User profile saved");
                                        
                                        // Update display name
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(fullName)
                                                .build();
                                        
                                        firebaseUser.updateProfile(profileUpdates)
                                                .addOnCompleteListener(updateTask -> {
                                                    if (updateTask.isSuccessful()) {
                                                        Log.d(TAG, "Display name updated");
                                                        
                                                        // Send email verification
                                                        sendEmailVerification(new EmailVerificationCallback() {
                                                            @Override
                                                            public void onSuccess() {
                                                                callback.onSuccess(user);
                                                            }
                                                            
                                                            @Override
                                                            public void onError(String error) {
                                                                // Still succeed even if email verification fails
                                                                callback.onSuccess(user);
                                                            }
                                                        });
                                                    } else {
                                                        Log.e(TAG, "Failed to update display name");
                                                        callback.onSuccess(user); // Still succeed
                                                    }
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to save user profile", e);
                                        callback.onError("Failed to save user profile: " + e.getMessage());
                                    });
                        } else {
                            callback.onError("Failed to get user after registration");
                        }
                    } else {
                        Log.e(TAG, "User registration failed", task.getException());
                        String errorMessage = getErrorMessage(task.getException());
                        callback.onError(errorMessage);
                    }
                });
    }
    
    // Login user
    public void loginUser(String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Login successful");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        
                        if (firebaseUser != null) {
                            // Get user profile from Firestore
                            db.collection("users").document(firebaseUser.getUid())
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            User user = documentSnapshot.toObject(User.class);
                                            if (user != null) {
                                                callback.onSuccess(user);
                                            } else {
                                                callback.onError("Failed to parse user data");
                                            }
                                        } else {
                                            callback.onError("User profile not found");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error getting user profile", e);
                                        callback.onError("Failed to get user profile: " + e.getMessage());
                                    });
                        } else {
                            callback.onError("Failed to get user after login");
                        }
                    } else {
                        Log.e(TAG, "Login failed", task.getException());
                        String errorMessage = getErrorMessage(task.getException());
                        callback.onError(errorMessage);
                    }
                });
    }
    
    // Logout user
    public void logoutUser() {
        mAuth.signOut();
        Log.d(TAG, "User logged out");
    }
    
    // Send email verification
    public void sendEmailVerification(EmailVerificationCallback callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Verification email sent");
                            if (context != null) {
                                Toast.makeText(context, "Verification email sent to " + user.getEmail(), 
                                        Toast.LENGTH_LONG).show();
                            }
                            callback.onSuccess();
                        } else {
                            Log.e(TAG, "Failed to send verification email", task.getException());
                            callback.onError("Failed to send verification email");
                        }
                    });
        } else {
            callback.onError("No user logged in");
        }
    }
    
    // Check if email is verified
    public boolean isEmailVerified() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null && user.isEmailVerified();
    }
    
    // Reset password
    public void resetPassword(String email, PasswordResetCallback callback) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Password reset email sent");
                        if (context != null) {
                            Toast.makeText(context, "Password reset email sent to " + email, 
                                    Toast.LENGTH_LONG).show();
                        }
                        callback.onSuccess();
                    } else {
                        Log.e(TAG, "Failed to send password reset email", task.getException());
                        String errorMessage = getErrorMessage(task.getException());
                        callback.onError(errorMessage);
                    }
                });
    }
    
    // Update user profile
    public void updateUserProfile(User user, AuthCallback callback) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            callback.onError("No user logged in");
            return;
        }
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", user.getFullName());
        updates.put("phoneNumber", user.getPhoneNumber());
        updates.put("role", user.getRole());
        updates.put("updatedAt", System.currentTimeMillis());
        
        db.collection("users").document(firebaseUser.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User profile updated");
                    
                    // Update display name if changed
                    if (!user.getFullName().equals(firebaseUser.getDisplayName())) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(user.getFullName())
                                .build();
                        
                        firebaseUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Log.d(TAG, "Display name updated");
                                    }
                                });
                    }
                    
                    callback.onSuccess(user);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update user profile", e);
                    callback.onError("Failed to update profile: " + e.getMessage());
                });
    }
    
    // Get user role
    public void getUserRole(String userId, OnCompleteListener<String> callback) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            callback.onComplete(Task.forResult(user.getRole()));
                        } else {
                            callback.onComplete(Task.forException(new Exception("User not found")));
                        }
                    } else {
                        callback.onComplete(Task.forException(new Exception("User profile not found")));
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onComplete(Task.forException(e));
                });
    }
    
    // Helper method to get user-friendly error messages
    private String getErrorMessage(Exception exception) {
        if (exception == null) {
            return "Unknown error occurred";
        }
        
        String message = exception.getMessage();
        if (message == null) {
            return "Authentication failed";
        }
        
        if (message.contains("email")) {
            if (message.contains("badly formatted")) {
                return "Invalid email address";
            } else if (message.contains("already in use")) {
                return "Email already registered";
            } else if (message.contains("not found")) {
                return "Email not found";
            }
        } else if (message.contains("password")) {
            if (message.contains("weak")) {
                return "Password is too weak (minimum 6 characters)";
            } else if (message.contains("wrong")) {
                return "Incorrect password";
            }
        } else if (message.contains("network")) {
            return "Network error. Please check your connection";
        } else if (message.contains("too many")) {
            return "Too many failed attempts. Please try again later";
        }
        
        return "Authentication failed: " + message;
    }
}
