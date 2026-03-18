package com.timbertrade.app.marketplace;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MarketplaceActivity extends Activity {
    
    private static final String TAG = "MarketplaceActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starting MarketplaceActivity");
        
        try {
            super.onCreate(savedInstanceState);
            createMarketplaceLayout();
        } catch (Exception e) {
            Log.e(TAG, "Error in MarketplaceActivity: " + e.getMessage(), e);
        }
    }
    
    private void createMarketplaceLayout() {
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor("#F5F7FA")); // Light app background
        
        // Custom App Bar Toolbar
        int toolbarId = View.generateViewId();
        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setId(toolbarId);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setBackgroundColor(Color.parseColor("#2E7D32"));
        toolbar.setPadding(40, 50, 40, 50);
        toolbar.setElevation(12f);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        
        // Back Button (simulated)
        TextView backBtn = new TextView(this);
        backBtn.setText("< Back");
        backBtn.setTextColor(Color.WHITE);
        backBtn.setTextSize(16);
        backBtn.setPadding(0, 0, 40, 0);
        backBtn.setOnClickListener(v -> finish());
        
        TextView titleText = new TextView(this);
        titleText.setText("Marketplace");
        titleText.setTextSize(20);
        titleText.setTextColor(Color.WHITE);
        titleText.setTypeface(null, Typeface.BOLD);
        
        toolbar.addView(backBtn);
        toolbar.addView(titleText);
        
        RelativeLayout.LayoutParams toolbarParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        toolbarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(toolbar, toolbarParams);
        
        // Scroll Content
        ScrollView scrollView = new ScrollView(this);
        scrollView.setVerticalScrollBarEnabled(false);
        RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollParams.addRule(RelativeLayout.BELOW, toolbarId);
        
        LinearLayout scrollContent = new LinearLayout(this);
        scrollContent.setOrientation(LinearLayout.VERTICAL);
        scrollContent.setPadding(30, 40, 30, 60);
        
        // Categories Row
        scrollContent.addView(createSectionTitle("Categories"));
        LinearLayout categoryRow = new LinearLayout(this);
        categoryRow.setOrientation(LinearLayout.HORIZONTAL);
        categoryRow.setWeightSum(3f);
        categoryRow.addView(createCategoryChip("Hardwood", "#2E7D32", Color.WHITE));
        categoryRow.addView(createCategoryChip("Softwood", "#E8F5E9", Color.parseColor("#2E7D32")));
        categoryRow.addView(createCategoryChip("Plywood", "#E8F5E9", Color.parseColor("#2E7D32")));
        scrollContent.addView(categoryRow);
        
        // Featured Products
        scrollContent.addView(createSectionTitle("Featured Timber"));
        scrollContent.addView(createProductCard("Premium Oak Wood", "Grade A - Perfect for furniture", "$120 / unit", "#F57C00"));
        scrollContent.addView(createProductCard("Treated Pine", "Outdoor & Construction ready", "$85 / unit", "#1976D2"));
        scrollContent.addView(createProductCard("Mahogany Beams", "High durability structural beams", "$210 / unit", "#7B1FA2"));
        
        scrollView.addView(scrollContent);
        root.addView(scrollView, scrollParams);
        
        setContentView(root);
    }
    
    private View createSectionTitle(String title) {
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setTextSize(18);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(Color.parseColor("#212121"));
        textView.setPadding(10, 30, 10, 20);
        return textView;
    }
    
    private View createCategoryChip(String title, String bgColor, int textColor) {
        TextView chip = new TextView(this);
        chip.setText(title);
        chip.setTextColor(textColor);
        chip.setTextSize(13);
        chip.setTypeface(null, Typeface.BOLD);
        chip.setGravity(Gravity.CENTER);
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor(bgColor));
        bg.setCornerRadius(100f); // Fully rounded pill shape
        chip.setBackground(bg);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        params.setMargins(10, 10, 10, 10);
        chip.setPadding(0, 25, 0, 25);
        chip.setLayoutParams(params);
        chip.setElevation(4f);
        
        return chip;
    }

    private View createProductCard(String title, String desc, String price, String iconColor) {
        LinearLayout outerLayout = new LinearLayout(this);
        outerLayout.setPadding(10, 10, 10, 20);
        
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setPadding(30, 30, 30, 30);
        card.setGravity(Gravity.CENTER_VERTICAL);
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(24f);
        card.setBackground(bg);
        card.setElevation(8f);
        
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        card.setLayoutParams(cardParams);
        
        // Product Image (Simulated with Color Block)
        TextView icon = new TextView(this);
        icon.setText(title.substring(0, 1));
        icon.setTextColor(Color.WHITE);
        icon.setTextSize(24);
        icon.setTypeface(null, Typeface.BOLD);
        icon.setGravity(Gravity.CENTER);
        
        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setCornerRadius(16f);
        iconBg.setColor(Color.parseColor(iconColor));
        icon.setBackground(iconBg);
        
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(140, 140);
        iconParams.setMargins(0, 0, 30, 0);
        icon.setLayoutParams(iconParams);
        
        // Texts
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textLayout.setLayoutParams(textParams);
        
        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(16);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setTextColor(Color.parseColor("#212121"));
        
        TextView descText = new TextView(this);
        descText.setText(desc);
        descText.setTextSize(12);
        descText.setTextColor(Color.parseColor("#757575"));
        descText.setPadding(0, 5, 0, 10);
        
        TextView priceText = new TextView(this);
        priceText.setText(price);
        priceText.setTextSize(15);
        priceText.setTypeface(null, Typeface.BOLD);
        priceText.setTextColor(Color.parseColor("#2E7D32"));
        
        textLayout.addView(titleText);
        textLayout.addView(descText);
        textLayout.addView(priceText);
        
        card.addView(icon);
        card.addView(textLayout);
        
        outerLayout.addView(card);
        return outerLayout;
    }
}
