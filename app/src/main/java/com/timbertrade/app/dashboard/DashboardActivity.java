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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView;

import com.timbertrade.app.R;
import com.timbertrade.app.auth.LoginActivity;
import com.timbertrade.app.models.User;
import com.timbertrade.app.utils.DemoDataGenerator;

import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    
    private static final String TAG = "DashboardActivity";
    
    private Toolbar toolbar;
    private LinearLayout bottomNavigation;
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
bottomNavigation = findViewById(R.id.bottomNavigationContainer);
                
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
                // Find individual tab views
                RelativeLayout tabDashboard = bottomNavigation.findViewById(R.id.tabDashboard);
                RelativeLayout tabMarketplace = bottomNavigation.findViewById(R.id.tabMarketplace);
                RelativeLayout tabAuctions = bottomNavigation.findViewById(R.id.tabAuctions);
                RelativeLayout tabPayments = bottomNavigation.findViewById(R.id.tabPayments);
                RelativeLayout tabProfile = bottomNavigation.findViewById(R.id.tabProfile);
                
                // Set click listeners for each tab
                if (tabDashboard != null) {
                    tabDashboard.setOnClickListener(v -> {
                        Log.d(TAG, "Dashboard tab clicked");
                        showDashboardFragment();
                        updateTabSelection(0);
                    });
                }
                
                if (tabMarketplace != null) {
                    tabMarketplace.setOnClickListener(v -> {
                        Log.d(TAG, "Marketplace tab clicked");
                        try {
                            Fragment selectedFragment = new com.timbertrade.app.marketplace.MarketplaceFragment();
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.flContent, selectedFragment)
                                    .commit();
                            updateTabSelection(1);
                        } catch (Exception e) {
                            Log.e(TAG, "Error creating MarketplaceFragment: " + e.getMessage(), e);
                            createFallbackLayout();
                        }
                    });
                }
                
                if (tabAuctions != null) {
                    tabAuctions.setOnClickListener(v -> {
                        Log.d(TAG, "Auctions tab clicked");
                        try {
                            Fragment selectedFragment = new com.timbertrade.app.auction.AuctionsFragment();
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.flContent, selectedFragment)
                                    .commit();
                            updateTabSelection(2);
                        } catch (Exception e) {
                            Log.e(TAG, "Error creating AuctionsFragment: " + e.getMessage(), e);
                            createFallbackLayout();
                        }
                    });
                }
                
                if (tabPayments != null) {
                    tabPayments.setOnClickListener(v -> {
                        Log.d(TAG, "Payments tab clicked");
                        try {
                            Fragment selectedFragment = new com.timbertrade.app.payment.PaymentsFragment();
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.flContent, selectedFragment)
                                    .commit();
                            updateTabSelection(3);
                        } catch (Exception e) {
                            Log.e(TAG, "Error creating PaymentsFragment: " + e.getMessage(), e);
                            createFallbackLayout();
                        }
                    });
                }
                
                if (tabProfile != null) {
                    tabProfile.setOnClickListener(v -> {
                        Log.d(TAG, "Profile tab clicked");
                        // TODO: Show profile fragment
                        Log.d(TAG, "Profile feature coming soon");
                        updateTabSelection(4);
                    });
                }
                
                // Set default selection (Dashboard)
                updateTabSelection(0);
                Log.d(TAG, "Bottom navigation setup complete");
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
                switch (currentUser.getUserRole()) {
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
    
    private void updateTabSelection(int selectedIndex) {
        try {
            if (bottomNavigation != null) {
                // Reset all tabs to default state
                resetTab(R.id.tabDashboard, R.id.ivDashboard, R.color.timber_text_secondary);
                resetTab(R.id.tabMarketplace, R.id.ivMarketplace, R.color.timber_text_secondary);
                resetTab(R.id.tabAuctions, R.id.ivAuctions, R.color.timber_text_secondary);
                resetTab(R.id.tabPayments, R.id.ivPayments, R.color.timber_text_secondary);
                resetTab(R.id.tabProfile, R.id.ivProfile, R.color.timber_text_secondary);
                
                // Highlight selected tab
                switch (selectedIndex) {
                    case 0: // Dashboard
                        highlightTab(R.id.tabDashboard, R.id.ivDashboard, R.color.timber_primary);
                        break;
                    case 1: // Marketplace
                        highlightTab(R.id.tabMarketplace, R.id.ivMarketplace, R.color.timber_primary);
                        break;
                    case 2: // Auctions
                        highlightTab(R.id.tabAuctions, R.id.ivAuctions, R.color.timber_primary);
                        break;
                    case 3: // Payments
                        highlightTab(R.id.tabPayments, R.id.ivPayments, R.color.timber_primary);
                        break;
                    case 4: // Profile
                        highlightTab(R.id.tabProfile, R.id.ivProfile, R.color.timber_primary);
                        break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating tab selection: " + e.getMessage(), e);
        }
    }
    
    private void resetTab(int tabId, int iconId, int colorId) {
        try {
            RelativeLayout tab = bottomNavigation.findViewById(tabId);
            if (tab != null) {
                ImageView icon = tab.findViewById(iconId);
                if (icon != null) {
                    icon.setColorFilter(getResources().getColor(colorId, null));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error resetting tab: " + e.getMessage(), e);
        }
    }
    
    private void highlightTab(int tabId, int iconId, int colorId) {
        try {
            RelativeLayout tab = bottomNavigation.findViewById(tabId);
            if (tab != null) {
                ImageView icon = tab.findViewById(iconId);
                if (icon != null) {
                    icon.setColorFilter(getResources().getColor(colorId, null));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error highlighting tab: " + e.getMessage(), e);
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
