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
    
    private static final String TAG = "MarketplaceFragment";
    
    // Modern Color Palette
    private final int COLOR_PRIMARY = Color.parseColor("#059669"); 
    private final int COLOR_PRIMARY_DARK = Color.parseColor("#047857");
    private final int COLOR_BG = Color.parseColor("#F3F4F6"); 
    private final int COLOR_TEXT_PRIMARY = Color.parseColor("#1F2937");
    private final int COLOR_TEXT_SECONDARY = Color.parseColor("#6B7280");
    private final int COLOR_WHITE = Color.WHITE;
    
    private String selectedCategory = "All Wood";
    private LinearLayout categoryRow;
    private LinearLayout productsContainer;

    private static class Product {
        String name, desc, price, iconBg, iconText, category;
        Product(String cat, String n, String d, String p, String ib, String it) {
            this.category = cat; this.name = n; this.desc = d; this.price = p; this.iconBg = ib; this.iconText = it;
        }
    }

    private final Product[] allProducts = {
        new Product("Hardwood", "Premium Oak Wood", "Grade A - Perfect for furniture", "$120.00", "#FDE68A", "#D97706"),
        new Product("Softwood", "Treated Pine", "Outdoor & Construction ready", "$85.00", "#BFDBFE", "#2563EB"),
        new Product("Hardwood", "Mahogany Beams", "High durability structural beams", "$210.00", "#FBCFE8", "#DB2777"),
        new Product("Plywood",  "Marine Plywood", "Water resistant grade", "$65.00", "#D1FAE5", "#059669"),
        new Product("Softwood", "Cedar Decking", "Natural rot resistance", "$145.00", "#FFEDD5", "#C2410C"),
        new Product("Plywood",  "Standard Plywood", "General construction use", "$45.00", "#F3F4F6", "#374151")
    };

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
        toolbar.setPadding(dpToPx(20), dpToPx(56), dpToPx(20), dpToPx(20));
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
        
        scrollContent.addView(createPromotionBanner());
        
        // Categories Row
        scrollContent.addView(createSectionTitle("Product Categories"));
        HorizontalScrollView hScroll = new HorizontalScrollView(getContext());
        hScroll.setHorizontalScrollBarEnabled(false);
        hScroll.setClipToPadding(false);
        hScroll.setPadding(dpToPx(4), dpToPx(10), dpToPx(4), dpToPx(10));
        
        categoryRow = new LinearLayout(getContext());
        categoryRow.setOrientation(LinearLayout.HORIZONTAL);
        updateCategoryRow();
        
        hScroll.addView(categoryRow);
        scrollContent.addView(hScroll);
        
        // Products List
        scrollContent.addView(createSectionTitle("Featured Timber"));
        productsContainer = new LinearLayout(getContext());
        productsContainer.setOrientation(LinearLayout.VERTICAL);
        refreshProductsList();
        scrollContent.addView(productsContainer);
        
        scrollView.addView(scrollContent);
        root.addView(scrollView, scrollParams);
        
        return root;
    }

    private void updateCategoryRow() {
        categoryRow.removeAllViews();
        String[] cats = {"All Wood", "Hardwood", "Softwood", "Plywood"};
        for (String cat : cats) {
            boolean active = cat.equals(selectedCategory);
            categoryRow.addView(createCategoryChip(cat, active));
        }
    }

    private void refreshProductsList() {
        productsContainer.removeAllViews();
        for (Product p : allProducts) {
            if (selectedCategory.equals("All Wood") || p.category.equals(selectedCategory)) {
                productsContainer.addView(createProductCard(p.name, p.desc, p.price + " / unit", p.iconBg, p.iconText));
            }
        }
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
        descText.setText("Valid on orders above 500 units.");
        descText.setTextSize(13);
        descText.setTextColor(Color.parseColor("#A7F3D0"));
        descText.setPadding(0, 0, 0, dpToPx(20));

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
        textView.setPadding(0, dpToPx(10), 0, dpToPx(12));
        return textView;
    }
    
    private View createCategoryChip(String title, boolean isActive) {
        TextView chip = new TextView(getContext());
        chip.setText(title);
        chip.setTextColor(isActive ? COLOR_WHITE : COLOR_TEXT_PRIMARY);
        chip.setTextSize(14);
        chip.setTypeface(null, Typeface.BOLD);
        chip.setGravity(Gravity.CENTER);
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(isActive ? COLOR_PRIMARY : COLOR_WHITE);
        bg.setCornerRadius(dpToPx(100));
        chip.setBackground(bg);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, dpToPx(12), 0);
        chip.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
        chip.setLayoutParams(params);
        chip.setElevation(dpToPx(active ? 4 : 2));
        
        chip.setClickable(true);
        chip.setOnClickListener(v -> {
            selectedCategory = title;
            updateCategoryRow();
            refreshProductsList();
        });
        
        return chip;
    }

    private View createProductCard(String title, String desc, String price, String iconBgColorStr, String iconTextColorStr) {
        LinearLayout outerLayout = new LinearLayout(getContext());
        outerLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams outerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        outerParams.setMargins(0, 0, 0, dpToPx(16));
        outerLayout.setLayoutParams(outerParams);

        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(COLOR_WHITE);
        cardBg.setCornerRadius(dpToPx(24));
        card.setBackground(cardBg);
        card.setElevation(dpToPx(6));
        card.setClipToOutline(true);

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
        GradientDrawable stockBg = new GradientDrawable();
        stockBg.setColor(Color.parseColor("#ECFDF5"));
        stockBg.setCornerRadius(dpToPx(100));
        inStockBadge.setBackground(stockBg);
        inStockBadge.setPadding(dpToPx(8), dpToPx(2), dpToPx(8), dpToPx(2));
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        badgeParams.topMargin = dpToPx(4);
        inStockBadge.setLayoutParams(badgeParams);

        bannerTextCol.addView(titleText);
        bannerTextCol.addView(inStockBadge);
        topBanner.addView(icon);
        topBanner.addView(bannerTextCol);
        card.addView(topBanner);

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

        TextView priceText = new TextView(getContext());
        priceText.setText(price);
        priceText.setTextSize(18);
        priceText.setTypeface(null, Typeface.BOLD);
        priceText.setTextColor(COLOR_TEXT_PRIMARY);

        textCol.addView(descText);
        textCol.addView(priceText);

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
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, requireContext().getResources().getDisplayMetrics());
    }
    
    private void showOrderConfirmationSheet(String woodName, double unitPrice) {
        final BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dpToPx(24), dpToPx(32), dpToPx(24), dpToPx(32));
        root.setBackgroundColor(COLOR_WHITE);
        
        View handle = new View(requireContext());
        handle.setBackgroundColor(Color.LTGRAY);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(dpToPx(44), dpToPx(4));
        hp.gravity = Gravity.CENTER_HORIZONTAL; hp.bottomMargin = dpToPx(24);
        root.addView(handle, hp);

        TextView title = new TextView(requireContext());
        title.setText("Confirm Order"); title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD); title.setTextColor(COLOR_TEXT_PRIMARY);
        root.addView(title);

        TextView itemTv = new TextView(requireContext());
        itemTv.setText(woodName); itemTv.setTextSize(16);
        itemTv.setTextColor(COLOR_PRIMARY); itemTv.setPadding(0, 0, 0, dpToPx(24));
        root.addView(itemTv);

        TextInputLayout tilQty = new TextInputLayout(requireContext(), null, com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
        tilQty.setHint("Quantity");
        float r = (float) dpToPx(16);
        tilQty.setBoxCornerRadii(r, r, r, r);
        tilQty.setBoxStrokeColor(COLOR_PRIMARY);
        tilQty.setHintTextColor(android.content.res.ColorStateList.valueOf(COLOR_PRIMARY));
        
        TextInputEditText etQty = new TextInputEditText(tilQty.getContext());
        etQty.setText("10"); etQty.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        tilQty.addView(etQty);
        root.addView(tilQty);

        MaterialButton btnConfirm = new MaterialButton(requireContext());
        btnConfirm.setText("Place Order");
        btnConfirm.setBackgroundTintList(android.content.res.ColorStateList.valueOf(COLOR_PRIMARY));
        btnConfirm.setTextColor(Color.WHITE);
        btnConfirm.setCornerRadius(dpToPx(16));
        btnConfirm.setPadding(0, dpToPx(16), 0, dpToPx(16));
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bp.topMargin = dpToPx(16);
        btnConfirm.setLayoutParams(bp);
        
        btnConfirm.setOnClickListener(v -> {
            try {
                int quantity = Integer.parseInt(etQty.getText().toString());
                String today = new java.text.SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new java.util.Date());
                DataManager.getInstance().addOrder(new Order("My Account", woodName, quantity, unitPrice, today, "Marketplace"));
                dialog.dismiss();
                Toast.makeText(getContext(), "Order placed! 🎊", Toast.LENGTH_SHORT).show();
                switchTab(3); 
            } catch (Exception e) { Toast.makeText(getContext(), "Error placing order", Toast.LENGTH_SHORT).show(); }
        });

        root.addView(btnConfirm);
        dialog.setContentView(root);
        dialog.show();
    }

    private class HorizontalScrollView extends android.widget.HorizontalScrollView {
        public HorizontalScrollView(android.content.Context context) { super(context); }
    }
}
