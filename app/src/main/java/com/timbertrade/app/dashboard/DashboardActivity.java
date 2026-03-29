package com.timbertrade.app.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.timbertrade.app.R;
import com.timbertrade.app.auth.LoginActivity;
import com.timbertrade.app.models.User;
import com.timbertrade.app.utils.DemoDataGenerator;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    
    private static final String TAG = "DashboardActivity";
    
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigation;
    private User currentUser;
    private SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starting DashboardActivity (No Auth Required)");
        
        try {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "super.onCreate completed");
            
            // Create a simple fallback layout first
            try {
                Log.d(TAG, "setContentView: Setting dashboard layout");
                setContentView(R.layout.activity_dashboard);
                Log.d(TAG, "setContentView: Dashboard layout set successfully");
            } catch (Exception layoutError) {
                Log.e(TAG, "Error setting main layout, using fallback: " + layoutError.getMessage(), layoutError);
                createFallbackLayout();
                return;
            }
            
            // Initialize views with error handling
            try {
                Log.d(TAG, "Initializing dashboard views");
                toolbar = findViewById(R.id.toolbar);
<<<<<<< HEAD
                bottomNavigation = findViewById(R.id.bottomNavigationContainer);
=======
                bottomNavigation = findViewById(R.id.bottomNavigation);
>>>>>>> a28433b8fa61d1e437af1c58f2eacaf6614f2546
                
                Log.d(TAG, "Views initialized: toolbar=" + (toolbar != null ? "found" : "null") + 
                        ", bottomNavigation=" + (bottomNavigation != null ? "found" : "null"));
            } catch (Exception viewError) {
                Log.e(TAG, "Error initializing views: " + viewError.getMessage(), viewError);
                createFallbackLayout();
                return;
            }
            
            // Setup toolbar with error handling
            try {
                if (toolbar != null) {
                    setSupportActionBar(toolbar);
                    Log.d(TAG, "Toolbar setup complete");
                } else {
                    Log.w(TAG, "Toolbar is null, skipping setup");
                }
            } catch (Exception toolbarError) {
                Log.e(TAG, "Error setting up toolbar: " + toolbarError.getMessage(), toolbarError);
            }
            
            // Load demo user data directly (no authentication required)
            try {
                Log.d(TAG, "Loading demo user data");
                loadDemoUserData();
                Log.d(TAG, "Demo user data loaded successfully");
            } catch (Exception userError) {
                Log.e(TAG, "Error loading user data: " + userError.getMessage(), userError);
                // Create fallback user
                currentUser = new User("demo_user_001", "Demo User", "demo@example.com", "", User.UserRole.BUYER);
                updateUI();
            }
            
            // Setup bottom navigation with error handling
            try {
                Log.d(TAG, "Setting up bottom navigation");
                setupBottomNavigation();
                Log.d(TAG, "Bottom navigation setup complete");
            } catch (Exception navError) {
                Log.e(TAG, "Error setting up navigation: " + navError.getMessage(), navError);
                // Continue without navigation
            }
            
            // Show default fragment with error handling
            try {
                if (savedInstanceState == null) {
                    Log.d(TAG, "Showing default dashboard fragment");
                    showDashboardFragment();
                    Log.d(TAG, "Dashboard fragment shown successfully");
                }
            } catch (Exception fragmentError) {
                Log.e(TAG, "Error showing fragment: " + fragmentError.getMessage(), fragmentError);
                // Continue without fragment
            }
            
            Log.d(TAG, "DashboardActivity setup complete");
            
        } catch (Exception e) {
            Log.e(TAG, "Critical error in onCreate: " + e.getMessage(), e);
            e.printStackTrace();
            
            // Show error message to user
            createErrorLayout("Critical Error: " + e.getMessage());
        }
    }
    
    private void createFallbackLayout() {
        Log.d(TAG, "createFallbackLayout: Creating simple fallback layout");
        
        try {
            // Create a simple layout that won't crash
            android.widget.LinearLayout mainLayout = new android.widget.LinearLayout(this);
            mainLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
            mainLayout.setPadding(50, 50, 50, 50);
            mainLayout.setBackgroundColor(getResources().getColor(R.color.timber_background));
            
            // Title
            TextView titleText = new TextView(this);
            titleText.setText("TimberTrade Dashboard");
            titleText.setTextSize(24);
            titleText.setTextColor(getResources().getColor(R.color.timber_primary));
            titleText.setGravity(android.view.Gravity.CENTER);
            mainLayout.addView(titleText);
            
            // Welcome message
            TextView welcomeText = new TextView(this);
            welcomeText.setText("Welcome to TimberTrade!\n\nFeatures Available:\n• Marketplace\n• Auctions\n• Payments\n• Analytics\n\nDemo data loaded successfully!");
            welcomeText.setTextSize(16);
            welcomeText.setTextColor(getResources().getColor(R.color.timber_text_primary));
            welcomeText.setGravity(android.view.Gravity.CENTER);
            welcomeText.setPadding(0, 30, 0, 0);
            mainLayout.addView(welcomeText);
            
            setContentView(mainLayout);
            Log.d(TAG, "Fallback layout created successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating fallback layout: " + e.getMessage(), e);
            createErrorLayout("Layout Error: " + e.getMessage());
        }
    }
    
    private void createErrorLayout(String errorMessage) {
        Log.d(TAG, "createErrorLayout: Creating error layout");
        
        try {
            TextView errorText = new TextView(this);
            errorText.setText("TimberTrade App\n\n" + errorMessage + "\n\nPlease restart the app.");
            errorText.setTextSize(16);
            errorText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            errorText.setPadding(50, 50, 50, 50);
            errorText.setGravity(android.view.Gravity.CENTER);
            setContentView(errorText);
        } catch (Exception e) {
            Log.e(TAG, "Even error layout failed: " + e.getMessage(), e);
        }
    }
    
    private void loadDemoUserData() {
        Log.d(TAG, "loadDemoUserData: Loading demo user data");
        try {
            // Use first demo user as default (John Mwangi - Buyer)
            List<User> demoUsers = DemoDataGenerator.generateDemoUsers();
            if (!demoUsers.isEmpty()) {
                currentUser = demoUsers.get(0); // John Mwangi (Buyer)
                Log.d(TAG, "Demo user loaded: " + currentUser.getFullName() + " (" + currentUser.getEmail() + ")");
            } else {
                // Fallback: create a default demo user
                currentUser = new User("demo_user_001", "John Mwangi", "john@example.com", "", User.UserRole.BUYER);
                Log.d(TAG, "Fallback demo user created");
            }
            
            updateUI();
            Log.d(TAG, "Demo user data loaded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in loadDemoUserData: " + e.getMessage(), e);
            // Create fallback user in case of error
            currentUser = new User("demo_user_001", "Demo User", "demo@example.com", "", User.UserRole.BUYER);
            updateUI();
        }
    }
    
    private void updateUI() {
        Log.d(TAG, "updateUI: Updating UI");
        try {
            if (currentUser != null && toolbar != null) {
                String welcomeMessage = "Welcome, " + currentUser.getFullName();
                toolbar.setTitle(welcomeMessage);
                Log.d(TAG, "Toolbar title set: " + welcomeMessage);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in updateUI: " + e.getMessage(), e);
        }
    }
    
    private void setupBottomNavigation() {
        Log.d(TAG, "setupBottomNavigation: Setting up navigation");
        try {
            if (bottomNavigation != null) {
                bottomNavigation.setOnItemSelectedListener(item -> {
                    try {
                        Log.d(TAG, "Navigation item selected: " + item.getItemId());
                        Fragment selectedFragment = null;
                        
                        int itemId = item.getItemId();
                        if (itemId == R.id.navigation_dashboard) {
                            showDashboardFragment();
                        } else if (itemId == R.id.navigation_marketplace) {
                            try {
                                selectedFragment = new com.timbertrade.app.marketplace.MarketplaceFragment();
                            } catch (Exception e) {
                                Log.e(TAG, "Error creating MarketplaceFragment: " + e.getMessage(), e);
                                createFallbackLayout();
                                return false;
                            }
                        } else if (itemId == R.id.navigation_auctions) {
                            try {
                                selectedFragment = new com.timbertrade.app.auction.AuctionsFragment();
                            } catch (Exception e) {
                                Log.e(TAG, "Error creating AuctionsFragment: " + e.getMessage(), e);
                                createFallbackLayout();
                                return false;
                            }
                        } else if (itemId == R.id.navigation_payments) {
                            try {
                                selectedFragment = new com.timbertrade.app.payment.PaymentsFragment();
                            } catch (Exception e) {
                                Log.e(TAG, "Error creating PaymentsFragment: " + e.getMessage(), e);
                                createFallbackLayout();
                                return false;
                            }
                        } else if (itemId == R.id.navigation_profile) {
                            // TODO: Show profile fragment
                            Log.d(TAG, "Profile feature coming soon");
                            return true;
                        }
                        
                        if (selectedFragment != null) {
                            try {
                                getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.flContent, selectedFragment)
                                        .commit();
                            } catch (Exception e) {
                                Log.e(TAG, "Error replacing fragment: " + e.getMessage(), e);
                                createFallbackLayout();
                                return false;
                            }
                        }
                        
                        return true;
                    } catch (Exception e) {
                        Log.e(TAG, "Error in navigation: " + e.getMessage(), e);
                        createFallbackLayout();
                        return false;
                    }
                });
                
                // Set default selection
                try {
                    bottomNavigation.setSelectedItemId(R.id.navigation_dashboard);
                    Log.d(TAG, "Bottom navigation setup complete");
                } catch (Exception e) {
                    Log.e(TAG, "Error setting default navigation: " + e.getMessage(), e);
                }
            } else {
                Log.w(TAG, "Bottom navigation is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in setupBottomNavigation: " + e.getMessage(), e);
        }
    }
    
    private void showDashboardFragment() {
        Log.d(TAG, "showDashboardFragment: Showing dashboard fragment");
        try {
            if (currentUser == null) {
                Log.w(TAG, "Current user is null, creating fallback");
                createFallbackLayout();
                return;
            }
            
            Fragment fragment = null;
            
            try {
                switch (currentUser.getRole()) {
                    case BUYER:
                        fragment = new BuyerDashboardFragment();
                        break;
                    case SELLER:
                        fragment = new SellerDashboardFragment();
                        break;
                    case ADMIN:
                        fragment = new AdminDashboardFragment();
                        break;
                    default:
                        fragment = new BuyerDashboardFragment();
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error creating dashboard fragment: " + e.getMessage(), e);
                createFallbackLayout();
                return;
            }
            
            if (fragment != null) {
                try {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flContent, fragment)
                            .commit();
                    Log.d(TAG, "Dashboard fragment shown: " + currentUser.getRole());
                } catch (Exception e) {
                    Log.e(TAG, "Error showing dashboard fragment: " + e.getMessage(), e);
                    createFallbackLayout();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in showDashboardFragment: " + e.getMessage(), e);
            createFallbackLayout();
        }
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
