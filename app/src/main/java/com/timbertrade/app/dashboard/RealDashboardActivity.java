package com.timbertrade.app.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class RealDashboardActivity extends Activity {

    private static final String TAG = "RealDashboardActivity";
    
    // Modern Color Palette
    private final int COLOR_PRIMARY = Color.parseColor("#059669"); // Emerald Native Green
    private final int COLOR_PRIMARY_DARK = Color.parseColor("#047857");
    private final int COLOR_ACCENT = Color.parseColor("#D97706"); // Amber for wood accent
    private final int COLOR_BG = Color.parseColor("#F3F4F6"); // Cool Gray 100
    private final int COLOR_TEXT_PRIMARY = Color.parseColor("#1F2937");
    private final int COLOR_TEXT_SECONDARY = Color.parseColor("#6B7280");
    private final int COLOR_WHITE = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            createAdvancedAppDashboard();
        } catch (Exception e) {
            Log.e(TAG, "Error rendering advanced dashboard", e);
            // Fallback simplistic view
            TextView fallback = new TextView(this);
            fallback.setText("Error loading Advanced UI. Please check logs.");
            setContentView(fallback);
        }
    }

    private void createAdvancedAppDashboard() {
        // Root container
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(COLOR_BG);

        // 1. Decorative Header Background (Stays fixed at the top, scrollview goes over it)
        View headerBg = new View(this);
        GradientDrawable gradientBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{COLOR_PRIMARY, COLOR_PRIMARY_DARK}
        );
        headerBg.setBackground(gradientBg);
        
        RelativeLayout.LayoutParams headerBgParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(280)
        );
        headerBgParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(headerBg, headerBgParams);

        // 2. Bottom Navigation
        int bottomNavId = View.generateViewId();
        View bottomNav = createModernBottomNav();
        bottomNav.setId(bottomNavId);

        RelativeLayout.LayoutParams navParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        navParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        root.addView(bottomNav, navParams);

        // 3. Scrollable Foreground Content
        ScrollView scrollView = new ScrollView(this);
        scrollView.setVerticalScrollBarEnabled(false);
        // Exclude bottom nav area from scroll view
        RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        scrollParams.addRule(RelativeLayout.ABOVE, bottomNavId);
        
        LinearLayout scrollContent = new LinearLayout(this);
        scrollContent.setOrientation(LinearLayout.VERTICAL);
        // Padding bottom to clear scroll slightly over bottom nav seamlessly
        scrollContent.setPadding(0, 0, 0, dpToPx(30)); 

        // Add Header Texts & Greeting (Transparent over the green background)
        scrollContent.addView(createGreetingSection());

        // Overlapping Main Overview Card
        scrollContent.addView(createOverlappingOverviewCard());

        // Services Grid
        scrollContent.addView(createServicesSection());

        // Timeline Recent Activity
        scrollContent.addView(createTimelineActivitySection());

        scrollView.addView(scrollContent);
        root.addView(scrollView, scrollParams);

        setContentView(root);
    }

    private View createGreetingSection() {
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setPadding(dpToPx(24), dpToPx(40), dpToPx(24), dpToPx(20));
        headerLayout.setGravity(Gravity.CENTER_VERTICAL);

        // Text Layout
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
        );
        textLayout.setLayoutParams(textParams);

        TextView greeting = new TextView(this);
        greeting.setText("Hello, Atanasi 👋");
        greeting.setTextSize(16);
        greeting.setTextColor(Color.parseColor("#A7F3D0")); // Light green text

        TextView subTitle = new TextView(this);
        subTitle.setText("Timber Dashboard");
        subTitle.setTextSize(26);
        subTitle.setTypeface(null, Typeface.BOLD);
        subTitle.setTextColor(COLOR_WHITE);

        textLayout.addView(greeting);
        textLayout.addView(subTitle);

        // Avatar
        TextView avatar = new TextView(this);
        avatar.setText("AK");
        avatar.setTextColor(COLOR_PRIMARY);
        avatar.setTextSize(18);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setGravity(Gravity.CENTER);
        
        GradientDrawable avatarBg = new GradientDrawable();
        avatarBg.setShape(GradientDrawable.OVAL);
        avatarBg.setColor(COLOR_WHITE);
        avatar.setBackground(avatarBg);
        
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(dpToPx(50), dpToPx(50));
        avatar.setLayoutParams(avatarParams);
        avatar.setElevation(dpToPx(4));

        headerLayout.addView(textLayout);
        headerLayout.addView(avatar);

        return headerLayout;
    }

    private View createOverlappingOverviewCard() {
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(10));
        
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_WHITE);
        bg.setCornerRadius(dpToPx(24));
        card.setBackground(bg);
        card.setElevation(dpToPx(12));
        card.setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(24));
        
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        card.setLayoutParams(cardParams);

        // Top Row inside card
        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setWeightSum(2f);

        // Balance section
        LinearLayout leftCol = new LinearLayout(this);
        leftCol.setOrientation(LinearLayout.VERTICAL);
        leftCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        
        TextView balanceLabel = new TextView(this);
        balanceLabel.setText("Total Revenue");
        balanceLabel.setTextSize(14);
        balanceLabel.setTextColor(COLOR_TEXT_SECONDARY);
        
        TextView balanceText = new TextView(this);
        balanceText.setText("$45,230.00");
        balanceText.setTextSize(26);
        balanceText.setTypeface(null, Typeface.BOLD);
        balanceText.setTextColor(COLOR_TEXT_PRIMARY);
        
        leftCol.addView(balanceLabel);
        leftCol.addView(balanceText);
        
        // Orders Section
        LinearLayout rightCol = new LinearLayout(this);
        rightCol.setOrientation(LinearLayout.VERTICAL);
        rightCol.setGravity(Gravity.END);
        rightCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        
        TextView orderLabel = new TextView(this);
        orderLabel.setText("Active Orders");
        orderLabel.setTextSize(14);
        orderLabel.setTextColor(COLOR_TEXT_SECONDARY);
        
        TextView orderText = new TextView(this);
        orderText.setText("12");
        orderText.setTextSize(26);
        orderText.setTypeface(null, Typeface.BOLD);
        orderText.setTextColor(COLOR_ACCENT);
        
        rightCol.addView(orderLabel);
        rightCol.addView(orderText);
        
        topRow.addView(leftCol);
        topRow.addView(rightCol);
        card.addView(topRow);

        // Add a nice horizontal separator
        View separator = new View(this);
        separator.setBackgroundColor(Color.parseColor("#E5E7EB"));
        LinearLayout.LayoutParams sepParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1));
        sepParams.setMargins(0, dpToPx(20), 0, dpToPx(20));
        card.addView(separator, sepParams);

        // Bottom Row generic metric
        TextView growthMetric = new TextView(this);
        growthMetric.setText("+15.3% growth this month ↗");
        growthMetric.setTextSize(13);
        growthMetric.setTypeface(null, Typeface.BOLD);
        growthMetric.setTextColor(Color.parseColor("#10B981"));
        card.addView(growthMetric);

        wrapper.addView(card);
        return wrapper;
    }

    private View createServicesSection() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(20), dpToPx(15), dpToPx(20), dpToPx(5));

        TextView title = new TextView(this);
        title.setText("Quick Services");
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(COLOR_TEXT_PRIMARY);
        title.setPadding(dpToPx(5), 0, 0, dpToPx(15));
        layout.addView(title);

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setWeightSum(2f);
        row1.addView(createServiceCard("Orders", "Manage", "#E0E7FF", "#4338CA", v -> {
            startActivity(new Intent(this, com.timbertrade.app.orders.NewOrderActivity.class));
        }));
        row1.addView(createServiceCard("Inventory", "In Stock", "#D1FAE5", "#059669", v -> {
            startActivity(new Intent(this, com.timbertrade.app.inventory.InventoryActivity.class));
        }));

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.setWeightSum(2f);
        row2.setPadding(0, dpToPx(15), 0, 0);
        row2.addView(createServiceCard("Reports", "Analytics", "#FEF3C7", "#D97706", v -> {
            startActivity(new Intent(this, com.timbertrade.app.reports.ReportsActivity.class));
        }));
        row2.addView(createServiceCard("Market", "Trade Now", "#FCE7F3", "#BE185D", v -> {
            startActivity(new Intent(this, com.timbertrade.app.marketplace.MarketplaceActivity.class));
        }));

        layout.addView(row1);
        layout.addView(row2);
        return layout;
    }

    private View createServiceCard(String title, String subtitle, String bgColorStr, String iconColorStr, View.OnClickListener listener) {
        FrameLayout container = new FrameLayout(this);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        p.setMargins(dpToPx(6), 0, dpToPx(6), 0);
        container.setLayoutParams(p);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(20), dpToPx(24), dpToPx(20), dpToPx(24));
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_WHITE);
        bg.setCornerRadius(dpToPx(20));
        card.setBackground(bg);
        card.setElevation(dpToPx(4));

        // Icon circular background
        TextView iconView = new TextView(this);
        iconView.setText(title.substring(0, 1));
        iconView.setTextSize(22);
        iconView.setTextColor(Color.parseColor(iconColorStr));
        iconView.setTypeface(null, Typeface.BOLD);
        iconView.setGravity(Gravity.CENTER);
        
        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setShape(GradientDrawable.OVAL);
        iconBg.setColor(Color.parseColor(bgColorStr));
        
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(48), dpToPx(48));
        iconParams.setMargins(0, 0, 0, dpToPx(16));
        iconView.setLayoutParams(iconParams);
        iconView.setBackground(iconBg);
        
        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(16);
        titleText.setTextColor(COLOR_TEXT_PRIMARY);
        titleText.setTypeface(null, Typeface.BOLD);
        
        TextView subTitleText = new TextView(this);
        subTitleText.setText(subtitle);
        subTitleText.setTextSize(12);
        subTitleText.setTextColor(COLOR_TEXT_SECONDARY);

        card.addView(iconView);
        card.addView(titleText);
        card.addView(subTitleText);
        
        // Add ripple effect for tactile feedback
        RippleDrawable ripple = new RippleDrawable(
            ColorStateList.valueOf(Color.parseColor("#1A000000")), 
            bg, 
            null
        );
        card.setBackground(ripple);
        card.setClickable(true);
        card.setOnClickListener(listener);

        container.addView(card);
        return container;
    }

    private View createTimelineActivitySection() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(40));

        TextView title = new TextView(this);
        title.setText("Recent Activity");
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(COLOR_TEXT_PRIMARY);
        title.setPadding(dpToPx(5), 0, 0, dpToPx(15));
        layout.addView(title);

        // A single clean card wrapping the timeline
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_WHITE);
        bg.setCornerRadius(dpToPx(20));
        card.setBackground(bg);
        card.setElevation(dpToPx(4));
        card.setPadding(0, dpToPx(15), 0, dpToPx(15));
        
        card.addView(createTimelineItem("Order Approved", "Order #4092 shipped to warehouse", "Just now", COLOR_PRIMARY, true));
        card.addView(createTimelineItem("Payment Received", "$1,450.00 from John Doe", "2 hr ago", COLOR_ACCENT, true));
        card.addView(createTimelineItem("Stock Alert", "Pine wood inventory low", "1 day ago", Color.parseColor("#EF4444"), false));

        layout.addView(card);
        return layout;
    }

    private View createTimelineItem(String title, String subtitle, String time, int dotColor, boolean hasLineIndicator) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(10));
        
        // Timeline dot + line column
        LinearLayout timelineCol = new LinearLayout(this);
        timelineCol.setOrientation(LinearLayout.VERTICAL);
        timelineCol.setGravity(Gravity.CENTER_HORIZONTAL);
        
        // Dot
        View dot = new View(this);
        GradientDrawable dotBg = new GradientDrawable();
        dotBg.setShape(GradientDrawable.OVAL);
        dotBg.setColor(dotColor);
        dot.setBackground(dotBg);
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dpToPx(12), dpToPx(12));
        dotParams.setMargins(0, dpToPx(6), dpToPx(15), 0);
        
        // Line
        View line = new View(this);
        if (hasLineIndicator) {
            line.setBackgroundColor(Color.parseColor("#E5E7EB"));
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(dpToPx(2), ViewGroup.LayoutParams.MATCH_PARENT);
            lineParams.setMargins(0, dpToPx(4), dpToPx(15), dpToPx(4));
            line.setLayoutParams(lineParams);
        }

        timelineCol.addView(dot, dotParams);
        if(hasLineIndicator) timelineCol.addView(line);

        // Content
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        contentLayout.setLayoutParams(contentParams);

        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(15);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setTextColor(COLOR_TEXT_PRIMARY);

        TextView subTitleText = new TextView(this);
        subTitleText.setText(subtitle);
        subTitleText.setTextSize(13);
        subTitleText.setTextColor(COLOR_TEXT_SECONDARY);
        subTitleText.setPadding(0, dpToPx(2), 0, dpToPx(2));
        
        TextView timeText = new TextView(this);
        timeText.setText(time);
        timeText.setTextSize(11);
        timeText.setTextColor(Color.parseColor("#9CA3AF"));

        contentLayout.addView(titleText);
        contentLayout.addView(subTitleText);
        contentLayout.addView(timeText);

        item.addView(timelineCol);
        item.addView(contentLayout);
        
        return item;
    }

    private View createModernBottomNav() {
        LinearLayout navBar = new LinearLayout(this);
        navBar.setOrientation(LinearLayout.HORIZONTAL);
        navBar.setBackgroundColor(COLOR_WHITE);
        navBar.setElevation(dpToPx(16)); // cast strong shadow
        navBar.setPadding(0, dpToPx(10), 0, dpToPx(10));
        navBar.setWeightSum(4f);
        
        // Slight top border just in case
        GradientDrawable topBorderBg = new GradientDrawable();
        topBorderBg.setColor(COLOR_WHITE);
        navBar.setBackground(topBorderBg);
        
        navBar.addView(createModernNavTab("Home", "H", true, true));
        navBar.addView(createModernNavTab("Market", "M", false, false));
        navBar.addView(createModernNavTab("Orders", "O", false, false));
        navBar.addView(createModernNavTab("Profile", "P", false, false));

        return navBar;
    }

    private View createModernNavTab(String text, String letterIcon, boolean isActive, boolean isClickable) {
        LinearLayout tab = new LinearLayout(this);
        tab.setOrientation(LinearLayout.VERTICAL);
        tab.setGravity(Gravity.CENTER);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tab.setLayoutParams(params);
        tab.setPadding(dpToPx(6), dpToPx(8), dpToPx(6), dpToPx(8));
        
        int color = isActive ? COLOR_PRIMARY : Color.parseColor("#9CA3AF");

        TextView icon = new TextView(this);
        icon.setText(letterIcon);
        icon.setTextSize(20);
        icon.setTypeface(null, Typeface.BOLD);
        icon.setTextColor(color);

        // Active highlighted background Behind Icon
        if (isActive) {
            GradientDrawable activeBg = new GradientDrawable();
            activeBg.setColor(Color.parseColor("#D1FAE5")); // Light green
            activeBg.setCornerRadius(dpToPx(16));
            icon.setBackground(activeBg);
            icon.setPadding(dpToPx(20), dpToPx(4), dpToPx(20), dpToPx(4));
        } else {
            icon.setPadding(dpToPx(20), dpToPx(4), dpToPx(20), dpToPx(4));
        }

        TextView label = new TextView(this);
        label.setText(text);
        label.setTextSize(11);
        label.setTextColor(color);
        label.setPadding(0, dpToPx(4), 0, 0);

        if (isActive) {
            label.setTypeface(null, Typeface.BOLD);
        }

        tab.addView(icon);
        tab.addView(label);

        // Add ripple for tab
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
        tab.setBackgroundResource(outValue.resourceId);
        tab.setClickable(true);

        if (text.equals("Home") && !isActive) {
            tab.setOnClickListener(v -> {
                startActivity(new Intent(RealDashboardActivity.this, com.timbertrade.app.dashboard.RealDashboardActivity.class));
                finish();
            });
        } else if (text.equals("Market") && !isActive) {
            tab.setOnClickListener(v -> {
                startActivity(new Intent(RealDashboardActivity.this, com.timbertrade.app.marketplace.MarketplaceActivity.class));
            });
        } else if (text.equals("Orders") && !isActive) {
            tab.setOnClickListener(v -> {
                startActivity(new Intent(RealDashboardActivity.this, com.timbertrade.app.orders.NewOrderActivity.class));
            });
        } else if (text.equals("Profile") && !isActive) {
            tab.setOnClickListener(v -> {
                startActivity(new Intent(RealDashboardActivity.this, com.timbertrade.app.dashboard.ProfileActivity.class));
            });
        } else if (isActive) {
            // Already on this page
        }

        return tab;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }
}
