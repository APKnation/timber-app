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

import com.timbertrade.app.marketplace.MarketplaceActivity;
import com.timbertrade.app.orders.NewOrderActivity;

public class ProfileActivity extends Activity {

    private static final String TAG = "ProfileActivity";

    // Modern Color Palette
    private final int COLOR_PRIMARY = Color.parseColor("#059669");
    private final int COLOR_PRIMARY_DARK = Color.parseColor("#047857");
    private final int COLOR_BG = Color.parseColor("#F3F4F6");
    private final int COLOR_TEXT_PRIMARY = Color.parseColor("#1F2937");
    private final int COLOR_TEXT_SECONDARY = Color.parseColor("#6B7280");
    private final int COLOR_WHITE = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            createAdvancedProfileLayout();
        } catch (Exception e) {
            Log.e(TAG, "Error rendering advanced profile layout", e);
        }
    }

    private void createAdvancedProfileLayout() {
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(COLOR_BG);

        // 1. Decorative Header Background
        View headerBg = new View(this);
        GradientDrawable gradientBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{COLOR_PRIMARY, COLOR_PRIMARY_DARK}
        );
        headerBg.setBackground(gradientBg);
        
        RelativeLayout.LayoutParams headerBgParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(240)
        );
        headerBgParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(headerBg, headerBgParams);

        // Custom App Bar Toolbar
        int toolbarId = View.generateViewId();
        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setId(toolbarId);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setPadding(dpToPx(20), dpToPx(40), dpToPx(20), dpToPx(20));
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView titleText = new TextView(this);
        titleText.setText("My Profile");
        titleText.setTextSize(24);
        titleText.setTextColor(COLOR_WHITE);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleText.setLayoutParams(titleParams);
        toolbar.addView(titleText);
        
        RelativeLayout.LayoutParams toolbarParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        toolbarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(toolbar, toolbarParams);

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
        RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        scrollParams.addRule(RelativeLayout.BELOW, toolbarId);
        scrollParams.addRule(RelativeLayout.ABOVE, bottomNavId);

        LinearLayout scrollContent = new LinearLayout(this);
        scrollContent.setOrientation(LinearLayout.VERTICAL);
        scrollContent.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(40));
        
        // Profile Info Card (Overlapping header)
        scrollContent.addView(createProfileInfoCard());
        
        // Settings Categories
        scrollContent.addView(createSectionTitle("Account"));
        scrollContent.addView(createSettingsCard(new String[][]{
            {"Personal Details", "Update your info", "P"},
            {"Payment Methods", "Manage cards & accounts", "C"},
            {"Notifications", "Manage alerts", "N"}
        }));
        
        scrollContent.addView(createSectionTitle("App Settings"));
        scrollContent.addView(createSettingsCard(new String[][]{
            {"Language", "English (US)", "L"},
            {"Dark Mode", "System Default", "D"},
            {"Privacy & Security", "Password and PIN", "S"}
        }));
        
        // Logout Button
        scrollContent.addView(createLogoutButton());

        scrollView.addView(scrollContent);
        root.addView(scrollView, scrollParams);

        setContentView(root);
    }
    
    private View createProfileInfoCard() {
        // FrameLayout to let avatar overlap the card nicely
        FrameLayout frame = new FrameLayout(this);
        frame.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(20), dpToPx(60), dpToPx(20), dpToPx(20));
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_WHITE);
        bg.setCornerRadius(dpToPx(24));
        card.setBackground(bg);
        card.setElevation(dpToPx(8));
        
        FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, dpToPx(50), 0, 0); // Push card down to make room for avatar
        card.setLayoutParams(cardParams);
        
        // Texts inside Card
        TextView name = new TextView(this);
        name.setText("Atanasi Kafuka");
        name.setTextSize(22);
        name.setTextColor(COLOR_TEXT_PRIMARY);
        name.setTypeface(null, Typeface.BOLD);
        name.setGravity(Gravity.CENTER);
        
        TextView email = new TextView(this);
        email.setText("atanasi@timberapp.com");
        email.setTextSize(14);
        email.setTextColor(COLOR_TEXT_SECONDARY);
        email.setGravity(Gravity.CENTER);
        email.setPadding(0, dpToPx(4), 0, dpToPx(20));
        
        // Stats Row
        LinearLayout statsRow = new LinearLayout(this);
        statsRow.setOrientation(LinearLayout.HORIZONTAL);
        statsRow.setWeightSum(2f);
        
        statsRow.addView(createStatColumn("12", "Active Orders"));
        statsRow.addView(createStatColumn("Premium", "Tier Status"));
        
        card.addView(name);
        card.addView(email);
        card.addView(statsRow);
        
        frame.addView(card);
        
        // Floating Avatar
        TextView avatar = new TextView(this);
        avatar.setText("AK");
        avatar.setTextColor(COLOR_PRIMARY);
        avatar.setTextSize(32);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setGravity(Gravity.CENTER);
        
        GradientDrawable avatarBg = new GradientDrawable();
        avatarBg.setShape(GradientDrawable.OVAL);
        avatarBg.setColor(COLOR_WHITE);
        avatarBg.setStroke(dpToPx(4), COLOR_PRIMARY);
        avatar.setBackground(avatarBg);
        avatar.setElevation(dpToPx(12));
        
        FrameLayout.LayoutParams avatarParams = new FrameLayout.LayoutParams(dpToPx(100), dpToPx(100));
        avatarParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        avatar.setLayoutParams(avatarParams);
        
        frame.addView(avatar);
        
        return frame;
    }
    
    private View createStatColumn(String value, String label) {
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        col.setGravity(Gravity.CENTER);
        
        TextView valText = new TextView(this);
        valText.setText(value);
        valText.setTextSize(20);
        valText.setTypeface(null, Typeface.BOLD);
        valText.setTextColor(COLOR_PRIMARY);
        
        TextView labelText = new TextView(this);
        labelText.setText(label);
        labelText.setTextSize(12);
        labelText.setTextColor(COLOR_TEXT_SECONDARY);
        
        col.addView(valText);
        col.addView(labelText);
        return col;
    }

    private View createSectionTitle(String title) {
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setTextSize(16);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(COLOR_TEXT_PRIMARY);
        textView.setPadding(dpToPx(10), dpToPx(30), dpToPx(10), dpToPx(10));
        return textView;
    }

    private View createSettingsCard(String[][] items) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_WHITE);
        bg.setCornerRadius(dpToPx(20));
        card.setBackground(bg);
        card.setElevation(dpToPx(4));
        
        for (int i = 0; i < items.length; i++) {
            boolean isLast = (i == items.length - 1);
            card.addView(createSettingItem(items[i][0], items[i][1], items[i][2], isLast));
        }
        
        return card;
    }
    
    private View createSettingItem(String title, String subtitle, String iconLetter, boolean isLast) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        
        // Use ripple effect for rows
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        itemLayout.setBackgroundResource(outValue.resourceId);
        itemLayout.setClickable(true);
        itemLayout.setOnClickListener(v -> {
            Toast.makeText(this, title + " coming soon", Toast.LENGTH_SHORT).show();
        });
        
        LinearLayout contentRow = new LinearLayout(this);
        contentRow.setOrientation(LinearLayout.HORIZONTAL);
        contentRow.setPadding(dpToPx(20), dpToPx(15), dpToPx(20), dpToPx(15));
        contentRow.setGravity(Gravity.CENTER_VERTICAL);
        
        // Icon
        TextView icon = new TextView(this);
        icon.setText(iconLetter);
        icon.setTextColor(COLOR_PRIMARY);
        icon.setTextSize(16);
        icon.setTypeface(null, Typeface.BOLD);
        icon.setGravity(Gravity.CENTER);
        
        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setCornerRadius(dpToPx(10));
        iconBg.setColor(Color.parseColor("#D1FAE5")); // Light Green
        icon.setBackground(iconBg);
        
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(40), dpToPx(40));
        iconParams.setMargins(0, 0, dpToPx(15), 0);
        icon.setLayoutParams(iconParams);
        
        // Text Column
        LinearLayout textCol = new LinearLayout(this);
        textCol.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textCol.setLayoutParams(textParams);
        
        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(15);
        titleText.setTextColor(COLOR_TEXT_PRIMARY);
        titleText.setTypeface(null, Typeface.BOLD);
        
        TextView subText = new TextView(this);
        subText.setText(subtitle);
        subText.setTextSize(12);
        subText.setTextColor(COLOR_TEXT_SECONDARY);
        
        textCol.addView(titleText);
        textCol.addView(subText);
        
        // Right Arrow
        TextView arrow = new TextView(this);
        arrow.setText(">");
        arrow.setTextSize(16);
        arrow.setTextColor(Color.parseColor("#9CA3AF"));
        arrow.setTypeface(null, Typeface.BOLD);
        
        contentRow.addView(icon);
        contentRow.addView(textCol);
        contentRow.addView(arrow);
        
        itemLayout.addView(contentRow);
        
        if (!isLast) {
            View divider = new View(this);
            divider.setBackgroundColor(Color.parseColor("#F3F4F6"));
            LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1));
            divParams.setMargins(dpToPx(75), 0, dpToPx(20), 0); // Indent to align with text
            divider.setLayoutParams(divParams);
            itemLayout.addView(divider);
        }
        
        return itemLayout;
    }
    
    private View createLogoutButton() {
        TextView logoutBtn = new TextView(this);
        logoutBtn.setText("Log Out");
        logoutBtn.setTextColor(Color.parseColor("#EF4444")); // Red text
        logoutBtn.setTextSize(16);
        logoutBtn.setTypeface(null, Typeface.BOLD);
        logoutBtn.setGravity(Gravity.CENTER);
        logoutBtn.setPadding(dpToPx(20), dpToPx(16), dpToPx(20), dpToPx(16));
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#FEE2E2")); // Light red bg
        bg.setCornerRadius(dpToPx(16));
        logoutBtn.setBackground(bg);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dpToPx(40), 0, dpToPx(20));
        logoutBtn.setLayoutParams(params);
        
        logoutBtn.setClickable(true);
        logoutBtn.setOnClickListener(v -> finishAffinity());
        
        return logoutBtn;
    }

    private View createModernBottomNav() {
        LinearLayout navBar = new LinearLayout(this);
        navBar.setOrientation(LinearLayout.HORIZONTAL);
        navBar.setBackgroundColor(COLOR_WHITE);
        navBar.setElevation(dpToPx(16));
        navBar.setPadding(0, dpToPx(10), 0, dpToPx(10));
        navBar.setWeightSum(4f);
        
        GradientDrawable topBorderBg = new GradientDrawable();
        topBorderBg.setColor(COLOR_WHITE);
        navBar.setBackground(topBorderBg);
        
        navBar.addView(createModernNavTab("Home", "H", false));
        navBar.addView(createModernNavTab("Market", "M", false));
        navBar.addView(createModernNavTab("Orders", "O", false));
        navBar.addView(createModernNavTab("Profile", "P", true)); // Profile is active

        return navBar;
    }

    private View createModernNavTab(String text, String letterIcon, boolean isActive) {
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

        if (isActive) {
            GradientDrawable activeBg = new GradientDrawable();
            activeBg.setColor(Color.parseColor("#D1FAE5")); 
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

        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
        tab.setBackgroundResource(outValue.resourceId);
        tab.setClickable(true);

        if (text.equals("Home") && !isActive) {
            tab.setOnClickListener(v -> {
                startActivity(new Intent(ProfileActivity.this, RealDashboardActivity.class));
            });
        } else if (text.equals("Market") && !isActive) {
            tab.setOnClickListener(v -> {
                startActivity(new Intent(ProfileActivity.this, MarketplaceActivity.class));
            });
        } else if (text.equals("Orders") && !isActive) {
            tab.setOnClickListener(v -> {
                startActivity(new Intent(ProfileActivity.this, NewOrderActivity.class));
            });
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
