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
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.timbertrade.app.dashboard.RealDashboardActivity;
import com.timbertrade.app.models.Order;
import com.timbertrade.app.utils.DataManager;

import java.util.Locale;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            return createAdvancedLayout();
        } catch (Exception e) {
            Log.e(TAG, "Error rendering marketplace", e);
            return new FrameLayout(getContext());
        }
    }
    
    private void switchTab(int tabIndex) {
        if (getActivity() instanceof RealDashboardActivity) {
            ((RealDashboardActivity) getActivity()).switchTab(tabIndex);
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
        
        // Custom App Bar Toolbar
        int toolbarId = View.generateViewId();
        LinearLayout toolbar = new LinearLayout(getContext());
        toolbar.setId(toolbarId);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setPadding(dpToPx(20), dpToPx(48), dpToPx(20), dpToPx(20));
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        
        // Premium Back Button
        TextView backBtn = new TextView(getContext());
        backBtn.setText("←");
        backBtn.setTextColor(COLOR_PRIMARY);
        backBtn.setTextSize(22);
        backBtn.setGravity(Gravity.CENTER);
        backBtn.setTypeface(null, Typeface.BOLD);
        
        GradientDrawable backBg = new GradientDrawable();
        backBg.setShape(GradientDrawable.OVAL);
        backBg.setColor(COLOR_WHITE);
        backBtn.setBackground(backBg);
        backBtn.setElevation(dpToPx(4));
        
        LinearLayout.LayoutParams backParams = new LinearLayout.LayoutParams(dpToPx(44), dpToPx(44));
        backParams.setMargins(0, 0, dpToPx(16), 0);
        backBtn.setLayoutParams(backParams);
        
        backBtn.setClickable(true);
        backBtn.setOnClickListener(v -> switchTab(0));
        
        TextView titleText = new TextView(getContext());
        titleText.setText("Marketplace");
        titleText.setTextSize(26);
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
        card.setPadding(dpToPx(28), dpToPx(32), dpToPx(28), dpToPx(32));
        
        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.BL_TR,
                new int[]{Color.parseColor("#064E3B"), Color.parseColor("#059669"), Color.parseColor("#10B981")}
        );
        bg.setCornerRadius(dpToPx(28));
        card.setBackground(bg);
        card.setElevation(dpToPx(16));
        
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, dpToPx(24));
        card.setLayoutParams(cardParams);
        
        // Tag line
        TextView tagText = new TextView(getContext());
        tagText.setText("✦  LIMITED TIME OFFER");
        tagText.setTextSize(11);
        tagText.setTypeface(null, Typeface.BOLD);
        tagText.setTextColor(Color.parseColor("#6EE7B7"));
        tagText.setLetterSpacing(0.08f);
        tagText.setPadding(0, 0, 0, dpToPx(12));
        
        TextView titleText = new TextView(getContext());
        titleText.setText("20% Off Bulk Orders");
        titleText.setTextSize(26);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setTextColor(Color.WHITE);
        titleText.setPadding(0, 0, 0, dpToPx(8));
        
        TextView descText = new TextView(getContext());
        descText.setText("Valid on orders above 500 units of any grade.");
        descText.setTextSize(13);
        descText.setTextColor(Color.parseColor("#A7F3D0"));
        descText.setPadding(0, 0, 0, dpToPx(20));

        // CTA Button
        TextView ctaBtn = new TextView(getContext());
        ctaBtn.setText("Explore Deals  →");
        ctaBtn.setTextSize(14);
        ctaBtn.setTypeface(null, Typeface.BOLD);
        ctaBtn.setTextColor(Color.parseColor("#064E3B"));
        ctaBtn.setPadding(dpToPx(24), dpToPx(12), dpToPx(24), dpToPx(12));
        GradientDrawable ctaBg = new GradientDrawable();
        ctaBg.setColor(Color.WHITE);
        ctaBg.setCornerRadius(dpToPx(100));
        ctaBtn.setBackground(ctaBg);
        ctaBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        
        card.addView(tagText);
        card.addView(titleText);
        card.addView(descText);
        card.addView(ctaBtn);
        
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
        outerLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams outerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        outerParams.setMargins(0, 0, 0, dpToPx(16));
        outerLayout.setLayoutParams(outerParams);

        // Outer card
        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(COLOR_WHITE);
        cardBg.setCornerRadius(dpToPx(24));
        card.setBackground(cardBg);
        card.setElevation(dpToPx(6));
        card.setClipToOutline(true);
        card.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Top colored banner
        LinearLayout topBanner = new LinearLayout(getContext());
        topBanner.setOrientation(LinearLayout.HORIZONTAL);
        topBanner.setGravity(Gravity.CENTER_VERTICAL);
        topBanner.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
        GradientDrawable bannerBg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.parseColor(iconBgColorStr), adjustAlpha(Color.parseColor(iconBgColorStr), 0.5f)}
        );
        bannerBg.setCornerRadii(new float[]{dpToPx(24), dpToPx(24), 0, 0, 0, 0, 0, 0});
        topBanner.setBackground(bannerBg);

        // Letter icon
        TextView icon = new TextView(getContext());
        icon.setText(title.substring(0, 1));
        icon.setTextColor(Color.parseColor(iconTextColorStr));
        icon.setTextSize(28);
        icon.setTypeface(null, Typeface.BOLD);
        icon.setGravity(Gravity.CENTER);
        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setColor(Color.WHITE);
        iconBg.setCornerRadius(dpToPx(16));
        icon.setBackground(iconBg);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(56), dpToPx(56));
        iconParams.setMargins(0, 0, dpToPx(16), 0);
        icon.setLayoutParams(iconParams);

        // Title + in stock horizontally arranged
        LinearLayout bannerTextCol = new LinearLayout(getContext());
        bannerTextCol.setOrientation(LinearLayout.VERTICAL);
        bannerTextCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView titleText = new TextView(getContext());
        titleText.setText(title);
        titleText.setTextSize(16);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setTextColor(Color.parseColor(iconTextColorStr));

        TextView inStockBadge = new TextView(getContext());
        inStockBadge.setText("✓ In Stock");
        inStockBadge.setTextSize(11);
        inStockBadge.setTypeface(null, Typeface.BOLD);
        inStockBadge.setTextColor(COLOR_PRIMARY);
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        badgeParams.setMargins(0, dpToPx(4), 0, 0);
        inStockBadge.setLayoutParams(badgeParams);
        GradientDrawable stockBg = new GradientDrawable();
        stockBg.setColor(Color.parseColor("#ECFDF5"));
        stockBg.setCornerRadius(dpToPx(100));
        inStockBadge.setBackground(stockBg);
        inStockBadge.setPadding(dpToPx(8), dpToPx(2), dpToPx(8), dpToPx(2));

        bannerTextCol.addView(titleText);
        bannerTextCol.addView(inStockBadge);
        topBanner.addView(icon);
        topBanner.addView(bannerTextCol);
        card.addView(topBanner);

        // Bottom section: desc + price + CTA
        LinearLayout bottomSection = new LinearLayout(getContext());
        bottomSection.setOrientation(LinearLayout.HORIZONTAL);
        bottomSection.setGravity(Gravity.CENTER_VERTICAL);
        bottomSection.setPadding(dpToPx(20), dpToPx(16), dpToPx(20), dpToPx(16));

        LinearLayout textCol = new LinearLayout(getContext());
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView descText = new TextView(getContext());
        descText.setText(desc);
        descText.setTextSize(13);
        descText.setTextColor(COLOR_TEXT_SECONDARY);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        descParams.setMargins(0, 0, 0, dpToPx(6));
        descText.setLayoutParams(descParams);

        TextView priceText = new TextView(getContext());
        priceText.setText(price);
        priceText.setTextSize(18);
        priceText.setTypeface(null, Typeface.BOLD);
        priceText.setTextColor(COLOR_TEXT_PRIMARY);

        textCol.addView(descText);
        textCol.addView(priceText);

        // Order Now chip
        TextView orderChip = new TextView(getContext());
        orderChip.setText("Order →");
        orderChip.setTextSize(13);
        orderChip.setTypeface(null, Typeface.BOLD);
        orderChip.setTextColor(COLOR_WHITE);
        orderChip.setPadding(dpToPx(16), dpToPx(10), dpToPx(16), dpToPx(10));
        GradientDrawable chipBg = new GradientDrawable();
        chipBg.setColor(COLOR_PRIMARY);
        chipBg.setCornerRadius(dpToPx(100));
        orderChip.setBackground(chipBg);
        orderChip.setClickable(true);
        orderChip.setOnClickListener(v -> {
            try {
                double priceVal = Double.parseDouble(price.replaceAll("[^0-9.]", ""));
                showOrderConfirmationSheet(title, priceVal);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        bottomSection.addView(textCol);
        bottomSection.addView(orderChip);
        card.addView(bottomSection);

        outerLayout.addView(card);
        return outerLayout;
    }

    private int adjustAlpha(int color, float factor) {
        int alpha = Math.min(255, Math.round(Color.alpha(color) * factor + 80));
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                requireContext().getResources().getDisplayMetrics()
        );
    }
    
    private void showOrderConfirmationSheet(String woodName, double unitPrice) {
        final BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dpToPx(24), dpToPx(32), dpToPx(24), dpToPx(32));
        root.setBackgroundColor(COLOR_WHITE);
        
        // Handle visual cue
        View handle = new View(requireContext());
        handle.setBackgroundColor(Color.LTGRAY);
        LinearLayout.LayoutParams handleParams = new LinearLayout.LayoutParams(dpToPx(44), dpToPx(4));
        handleParams.gravity = Gravity.CENTER_HORIZONTAL;
        handleParams.bottomMargin = dpToPx(24);
        root.addView(handle, handleParams);

        TextView title = new TextView(requireContext());
        title.setText("Confirm Your Order");
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(COLOR_TEXT_PRIMARY);
        title.setPadding(0, 0, 0, dpToPx(4));
        root.addView(title);

        TextView itemTv = new TextView(requireContext());
        itemTv.setText(woodName);
        itemTv.setTextSize(16);
        itemTv.setTextColor(COLOR_PRIMARY);
        itemTv.setPadding(0, 0, 0, dpToPx(24));
        root.addView(itemTv);

        TextInputLayout tilQty = new TextInputLayout(requireContext(), null, com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
        tilQty.setHint("Quantity to Order");
        float r = (float) dpToPx(16);
        tilQty.setBoxCornerRadii(r, r, r, r);
        tilQty.setBoxStrokeColor(COLOR_PRIMARY);
        tilQty.setHintTextColor(android.content.res.ColorStateList.valueOf(COLOR_PRIMARY));
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dpToPx(16);
        tilQty.setLayoutParams(params);

        TextInputEditText etQty = new TextInputEditText(tilQty.getContext());
        etQty.setText("10"); 
        etQty.setHint("Qty");
        etQty.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        etQty.setTextSize(16);
        etQty.setTextColor(COLOR_TEXT_PRIMARY);
        tilQty.addView(etQty);
        root.addView(tilQty);

        MaterialButton btnConfirm = new MaterialButton(requireContext());
        btnConfirm.setText("Place Order");
        btnConfirm.setBackgroundTintList(android.content.res.ColorStateList.valueOf(COLOR_PRIMARY));
        btnConfirm.setTextColor(Color.WHITE);
        btnConfirm.setCornerRadius(dpToPx(16));
        btnConfirm.setPadding(0, dpToPx(16), 0, dpToPx(16));
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParams.topMargin = dpToPx(16);
        btnConfirm.setLayoutParams(btnParams);
        
        btnConfirm.setOnClickListener(v -> {
            String qtyStr = etQty.getText().toString().trim();
            if (qtyStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int quantity = Integer.parseInt(qtyStr);
                String today = new java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new java.util.Date());
                
                Order newOrder = new Order("My Account", woodName, quantity, unitPrice, today, "Order from Marketplace");
                DataManager.getInstance().addOrder(newOrder);
                
                dialog.dismiss();
                Toast.makeText(getContext(), "Order placed successfully! 🎊", Toast.LENGTH_LONG).show();
                
                // Redirect to Orders tab
                switchTab(2); 
            } catch (Exception e) {
                Toast.makeText(getContext(), "Order failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        root.addView(btnConfirm);
        dialog.setContentView(root);
        dialog.show();
    }

    private class HorizontalScrollView extends android.widget.HorizontalScrollView {
        public HorizontalScrollView(android.content.Context context) {
            super(context);
        }
    }
}
