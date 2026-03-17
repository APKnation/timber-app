package com.timbertrade.app.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RealDashboardActivity extends Activity {
    
    private static final String TAG = "RealDashboardActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starting RealDashboardActivity");
        
        try {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "super.onCreate completed");
            
            // Create professional dashboard layout
            createProfessionalDashboard();
            Log.d(TAG, "RealDashboardActivity setup complete");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in RealDashboardActivity: " + e.getMessage(), e);
            e.printStackTrace();
            
            // Fallback to clean version
            try {
                TextView errorText = new TextView(this);
                errorText.setText("TimberTrade Dashboard\n\nError loading dashboard\n\nPlease restart the app.");
                errorText.setTextSize(16);
                errorText.setPadding(50, 50, 50, 50);
                errorText.setBackgroundColor(Color.parseColor("#2E7D32"));
                errorText.setTextColor(Color.WHITE);
                errorText.setGravity(Gravity.CENTER);
                setContentView(errorText);
            } catch (Exception ex) {
                Log.e(TAG, "Even error display failed: " + ex.getMessage(), ex);
            }
        }
    }
    
    private void createProfessionalDashboard() {
        Log.d(TAG, "createProfessionalDashboard: Creating professional layout");
        
        // Main container
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.parseColor("#F8F9FA")); // Modern light background
        
        // Header Section
        createHeaderSection(mainLayout);
        
        // Welcome Section for First-Time Users
        createWelcomeSection(mainLayout);
        
        // Stats Section
        createStatsSection(mainLayout);
        
        // Recent Activity Section
        createRecentActivitySection(mainLayout);
        
        // Bottom Navigation
        createBottomNavigation(mainLayout);
        
        setContentView(mainLayout);
        Log.d(TAG, "Professional dashboard created successfully");
    }
    
    private void createHeaderSection(LinearLayout mainLayout) {
        Log.d(TAG, "createHeaderSection: Creating header");
        
        // Header container with gradient
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.VERTICAL);
        
        // Create gradient background
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#2E7D32"), Color.parseColor("#1B5E20")}
        );
        gradientDrawable.setCornerRadius(0);
        headerLayout.setBackground(gradientDrawable);
        headerLayout.setPadding(30, 50, 30, 50);
        
        // Title with shadow effect
        TextView titleText = new TextView(this);
        titleText.setText("TimberManagement App");
        titleText.setTextSize(28);
        titleText.setTextColor(Color.WHITE);
        titleText.setGravity(Gravity.CENTER);
        titleText.setShadowLayer(2, 1, 1, Color.parseColor("#000000"));
        titleText.setPadding(0, 0, 0, 15);
        headerLayout.addView(titleText);
        
        // Subtitle
        TextView subtitleText = new TextView(this);
        subtitleText.setText("Timber Management System");
        subtitleText.setTextSize(16);
        subtitleText.setTextColor(Color.parseColor("#E8F5E8"));
        subtitleText.setGravity(Gravity.CENTER);
        subtitleText.setPadding(0, 0, 0, 20);
        headerLayout.addView(subtitleText);
        
        // User info card
        LinearLayout userCard = createCard(Color.parseColor("#FFFFFF"), 20);
        userCard.setPadding(20, 15, 20, 15);
        
        TextView userText = new TextView(this);
        userText.setText("Welcome, Atanasi Kafuka\nBuyer Account\n+254 712 345 678");
        userText.setTextSize(14);
        userText.setTextColor(Color.parseColor("#333333"));
        userText.setGravity(Gravity.CENTER);
        userCard.addView(userText);
        
        headerLayout.addView(userCard);
        mainLayout.addView(headerLayout);
        
        // Add spacing
        addSpacer(mainLayout, 20);
    }
    
    private void createStatsSection(LinearLayout mainLayout) {
        Log.d(TAG, "createStatsSection: Creating stats");
        
        // Stats container
        LinearLayout statsLayout = new LinearLayout(this);
        statsLayout.setOrientation(LinearLayout.HORIZONTAL);
        statsLayout.setPadding(20, 0, 20, 0);
        
        // Active Orders
        LinearLayout activeOrdersLayout = createEnhancedStatCard("Active Orders", "12", Color.parseColor("#4CAF50"));
        statsLayout.addView(activeOrdersLayout);
        
        // Pending Payments
        LinearLayout pendingPaymentsLayout = createEnhancedStatCard("Pending Payments", "3", Color.parseColor("#FF9800"));
        statsLayout.addView(pendingPaymentsLayout);
        
        // Total Revenue
        LinearLayout revenueLayout = createEnhancedStatCard("Total Revenue", "$45,230", Color.parseColor("#2196F3"));
        statsLayout.addView(revenueLayout);
        
        mainLayout.addView(statsLayout);
        
        // Add spacing
        addSpacer(mainLayout, 20);
    }
    
    private LinearLayout createEnhancedStatCard(String title, String value, int color) {
        LinearLayout cardLayout = new LinearLayout(this);
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.setBackgroundColor(Color.WHITE);
        
        // Create card with shadow effect
        GradientDrawable cardBackground = new GradientDrawable();
        cardBackground.setColor(Color.WHITE);
        cardBackground.setCornerRadius(12);
        cardLayout.setBackground(cardBackground);
        cardLayout.setPadding(20, 20, 20, 20);
        cardLayout.setElevation(4);
        
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        );
        cardParams.setMargins(8, 0, 8, 0);
        cardLayout.setLayoutParams(cardParams);
        
        // Value
        TextView valueText = new TextView(this);
        valueText.setText(value);
        valueText.setTextSize(20);
        valueText.setTextColor(color);
        valueText.setGravity(Gravity.CENTER);
        valueText.setPadding(0, 5, 0, 5);
        valueText.setTypeface(null, android.graphics.Typeface.BOLD);
        cardLayout.addView(valueText);
        
        // Title
        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(12);
        titleText.setTextColor(Color.parseColor("#666666"));
        titleText.setGravity(Gravity.CENTER);
        cardLayout.addView(titleText);
        
        return cardLayout;
    }
    
    private void createQuickActionsSection(LinearLayout mainLayout) {
        Log.d(TAG, "createQuickActionsSection: Creating quick actions");
        
        // Section title
        TextView actionsTitle = new TextView(this);
        actionsTitle.setText("Quick Actions");
        actionsTitle.setTextSize(20);
        actionsTitle.setTextColor(Color.parseColor("#333333"));
        actionsTitle.setPadding(20, 0, 20, 15);
        actionsTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        mainLayout.addView(actionsTitle);
        
        // Actions container
        LinearLayout actionsLayout = new LinearLayout(this);
        actionsLayout.setOrientation(LinearLayout.HORIZONTAL);
        actionsLayout.setPadding(20, 0, 20, 0);
        
        // New Order Button
        Button newOrderBtn = createEnhancedActionButton("New Order", Color.parseColor("#4CAF50"));
        newOrderBtn.setOnClickListener(v -> {
            Log.d(TAG, "New Order button clicked");
            Intent intent = new Intent(RealDashboardActivity.this, com.timbertrade.app.orders.NewOrderActivity.class);
            startActivity(intent);
        });
        actionsLayout.addView(newOrderBtn);
        
        // View Inventory Button
        Button inventoryBtn = createEnhancedActionButton("Inventory", Color.parseColor("#2196F3"));
        inventoryBtn.setOnClickListener(v -> {
            Log.d(TAG, "Inventory button clicked");
            Intent intent = new Intent(RealDashboardActivity.this, com.timbertrade.app.inventory.InventoryActivity.class);
            startActivity(intent);
        });
        actionsLayout.addView(inventoryBtn);
        
        // Reports Button
        Button reportsBtn = createEnhancedActionButton("Reports", Color.parseColor("#FF9800"));
        reportsBtn.setOnClickListener(v -> {
            Log.d(TAG, "Reports button clicked");
            Intent intent = new Intent(RealDashboardActivity.this, com.timbertrade.app.reports.ReportsActivity.class);
            startActivity(intent);
        });
        actionsLayout.addView(reportsBtn);
        
        mainLayout.addView(actionsLayout);
        
        // Add spacing
        addSpacer(mainLayout, 20);
    }
    
    private Button createEnhancedActionButton(String text, int color) {
        Button button = new Button(this);
        button.setText(text);
        button.setBackgroundColor(color);
        button.setTextColor(Color.WHITE);
        button.setPadding(15, 20, 15, 20);
        
        // Create rounded button
        GradientDrawable buttonBackground = new GradientDrawable();
        buttonBackground.setColor(color);
        buttonBackground.setCornerRadius(12);
        button.setBackground(buttonBackground);
        button.setElevation(3);
        
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        );
        btnParams.setMargins(5, 0, 5, 0);
        button.setLayoutParams(btnParams);
        
        return button;
    }
    
    private void createRecentActivitySection(LinearLayout mainLayout) {
        Log.d(TAG, "createRecentActivitySection: Creating recent activity");
        
        // Section title
        TextView activityTitle = new TextView(this);
        activityTitle.setText("Recent Activity");
        activityTitle.setTextSize(20);
        activityTitle.setTextColor(Color.parseColor("#333333"));
        activityTitle.setPadding(20, 0, 20, 15);
        activityTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        mainLayout.addView(activityTitle);
        
        // Activity container
        LinearLayout activityLayout = createCard(Color.WHITE, 12);
        activityLayout.setPadding(20, 15, 20, 20);
        LinearLayout.LayoutParams activityParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        activityParams.setMargins(20, 0, 20, 20);
        activityLayout.setLayoutParams(activityParams);
        
        // Activity items
        addEnhancedActivityItem(activityLayout, "New order received", "Oak Wood - 50 units", "2 hours ago", Color.parseColor("#4CAF50"));
        addEnhancedActivityItem(activityLayout, "Payment confirmed", "Order #1234 - $2,340", "5 hours ago", Color.parseColor("#FF9800"));
        addEnhancedActivityItem(activityLayout, "Inventory updated", "Pine Wood stock +100 units", "1 day ago", Color.parseColor("#2196F3"));
        addEnhancedActivityItem(activityLayout, "Report generated", "Monthly summary ready", "2 days ago", Color.parseColor("#9C27B0"));
        
        mainLayout.addView(activityLayout);
        
        // Add spacing
        addSpacer(mainLayout, 20);
    }
    
    private void addEnhancedActivityItem(LinearLayout parent, String title, String description, String time, int color) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setPadding(0, 12, 0, 12);
        
        // Text content
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        
        // Title
        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(14);
        titleText.setTextColor(Color.parseColor("#333333"));
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        textLayout.addView(titleText);
        
        // Description
        TextView descText = new TextView(this);
        descText.setText(description);
        descText.setTextSize(12);
        descText.setTextColor(Color.parseColor("#666666"));
        descText.setPadding(0, 2, 0, 2);
        textLayout.addView(descText);
        
        // Time
        TextView timeText = new TextView(this);
        timeText.setText(time);
        timeText.setTextSize(10);
        timeText.setTextColor(color);
        textLayout.addView(timeText);
        
        itemLayout.addView(textLayout);
        
        // Divider
        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor("#EEEEEE"));
        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        parent.addView(divider);
        
        parent.addView(itemLayout);
    }
    
    private void createBottomNavigation(LinearLayout mainLayout) {
        Log.d(TAG, "createBottomNavigation: Creating bottom navigation");
        
        // Navigation container
        LinearLayout navLayout = new LinearLayout(this);
        navLayout.setOrientation(LinearLayout.HORIZONTAL);
        navLayout.setBackgroundColor(Color.WHITE);
        navLayout.setPadding(0, 15, 0, 15);
        navLayout.setElevation(8);
        
        // Home button
        Button homeBtn = createNavButton("Home", Color.parseColor("#2E7D32"));
        homeBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Already on Dashboard", Toast.LENGTH_SHORT).show();
        });
        navLayout.addView(homeBtn);
        
        // Marketplace button
        Button marketBtn = createNavButton("Market", Color.parseColor("#666666"));
        marketBtn.setOnClickListener(v -> {
            Log.d(TAG, "Marketplace button clicked");
            Intent intent = new Intent(RealDashboardActivity.this, com.timbertrade.app.marketplace.MarketplaceActivity.class);
            startActivity(intent);
        });
        navLayout.addView(marketBtn);
        
        // Orders button
        Button ordersBtn = createNavButton("Orders", Color.parseColor("#666666"));
        ordersBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Orders coming soon!", Toast.LENGTH_SHORT).show();
        });
        navLayout.addView(ordersBtn);
        
        // Profile button
        Button profileBtn = createNavButton("Profile", Color.parseColor("#666666"));
        profileBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Profile coming soon!", Toast.LENGTH_SHORT).show();
        });
        navLayout.addView(profileBtn);
        
        mainLayout.addView(navLayout);
    }
    
    private Button createNavButton(String text, int color) {
        Button button = new Button(this);
        button.setText(text);
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setTextColor(color);
        button.setTextSize(12);
        button.setPadding(0, 5, 0, 5);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        );
        button.setLayoutParams(params);
        
        return button;
    }
    
    private LinearLayout createCard(int backgroundColor, int cornerRadius) {
        LinearLayout card = new LinearLayout(this);
        GradientDrawable background = new GradientDrawable();
        background.setColor(backgroundColor);
        background.setCornerRadius(cornerRadius);
        card.setBackground(background);
        card.setElevation(2);
        return card;
    }
    
    private void addSpacer(LinearLayout parent, int height) {
        TextView spacer = new TextView(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
        parent.addView(spacer);
    }
    
    private void createWelcomeSection(LinearLayout mainLayout) {
        Log.d(TAG, "createWelcomeSection: Creating welcome section for first-time users");
        
        // Welcome card container
        LinearLayout welcomeCard = createCard(Color.WHITE, 16);
        welcomeCard.setOrientation(LinearLayout.VERTICAL);
        welcomeCard.setPadding(25, 25, 25, 25);
        welcomeCard.setElevation(3);
        
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(20, 0, 20, 0);
        welcomeCard.setLayoutParams(cardParams);
        
        // Welcome title
        TextView welcomeTitle = new TextView(this);
        welcomeTitle.setText("Welcome to TimberManagement App!");
        welcomeTitle.setTextSize(20);
        welcomeTitle.setTextColor(Color.parseColor("#2E7D32"));
        welcomeTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        welcomeTitle.setGravity(Gravity.CENTER);
        welcomeTitle.setPadding(0, 0, 0, 15);
        welcomeCard.addView(welcomeTitle);
        
        // Quick Action Buttons at Top
        TextView actionsTitle = new TextView(this);
        actionsTitle.setText("Quick Start:");
        actionsTitle.setTextSize(16);
        actionsTitle.setTextColor(Color.parseColor("#2E7D32"));
        actionsTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        actionsTitle.setPadding(0, 10, 0, 15);
        welcomeCard.addView(actionsTitle);
        
        // Buttons container
        LinearLayout buttonsLayout = new LinearLayout(this);
        buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonsLayout.setPadding(0, 0, 0, 20);
        
        // New Order Button
        Button newOrderBtn = createWelcomeButton("New Order", Color.parseColor("#4CAF50"));
        newOrderBtn.setOnClickListener(v -> {
            startActivity(new Intent(RealDashboardActivity.this, com.timbertrade.app.orders.NewOrderActivity.class));
        });
        buttonsLayout.addView(newOrderBtn);
        
        // Inventory Button
        Button inventoryBtn = createWelcomeButton("Inventory", Color.parseColor("#2196F3"));
        inventoryBtn.setOnClickListener(v -> {
            startActivity(new Intent(RealDashboardActivity.this, com.timbertrade.app.inventory.InventoryActivity.class));
        });
        buttonsLayout.addView(inventoryBtn);
        
        // Reports Button
        Button reportsBtn = createWelcomeButton("Reports", Color.parseColor("#FF9800"));
        reportsBtn.setOnClickListener(v -> {
            startActivity(new Intent(RealDashboardActivity.this, com.timbertrade.app.reports.ReportsActivity.class));
        });
        buttonsLayout.addView(reportsBtn);
        
        welcomeCard.addView(buttonsLayout);
        
        // Welcome message
        TextView welcomeMessage = new TextView(this);
        welcomeMessage.setText("Your complete timber management solution is here! This app helps you manage orders, track inventory, and generate business reports with ease.");
        welcomeMessage.setTextSize(14);
        welcomeMessage.setTextColor(Color.parseColor("#333333"));
        welcomeMessage.setPadding(0, 0, 0, 20);
        welcomeCard.addView(welcomeMessage);
        
        // Features overview
        TextView featuresTitle = new TextView(this);
        featuresTitle.setText("What You Can Do:");
        featuresTitle.setTextSize(16);
        featuresTitle.setTextColor(Color.parseColor("#2E7D32"));
        featuresTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        featuresTitle.setPadding(0, 10, 0, 15);
        welcomeCard.addView(featuresTitle);
        
        // Features list
        String[] features = {
            "1. Manage Orders - Create, edit, and track timber orders",
            "2. Track Inventory - Monitor stock levels and supplies", 
            "3. Generate Reports - Business analytics and insights",
            "4. Easy Navigation - Switch between sections seamlessly",
            "5. Mobile Ready - Access your data anywhere"
        };
        
        for (String feature : features) {
            TextView featureText = new TextView(this);
            featureText.setText(feature);
            featureText.setTextSize(13);
            featureText.setTextColor(Color.parseColor("#555555"));
            featureText.setPadding(0, 5, 0, 5);
            welcomeCard.addView(featureText);
        }
        
        // Getting started section
        TextView gettingStartedTitle = new TextView(this);
        gettingStartedTitle.setText("Getting Started:");
        gettingStartedTitle.setTextSize(16);
        gettingStartedTitle.setTextColor(Color.parseColor("#2E7D32"));
        gettingStartedTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        gettingStartedTitle.setPadding(0, 20, 0, 10);
        welcomeCard.addView(gettingStartedTitle);
        
        TextView gettingStartedText = new TextView(this);
        gettingStartedText.setText("1. Use the navigation bar below to explore different sections\n2. Click 'New Order' to create your first timber order\n3. Check 'Inventory' to see current stock levels\n4. Generate 'Reports' to view business analytics");
        gettingStartedText.setTextSize(13);
        gettingStartedText.setTextColor(Color.parseColor("#555555"));
        gettingStartedText.setPadding(0, 0, 0, 10);
        welcomeCard.addView(gettingStartedText);
        
        // Pro tip
        TextView tipTitle = new TextView(this);
        tipTitle.setText("Pro Tip:");
        tipTitle.setTextSize(14);
        tipTitle.setTextColor(Color.parseColor("#FF9800"));
        tipTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tipTitle.setPadding(0, 15, 0, 5);
        welcomeCard.addView(tipTitle);
        
        TextView tipText = new TextView(this);
        tipText.setText("All your data is saved locally on this device. You can create unlimited orders, inventory items, and reports without any limits!");
        tipText.setTextSize(12);
        tipText.setTextColor(Color.parseColor("#666666"));
        tipText.setPadding(0, 0, 0, 0);
        welcomeCard.addView(tipText);
        
        mainLayout.addView(welcomeCard);
    }
    
    private Button createWelcomeButton(String text, int color) {
        Button button = new Button(this);
        button.setText(text);
        button.setBackgroundColor(color);
        button.setTextColor(Color.WHITE);
        button.setTextSize(12);
        button.setPadding(15, 12, 15, 12);
        
        // Create rounded button
        GradientDrawable buttonBackground = new GradientDrawable();
        buttonBackground.setColor(color);
        buttonBackground.setCornerRadius(8);
        button.setBackground(buttonBackground);
        button.setElevation(2);
        
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        );
        btnParams.setMargins(5, 0, 5, 0);
        button.setLayoutParams(btnParams);
        
        return button;
    }
}
