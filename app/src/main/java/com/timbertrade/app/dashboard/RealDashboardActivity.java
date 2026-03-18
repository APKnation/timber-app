package com.timbertrade.app.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class RealDashboardActivity extends Activity {
    
    private static final String TAG = "RealDashboardActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starting RealDashboardActivity");
        
        try {
            super.onCreate(savedInstanceState);
            createNativeAppDashboard();
        } catch (Exception e) {
            Log.e(TAG, "Error in RealDashboardActivity: " + e.getMessage(), e);
            e.printStackTrace();
            
            // Fallback
            try {
                TextView errorText = new TextView(this);
                errorText.setText("TimberTrade Dashboard Error\nPlease restart the app.");
                errorText.setTextSize(16);
                errorText.setPadding(50, 50, 50, 50);
                errorText.setBackgroundColor(Color.parseColor("#2E7D32"));
                errorText.setTextColor(Color.WHITE);
                errorText.setGravity(Gravity.CENTER);
                setContentView(errorText);
            } catch (Exception ex) {
                Log.e(TAG, "Even error display failed", ex);
            }
        }
    }
    
    private void createNativeAppDashboard() {
        // Root is a RelativeLayout to support fixed bottom navigation
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor("#F5F7FA")); // Modern light gray app background
        
        // --- Bottom Navigation View ---
        int bottomNavId = View.generateViewId();
        LinearLayout bottomNav = createNativeBottomNav();
        bottomNav.setId(bottomNavId);
        
        RelativeLayout.LayoutParams navParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        navParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        root.addView(bottomNav, navParams);
        // -----------------------------
        
        // --- Scrollable Content ---
        ScrollView scrollView = new ScrollView(this);
        scrollView.setVerticalScrollBarEnabled(false);
        RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollParams.addRule(RelativeLayout.ABOVE, bottomNavId);
        
        LinearLayout scrollContent = new LinearLayout(this);
        scrollContent.setOrientation(LinearLayout.VERTICAL);
        scrollContent.setPadding(0, 0, 0, 60); // Padding bottom for smooth scrolling over nav
        
        // Add App Components
        scrollContent.addView(createAppBar());
        scrollContent.addView(createOverviewCards());
        scrollContent.addView(createQuickActionsGrid());
        scrollContent.addView(createRecentActivityList());
        
        scrollView.addView(scrollContent);
        root.addView(scrollView, scrollParams);
        // -----------------------------

        setContentView(root);
    }

    private View createAppBar() {
        LinearLayout appBar = new LinearLayout(this);
        appBar.setOrientation(LinearLayout.HORIZONTAL);
        appBar.setPadding(40, 50, 40, 50);
        appBar.setGravity(Gravity.CENTER_VERTICAL);
        appBar.setBackgroundColor(Color.WHITE);
        appBar.setElevation(8);
        
        // Profile Icon (Simulated)
        TextView profileIcon = new TextView(this);
        profileIcon.setText("AK");
        profileIcon.setTextColor(Color.WHITE);
        profileIcon.setTextSize(18);
        profileIcon.setTypeface(null, Typeface.BOLD);
        profileIcon.setGravity(Gravity.CENTER);
        
        GradientDrawable profileBg = new GradientDrawable();
        profileBg.setShape(GradientDrawable.OVAL);
        profileBg.setColor(Color.parseColor("#2E7D32"));
        profileIcon.setBackground(profileBg);
        
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(120, 120);
        profileIcon.setLayoutParams(iconParams);
        
        // Title Texts
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setPadding(30, 0, 0, 0);
        
        TextView greeting = new TextView(this);
        greeting.setText("Good Morning, Atanasi!" );
        greeting.setTextSize(14);
        greeting.setTextColor(Color.parseColor("#757575"));
        
        TextView appTitle = new TextView(this);
        appTitle.setText("TimberManager");
        appTitle.setTextSize(22);
        appTitle.setTextColor(Color.parseColor("#212121"));
        appTitle.setTypeface(null, Typeface.BOLD);
        
        textLayout.addView(greeting);
        textLayout.addView(appTitle);
        
        appBar.addView(profileIcon);
        appBar.addView(textLayout);
        
        return appBar;
    }

    private View createOverviewCards() {
        LinearLayout sectionLayout = new LinearLayout(this);
        sectionLayout.setOrientation(LinearLayout.VERTICAL);
        sectionLayout.setPadding(40, 60, 40, 30);
        
        TextView sectionTitle = new TextView(this);
        sectionTitle.setText("Financial Overview");
        sectionTitle.setTextSize(18);
        sectionTitle.setTypeface(null, Typeface.BOLD);
        sectionTitle.setTextColor(Color.parseColor("#212121"));
        sectionTitle.setPadding(0, 0, 0, 20);
        sectionLayout.addView(sectionTitle);
        
        // Horizontal Row of Cards
        LinearLayout cardsRow = new LinearLayout(this);
        cardsRow.setOrientation(LinearLayout.HORIZONTAL);
        cardsRow.setWeightSum(2f);
        
        // Balance Card
        LinearLayout balanceCard = createNativeCard(Color.WHITE);
        balanceCard.setPadding(40, 40, 40, 40);
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        p1.setMargins(0, 0, 20, 0);
        balanceCard.setLayoutParams(p1);
        
        TextView balanceLabel = new TextView(this);
        balanceLabel.setText("Total Revenue");
        balanceLabel.setTextSize(13);
        balanceLabel.setTextColor(Color.parseColor("#757575"));
        
        TextView balanceValue = new TextView(this);
        balanceValue.setText("$45,230.00");
        balanceValue.setTextSize(24);
        balanceValue.setTypeface(null, Typeface.BOLD);
        balanceValue.setTextColor(Color.parseColor("#2E7D32"));
        balanceValue.setPadding(0, 10, 0, 0);
        
        balanceCard.addView(balanceLabel);
        balanceCard.addView(balanceValue);
        
        // Orders Card
        LinearLayout ordersCard = createNativeCard(Color.parseColor("#2E7D32")); // Primary color card
        ordersCard.setPadding(40, 40, 40, 40);
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        p2.setMargins(20, 0, 0, 0);
        ordersCard.setLayoutParams(p2);
        
        TextView ordersLabel = new TextView(this);
        ordersLabel.setText("Active Orders");
        ordersLabel.setTextSize(13);
        ordersLabel.setTextColor(Color.parseColor("#E8F5E8"));
        
        TextView ordersValue = new TextView(this);
        ordersValue.setText("12");
        ordersValue.setTextSize(24);
        ordersValue.setTypeface(null, Typeface.BOLD);
        ordersValue.setTextColor(Color.WHITE);
        ordersValue.setPadding(0, 10, 0, 0);
        
        ordersCard.addView(ordersLabel);
        ordersCard.addView(ordersValue);
        
        cardsRow.addView(balanceCard);
        cardsRow.addView(ordersCard);
        
        sectionLayout.addView(cardsRow);
        return sectionLayout;
    }

    private View createQuickActionsGrid() {
        LinearLayout sectionLayout = new LinearLayout(this);
        sectionLayout.setOrientation(LinearLayout.VERTICAL);
        sectionLayout.setPadding(40, 30, 40, 30);
        
        TextView sectionTitle = new TextView(this);
        sectionTitle.setText("App Services");
        sectionTitle.setTextSize(18);
        sectionTitle.setTypeface(null, Typeface.BOLD);
        sectionTitle.setTextColor(Color.parseColor("#212121"));
        sectionTitle.setPadding(0, 0, 0, 20);
        sectionLayout.addView(sectionTitle);
        
        // Row 1
        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setWeightSum(2f);
        
        View action1 = createActionCard("New Order", "#E3F2FD", "#1976D2", v -> {
            startActivity(new Intent(RealDashboardActivity.this, com.timbertrade.app.orders.NewOrderActivity.class));
        });
        
        View action2 = createActionCard("Inventory", "#E8F5E9", "#2E7D32", v -> {
            startActivity(new Intent(RealDashboardActivity.this, com.timbertrade.app.inventory.InventoryActivity.class));
        });
        
        row1.addView(action1);
        row1.addView(action2);
        
        // Row 2
        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.setWeightSum(2f);
        row2.setPadding(0, 40, 0, 0);
        
        View action3 = createActionCard("Reports", "#FFF3E0", "#F57C00", v -> {
            startActivity(new Intent(RealDashboardActivity.this, com.timbertrade.app.reports.ReportsActivity.class));
        });
        
        View action4 = createActionCard("Marketplace", "#F3E5F5", "#7B1FA2", v -> {
            startActivity(new Intent(RealDashboardActivity.this, com.timbertrade.app.marketplace.MarketplaceActivity.class));
        });
        
        row2.addView(action3);
        row2.addView(action4);
        
        sectionLayout.addView(row1);
        sectionLayout.addView(row2);
        
        return sectionLayout;
    }

    private View createActionCard(String title, String bgColor, String textColor, View.OnClickListener listener) {
        LinearLayout outerLayout = new LinearLayout(this);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        p.setMargins(15, 0, 15, 0);
        outerLayout.setLayoutParams(p);
        
        LinearLayout card = createNativeCard(Color.WHITE);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(30, 50, 30, 50);
        card.setOnClickListener(listener);
        
        // Icon circular background
        TextView iconView = new TextView(this);
        iconView.setText(title.substring(0, 1)); // First letter as icon
        iconView.setTextSize(24);
        iconView.setTextColor(Color.parseColor(textColor));
        iconView.setTypeface(null, Typeface.BOLD);
        iconView.setGravity(Gravity.CENTER);
        
        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setShape(GradientDrawable.OVAL);
        iconBg.setColor(Color.parseColor(bgColor));
        
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(110, 110);
        iconParams.setMargins(0, 0, 0, 20);
        iconView.setLayoutParams(iconParams);
        iconView.setBackground(iconBg);
        
        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(14);
        titleText.setTextColor(Color.parseColor("#424242"));
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setGravity(Gravity.CENTER);
        
        card.addView(iconView);
        card.addView(titleText);
        
        outerLayout.addView(card);
        return outerLayout;
    }

    private View createRecentActivityList() {
        LinearLayout sectionLayout = new LinearLayout(this);
        sectionLayout.setOrientation(LinearLayout.VERTICAL);
        sectionLayout.setPadding(40, 40, 40, 60);
        
        TextView sectionTitle = new TextView(this);
        sectionTitle.setText("Recent Activity");
        sectionTitle.setTextSize(18);
        sectionTitle.setTypeface(null, Typeface.BOLD);
        sectionTitle.setTextColor(Color.parseColor("#212121"));
        sectionTitle.setPadding(0, 0, 0, 20);
        sectionLayout.addView(sectionTitle);
        
        LinearLayout listCard = createNativeCard(Color.WHITE);
        listCard.setPadding(20, 10, 20, 10);
        
        listCard.addView(createActivityListItem("Order Completed", "Oak Wood - 50 units", "2h ago"));
        listCard.addView(createDivider());
        listCard.addView(createActivityListItem("Payment Received", "Order #1234 - $2,340", "5h ago"));
        listCard.addView(createDivider());
        listCard.addView(createActivityListItem("Stock Alert", "Pine Wood low on stock", "1d ago"));
        
        sectionLayout.addView(listCard);
        return sectionLayout;
    }

    private View createActivityListItem(String title, String subtitle, String time) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(30, 30, 30, 30);
        item.setGravity(Gravity.CENTER_VERTICAL);
        
        // Simple dot icon
        View dot = new View(this);
        GradientDrawable dotBg = new GradientDrawable();
        dotBg.setShape(GradientDrawable.OVAL);
        dotBg.setColor(Color.parseColor("#2E7D32"));
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(24, 24);
        dotParams.setMargins(0, 0, 30, 0);
        dot.setLayoutParams(dotParams);
        dot.setBackground(dotBg);
        
        // Text Layout (takes remaining space)
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textLayout.setLayoutParams(textParams);
        
        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(15);
        titleText.setTextColor(Color.parseColor("#212121"));
        titleText.setTypeface(null, Typeface.BOLD);
        
        TextView descText = new TextView(this);
        descText.setText(subtitle);
        descText.setTextSize(13);
        descText.setTextColor(Color.parseColor("#757575"));
        descText.setPadding(0, 5, 0, 0);
        
        textLayout.addView(titleText);
        textLayout.addView(descText);
        
        // Time text (aligned to the end)
        TextView timeText = new TextView(this);
        timeText.setText(time);
        timeText.setTextSize(12);
        timeText.setTextColor(Color.parseColor("#9E9E9E"));
        
        item.addView(dot);
        item.addView(textLayout);
        item.addView(timeText);
        
        return item;
    }

    private View createDivider() {
        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor("#EEEEEE"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);
        lp.setMargins(30, 0, 30, 0);
        divider.setLayoutParams(lp);
        return divider;
    }

    private LinearLayout createNativeBottomNav() {
        LinearLayout navBar = new LinearLayout(this);
        navBar.setOrientation(LinearLayout.HORIZONTAL);
        navBar.setBackgroundColor(Color.WHITE);
        navBar.setElevation(20); // prominent shadow at the bottom
        navBar.setPadding(0, 20, 0, 30);
        navBar.setWeightSum(4f);
        
        // We add a top border just in case elevation isn't visibly casting upward
        View border = new View(this);
        border.setBackgroundColor(Color.parseColor("#E0E0E0"));
        navBar.addView(border, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        
        navBar.addView(createBottomNavItem("Home", "#2E7D32", true, true));
        navBar.addView(createBottomNavItem("Market", "#757575", false, false));
        navBar.addView(createBottomNavItem("Orders", "#757575", false, false));
        navBar.addView(createBottomNavItem("Profile", "#757575", false, false));
        
        return navBar;
    }

    private View createBottomNavItem(String title, String color, boolean isActive, boolean isClickable) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setGravity(Gravity.CENTER);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        itemLayout.setLayoutParams(params);
        
        // Simulated Icon (First letter)
        TextView icon = new TextView(this);
        icon.setText(title.substring(0, 1));
        icon.setTextSize(20);
        icon.setTypeface(null, Typeface.BOLD);
        icon.setTextColor(Color.parseColor(color));
        icon.setGravity(Gravity.CENTER);
        icon.setPadding(0, 15, 0, 5);
        
        // Label
        TextView label = new TextView(this);
        label.setText(title);
        label.setTextSize(11);
        label.setTextColor(Color.parseColor(color));
        label.setGravity(Gravity.CENTER);
        
        if (isActive) {
            label.setTypeface(null, Typeface.BOLD);
            // Optionally add an indicator pill
            View indicator = new View(this);
            GradientDrawable pill = new GradientDrawable();
            pill.setCornerRadius(10);
            pill.setColor(Color.parseColor(color));
            indicator.setBackground(pill);
            LinearLayout.LayoutParams indentParams = new LinearLayout.LayoutParams(40, 8);
            indentParams.setMargins(0, 10, 0, 0);
            itemLayout.addView(indicator, indentParams);
        }
        
        itemLayout.addView(icon);
        itemLayout.addView(label);
        
        if (!isClickable && !isActive) {
            itemLayout.setOnClickListener(v -> Toast.makeText(this, title + " coming soon!", Toast.LENGTH_SHORT).show());
        } else if (title.equals("Market")) {
            itemLayout.setOnClickListener(v -> {
                startActivity(new Intent(RealDashboardActivity.this, com.timbertrade.app.marketplace.MarketplaceActivity.class));
            });
        }
        
        return itemLayout;
    }

    private LinearLayout createNativeCard(int backgroundColor) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(backgroundColor);
        bg.setCornerRadius(24f); // Large rounded corners for modern app look
        
        card.setBackground(bg);
        card.setElevation(8f); // Material shadow
        
        return card;
    }
}
