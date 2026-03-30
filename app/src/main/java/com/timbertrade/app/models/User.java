package com.timbertrade.app.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Entity(tableName = "users")
public class User implements Serializable {
    @PrimaryKey
    @NonNull
    private String userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;
    private String profileImageUrl;
    private String location;
    private boolean isVerified;
    private boolean isEmailVerified;
    private long createdAt;
    private long updatedAt;
    private long lastLoginAt;
    
    public User() {
        // Default constructor for Firebase
    }
    
    // Constructor for Firebase Auth
    public User(String userId, String email, String fullName, String phoneNumber, String role, long createdAt) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.isVerified = false;
        this.isEmailVerified = false;
    }
    
    // Legacy constructor
    public User(String userId, String fullName, String email, String phoneNumber, UserRole role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role != null ? role.toString() : "BUYER";
        this.createdAt = new Date().getTime();
        this.updatedAt = new Date().getTime();
        this.isVerified = false;
        this.isEmailVerified = false;
    }
    
    // Create from Firebase User
    public static User fromFirebaseUser(FirebaseUser firebaseUser, String role) {
        User user = new User();
        user.userId = firebaseUser.getUid();
        user.email = firebaseUser.getEmail();
        user.fullName = firebaseUser.getDisplayName();
        user.isEmailVerified = firebaseUser.isEmailVerified();
        user.role = role;
        user.createdAt = System.currentTimeMillis();
        user.updatedAt = System.currentTimeMillis();
        user.isVerified = false;
        return user;
    }
    
    // Create from Firestore document
    public static User fromFirestoreMap(Map<String, Object> data) {
        User user = new User();
        if (data != null) {
            user.userId = (String) data.get("userId");
            user.fullName = (String) data.get("fullName");
            user.email = (String) data.get("email");
            user.phoneNumber = (String) data.get("phoneNumber");
            user.role = (String) data.get("role");
            user.profileImageUrl = (String) data.get("profileImageUrl");
            user.location = (String) data.get("location");
            user.isVerified = Boolean.TRUE.equals(data.get("isVerified"));
            user.isEmailVerified = Boolean.TRUE.equals(data.get("isEmailVerified"));
            user.createdAt = data.get("createdAt") != null ? ((Long) data.get("createdAt")) : System.currentTimeMillis();
            user.updatedAt = data.get("updatedAt") != null ? ((Long) data.get("updatedAt")) : System.currentTimeMillis();
            user.lastLoginAt = data.get("lastLoginAt") != null ? ((Long) data.get("lastLoginAt")) : null;
        }
        return user;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    // Legacy method for compatibility
    public UserRole getUserRole() {
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (Exception e) {
            return UserRole.BUYER;
        }
    }
    
    public void setUserRole(UserRole role) {
        this.role = role.toString();
    }
    
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public boolean isVerified() {
        return isVerified;
    }
    
    public void setVerified(boolean verified) {
        isVerified = verified;
    }
    
    public boolean isEmailVerified() {
        return isEmailVerified;
    }
    
    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public long getLastLoginAt() {
        return lastLoginAt;
    }
    
    public void setLastLoginAt(long lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
    
    // Legacy getters for compatibility
    public Date getCreatedAtDate() {
        return new Date(createdAt);
    }
    
    public Date getLastLoginAtDate() {
        return lastLoginAt != null ? new Date(lastLoginAt) : null;
    }
    
    public void setLastLoginAtDate(Date date) {
        this.lastLoginAt = date != null ? date.getTime() : null;
    }
    
    public enum UserRole {
        BUYER("Buyer"),
        SELLER("Seller"), 
        ADMIN("Admin");
        
        private final String displayName;
        
        UserRole(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
