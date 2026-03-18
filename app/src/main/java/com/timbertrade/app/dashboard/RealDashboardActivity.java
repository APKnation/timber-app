package com.timbertrade.app.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.timbertrade.app.marketplace.MarketplaceFragment;
import com.timbertrade.app.orders.NewOrderFragment;

public class RealDashboardActivity extends AppCompatActivity {

    private static final String TAG = "RealDashboardActivity";

    private final int COLOR_PRIMARY = Color.parseColor("#059669");
    private final int COLOR_WHITE = Color.WHITE;

    private LinearLayout navBar;
    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            createSingleActivityLayout();
            switchTab(0); // Start on Home
        } catch (Exception e) {
            Log.e(TAG, "Error rendering Single Activity host", e);
        }
    }

    private void createSingleActivityLayout() {
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor("#F3F4F6"));

        // 1. Bottom Navigation Bar (Persistent)
        int bottomNavId = View.generateViewId();
        View bottomNav = createModernBottomNav();
        bottomNav.setId(bottomNavId);

        RelativeLayout.LayoutParams navParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        navParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        root.addView(bottomNav, navParams);

        // 2. Fragment Container (Fills rest of screen)
        FrameLayout fragmentContainer = new FrameLayout(this);
        fragmentContainer.setId(android.R.id.content); // Use default content id for Fragment targeting
        
        RelativeLayout.LayoutParams containerParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        containerParams.addRule(RelativeLayout.ABOVE, bottomNavId);
        root.addView(fragmentContainer, containerParams);

        setContentView(root);
    }

    private View createModernBottomNav() {
        navBar = new LinearLayout(this);
        navBar.setOrientation(LinearLayout.HORIZONTAL);
        navBar.setBackgroundColor(COLOR_WHITE);
        navBar.setElevation(dpToPx(16));
        navBar.setPadding(0, dpToPx(10), 0, dpToPx(10));
        navBar.setWeightSum(4f);

        refreshBottomNav(0);
        return navBar;
    }

    private void refreshBottomNav(int activeIndex) {
        navBar.removeAllViews();
        navBar.addView(createModernNavTab("Home", "H", activeIndex == 0, 0));
        navBar.addView(createModernNavTab("Market", "M", activeIndex == 1, 1));
        navBar.addView(createModernNavTab("Orders", "O", activeIndex == 2, 2));
        navBar.addView(createModernNavTab("Profile", "P", activeIndex == 3, 3));
    }

    private View createModernNavTab(String text, String letterIcon, boolean isActive, int tabIndex) {
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
        
        tab.setOnClickListener(v -> switchTab(tabIndex));

        return tab;
    }

    public void switchTab(int index) {
        if(currentTab == index && getSupportFragmentManager().findFragmentById(android.R.id.content) != null) return;
        currentTab = index;
        refreshBottomNav(index);

        Fragment selectedFragment = null;
        switch (index) {
            case 0: selectedFragment = new DashboardFragment(); break;
            case 1: selectedFragment = new MarketplaceFragment(); break;
            case 2: selectedFragment = new NewOrderFragment(); break;
            case 3: selectedFragment = new ProfileFragment(); break;
        }

        if (selectedFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(android.R.id.content, selectedFragment);
            transaction.commit();
        }
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()
        );
    }
}
