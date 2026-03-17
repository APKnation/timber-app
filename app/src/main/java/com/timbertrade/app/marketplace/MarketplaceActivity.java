package com.timbertrade.app.marketplace;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MarketplaceActivity extends Activity {
    
    private static final String TAG = "MarketplaceActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starting MarketplaceActivity");
        
        try {
            super.onCreate(savedInstanceState);
            
            // Create marketplace layout
            createMarketplaceLayout();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in MarketplaceActivity: " + e.getMessage(), e);
        }
    }
    
    private void createMarketplaceLayout() {
        // Main container
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        
        // Header
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.VERTICAL);
        headerLayout.setBackgroundColor(Color.parseColor("#2E7D32"));
        headerLayout.setPadding(30, 40, 30, 40);
        
        TextView titleText = new TextView(this);
        titleText.setText("Marketplace");
        titleText.setTextSize(24);
        titleText.setTextColor(Color.WHITE);
        titleText.setGravity(Gravity.CENTER);
        headerLayout.addView(titleText);
        
        TextView subtitleText = new TextView(this);
        subtitleText.setText("Browse Timber Products");
        subtitleText.setTextSize(14);
        subtitleText.setTextColor(Color.parseColor("#E8F5E8"));
        subtitleText.setGravity(Gravity.CENTER);
        subtitleText.setPadding(0, 10, 0, 0);
        headerLayout.addView(subtitleText);
        
        mainLayout.addView(headerLayout);
        
        // Content
        TextView contentText = new TextView(this);
        contentText.setText("Marketplace Features:\n\n• Browse timber products\n• Filter by wood type\n• View product details\n• Compare prices\n• Place orders\n\nComing soon with full functionality!");
        contentText.setTextSize(16);
        contentText.setTextColor(Color.parseColor("#333333"));
        contentText.setPadding(30, 30, 30, 30);
        contentText.setGravity(Gravity.CENTER);
        mainLayout.addView(contentText);
        
        setContentView(mainLayout);
    }
}
