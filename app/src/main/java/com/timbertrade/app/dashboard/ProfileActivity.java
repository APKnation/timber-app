package com.timbertrade.app.dashboard;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup;

public class ProfileActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.parseColor("#F3F4F6"));
        mainLayout.setGravity(Gravity.CENTER);
        
        // Avatar
        TextView avatar = new TextView(this);
        avatar.setText("AK");
        avatar.setTextColor(Color.WHITE);
        avatar.setTextSize(40);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setGravity(Gravity.CENTER);
        
        GradientDrawable avatarBg = new GradientDrawable();
        avatarBg.setShape(GradientDrawable.OVAL);
        avatarBg.setColor(Color.parseColor("#059669"));
        avatar.setBackground(avatarBg);
        
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(250, 250);
        avatarParams.setMargins(0, 0, 0, 40);
        avatar.setLayoutParams(avatarParams);
        avatar.setElevation(10f);
        
        // Name
        TextView name = new TextView(this);
        name.setText("Atanasi Kafuka");
        name.setTextSize(24);
        name.setTypeface(null, Typeface.BOLD);
        name.setTextColor(Color.parseColor("#1F2937"));
        name.setGravity(Gravity.CENTER);
        
        // Email
        TextView email = new TextView(this);
        email.setText("atanasi@timberapp.com");
        email.setTextSize(16);
        email.setTextColor(Color.parseColor("#6B7280"));
        email.setGravity(Gravity.CENTER);
        email.setPadding(0, 10, 0, 50);
        
        // Back Button
        TextView backBtn = new TextView(this);
        backBtn.setText("← Go Back to Dashboard");
        backBtn.setTextColor(Color.parseColor("#059669"));
        backBtn.setTextSize(16);
        backBtn.setTypeface(null, Typeface.BOLD);
        backBtn.setPadding(20, 20, 20, 20);
        backBtn.setOnClickListener(v -> finish());
        
        mainLayout.addView(avatar);
        mainLayout.addView(name);
        mainLayout.addView(email);
        mainLayout.addView(backBtn);
        
        setContentView(mainLayout);
    }
}
