package com.timbertrade.app.marketplace;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.content.Context;
import android.widget.FrameLayout;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MarketplaceFragment extends Fragment {
    
    private static final String TAG = "MarketplaceActivity";
    
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
            createAdvancedMarketplaceLayout();
        } catch (Exception e) {
            Log.e(TAG, "Error rendering marketplace", e);
        }
    }
    
    private View createAdvancedLayout()  {
        RelativeLayout root = new RelativeLayout(getContext());
        root.setBackgroundColor(COLOR_BG);
        
        // 1. Decorative Header Background
        View headerBg = new View(getContext());
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
        
        // Custom App Bar Toolbar (Transparent so it blends with header background)
        int toolbarId = View.generateViewId();
        LinearLayout toolbar = new LinearLayout(getContext());
        toolbar.setId(toolbarId);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setPadding(dpToPx(20), dpToPx(40), dpToPx(20), dpToPx(20));
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        
        // Back Button
        TextView backBtn = new TextView(getContext());
        backBtn.setText("< Back");
        backBtn.setTextColor(Color.parseColor("#D1FAE5")); // Light green
        backBtn.setTextSize(16);
        backBtn.setPadding(0, 0, dpToPx(20), 0);
        
        // Add ripple logic for back button
        TypedValue outValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
        backBtn.setBackgroundResource(outValue.resourceId);
        backBtn.setClickable(true);
        backBtn.setOnClickListener(v -> if(getActivity() != null) getActivity().onBackPressed());
        
        TextView titleText = new TextView(getContext());
        titleText.setText("Marketplace");
        titleText.setTextSize(24);
        titleText.setTextColor(COLOR_WHITE);
        titleText.setTypeface(null, Typeface.BOLD);
        
        toolbar.addView(backBtn);
        toolbar.addView(titleText);
        
        RelativeLayout.LayoutParams toolbarParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        toolbarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(toolbar, toolbarParams);
        
        // Scroll Content
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setVerticalScrollBarEnabled(false);
        RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollParams.addRule(RelativeLayout.BELOW, toolbarId);
        
        LinearLayout scrollContent = new LinearLayout(getContext());
        scrollContent.setOrientation(LinearLayout.VERTICAL);
        scrollContent.setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(40));
        
        // Main Featured Banner Card that overlaps slightly
        scrollContent.addView(createPromotionBanner());
        
        // Categories Row
        scrollContent.addView(createSectionTitle("Categories"));
        HorizontalScrollView hScroll = new HorizontalScrollView(getContext());
        hScroll.setHorizontalScrollBarEnabled(false);
        hScroll.setClipToPadding(false);
        hScroll.setPadding(dpToPx(4), dpToPx(10), dpToPx(4), dpToPx(10));
        
        LinearLayout categoryRow = new LinearLayout(getContext());
        categoryRow.setOrientation(LinearLayout.HORIZONTAL);
        categoryRow.addView(createCategoryChip("All Wood", COLOR_PRIMARY, COLOR_WHITE));
        categoryRow.addView(createCategoryChip("Hardwood", COLOR_WHITE, COLOR_TEXT_PRIMARY));
        categoryRow.addView(createCategoryChip("Softwood", COLOR_WHITE, COLOR_TEXT_PRIMARY));
        categoryRow.addView(createCategoryChip("Plywood", COLOR_WHITE, COLOR_TEXT_PRIMARY));
        
        hScroll.addView(categoryRow);
        scrollContent.addView(hScroll);
        
        // Products List
        scrollContent.addView(createSectionTitle("Featured Timber"));
        scrollContent.addView(createProductCard("Premium Oak Wood", "Grade A - Perfect for furniture", "$120.00 / unit", "#FDE68A", "#D97706"));
        scrollContent.addView(createProductCard("Treated Pine", "Outdoor & Construction ready", "$85.00 / unit", "#BFDBFE", "#2563EB"));
        scrollContent.addView(createProductCard("Mahogany Beams", "High durability structural beams", "$210.00 / unit", "#FBCFE8", "#DB2777"));
        
        scrollView.addView(scrollContent);
        root.addView(scrollView, scrollParams);
        
        return root;
    }
    
    private View createPromotionBanner() {
        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(24), dpToPx(30), dpToPx(24), dpToPx(30));
        
        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.parseColor("#10B981"), Color.parseColor("#059669")}
        );
        bg.setCornerRadius(dpToPx(24));
        card.setBackground(bg);
        card.setElevation(dpToPx(12));
        
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, dpToPx(15));
        card.setLayoutParams(cardParams);
        
        TextView tagText = new TextView(getContext());
        tagText.setText("SPECIAL OFFER");
        tagText.setTextSize(12);
        tagText.setTypeface(null, Typeface.BOLD);
        tagText.setTextColor(Color.parseColor("#A7F3D0"));
        tagText.setPadding(0, 0, 0, dpToPx(10));
        
        TextView titleText = new TextView(getContext());
        titleText.setText("Get 20% off Bulk Orders");
        titleText.setTextSize(22);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setTextColor(COLOR_WHITE);
        titleText.setPadding(0, 0, 0, dpToPx(8));
        
        TextView descText = new TextView(getContext());
        descText.setText("Valid on orders over 500 units.");
        descText.setTextSize(14);
        descText.setTextColor(Color.parseColor("#D1FAE5"));
        
        card.addView(tagText);
        card.addView(titleText);
        card.addView(descText);
        
        return card;
    }

    private View createSectionTitle(String title) {
        TextView textView = new TextView(getContext());
        textView.setText(title);
        textView.setTextSize(18);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(COLOR_TEXT_PRIMARY);
        textView.setPadding(0, dpToPx(10), 0, dpToPx(5));
        return textView;
    }
    
    private View createCategoryChip(String title, int bgColor, int textColor) {
        TextView chip = new TextView(getContext());
        chip.setText(title);
        chip.setTextColor(textColor);
        chip.setTextSize(14);
        chip.setTypeface(null, Typeface.BOLD);
        chip.setGravity(Gravity.CENTER);
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(bgColor);
        bg.setCornerRadius(dpToPx(100)); // Fully rounded
        chip.setBackground(bg);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, dpToPx(12), 0);
        chip.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
        chip.setLayoutParams(params);
        chip.setElevation(dpToPx(2));
        
        // Add ripple
        TypedValue outValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
        chip.setClickable(true);
        chip.setForeground(requireContext().getResources().getDrawable(outValue.resourceId));
        
        return chip;
    }

    private View createProductCard(String title, String desc, String price, String iconBgColorStr, String iconTextColorStr) {
        LinearLayout outerLayout = new LinearLayout(getContext());
        outerLayout.setPadding(0, dpToPx(10), 0, dpToPx(10));
        
        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setPadding(dpToPx(20), dpToPx(24), dpToPx(20), dpToPx(24));
        card.setGravity(Gravity.CENTER_VERTICAL);
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_WHITE);
        bg.setCornerRadius(dpToPx(20));
        card.setBackground(bg);
        card.setElevation(dpToPx(6));
        
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        card.setLayoutParams(cardParams);
        
        // Product Icon Element
        TextView icon = new TextView(getContext());
        icon.setText(title.substring(0, 1));
        icon.setTextColor(Color.parseColor(iconTextColorStr));
        icon.setTextSize(26);
        icon.setTypeface(null, Typeface.BOLD);
        icon.setGravity(Gravity.CENTER);
        
        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setCornerRadius(dpToPx(16));
        iconBg.setColor(Color.parseColor(iconBgColorStr));
        icon.setBackground(iconBg);
        
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(70), dpToPx(70));
        iconParams.setMargins(0, 0, dpToPx(20), 0);
        icon.setLayoutParams(iconParams);
        
        // Product Texts
        LinearLayout textLayout = new LinearLayout(getContext());
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textLayout.setLayoutParams(textParams);
        
        TextView titleText = new TextView(getContext());
        titleText.setText(title);
        titleText.setTextSize(17);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setTextColor(COLOR_TEXT_PRIMARY);
        
        TextView descText = new TextView(getContext());
        descText.setText(desc);
        descText.setTextSize(13);
        descText.setTextColor(COLOR_TEXT_SECONDARY);
        descText.setPadding(0, dpToPx(4), 0, dpToPx(10));
        
        TextView priceText = new TextView(getContext());
        priceText.setText(price);
        priceText.setTextSize(16);
        priceText.setTypeface(null, Typeface.BOLD);
        priceText.setTextColor(COLOR_PRIMARY);
        
        textLayout.addView(titleText);
        textLayout.addView(descText);
        textLayout.addView(priceText);
        
        // Navigation Arrow right
        TextView arrow = new TextView(getContext());
        arrow.setText(">");
        arrow.setTextSize(18);
        arrow.setTextColor(Color.parseColor("#9CA3AF"));
        arrow.setTypeface(null, Typeface.BOLD);
        
        card.addView(icon);
        card.addView(textLayout);
        card.addView(arrow);
        
        // Ripple
        TypedValue outValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        card.setBackgroundResource(outValue.resourceId);
        GradientDrawable finalBg = new GradientDrawable();
        finalBg.setColor(COLOR_WHITE);
        finalBg.setCornerRadius(dpToPx(20));
        android.graphics.drawable.RippleDrawable ripple = new android.graphics.drawable.RippleDrawable(
            android.content.res.ColorStateList.valueOf(Color.parseColor("#1A000000")),
            finalBg,
            null
        );
        card.setBackground(ripple);
        card.setClickable(true);
        
        outerLayout.addView(card);
        return outerLayout;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                requireContext().getResources().getDisplayMetrics()
        );
    }
    
    // Class inside required to use HorizontalScrollView
    private class HorizontalScrollView extends android.widget.HorizontalScrollView {
        public HorizontalScrollView(android.content.Context context) {
            super(context);
        }
    }
}
