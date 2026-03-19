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
        int imagePlaceholderColor; // Using colors to represent timber types in this demo
        Product(String cat, String n, String d, String p, String ib, String it, int imgColor) {
            this.category = cat; this.name = n; this.desc = d; this.price = p; this.iconBg = ib; this.iconText = it;
            this.imagePlaceholderColor = imgColor;
        }
    }

    private final Product[] allProducts = {
        new Product("Hardwood", "Premium Oak Wood", "Grade A timber for luxury furniture and flooring.", "$120.00", "#FDE68A", "#D97706", Color.parseColor("#A16207")),
        new Product("Softwood", "Treated Pine", "Pressure-treated pine, ideal for outdoor deck construction.", "$85.00", "#BFDBFE", "#2563EB", Color.parseColor("#1E40AF")),
        new Product("Hardwood", "Mahogany Beams", "Strong, durable beams with rich reddish-brown wood grain.", "$210.00", "#FBCFE8", "#DB2777", Color.parseColor("#9D174D")),
        new Product("Plywood",  "Marine Plywood", "High-grade water-resistant plywood for boat building.", "$65.00", "#D1FAE5", "#059669", Color.parseColor("#065F46")),
        new Product("Softwood", "Cedar Decking", "Naturally rot-resistant cedar planks for premium decks.", "$145.00", "#FFEDD5", "#C2410C", Color.parseColor("#9A3412")),
        new Product("Plywood",  "Standard Plywood", "Multi-purpose plywood for general interior construction.", "$45.00", "#F3F4F6", "#374151", Color.parseColor("#4B5563"))
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
        scrollContent.addView(createSectionTitle("Premium Timber Catalog"));
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
                productsContainer.addView(createProductCard(p.name, p.desc, p.price + " / unit", p.imagePlaceholderColor));
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
        chip.setElevation(dpToPx(isActive ? 4 : 2));
        
        chip.setClickable(true);
        chip.setOnClickListener(v -> {
            selectedCategory = title;
            updateCategoryRow();
            refreshProductsList();
        });
        
        return chip;
    }

    private View createProductCard(String title, String desc, String price, int timberColor) {
        LinearLayout outerLayout = new LinearLayout(getContext());
        outerLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams outerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        outerParams.setMargins(0, 0, 0, dpToPx(20));
        outerLayout.setLayoutParams(outerParams);

        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(COLOR_WHITE);
        cardBg.setCornerRadius(dpToPx(24));
        card.setBackground(cardBg);
        card.setElevation(dpToPx(8));
        card.setClipToOutline(true);

        // 1. Product Image Section
        android.widget.ImageView productImage = new android.widget.ImageView(getContext());
        productImage.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
        
        // Simulating image content with a textured gradient for now
        GradientDrawable imageTexture = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{timberColor, adjustAlpha(timberColor, 0.7f)}
        );
        productImage.setBackground(imageTexture);
        
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(160));
        productImage.setLayoutParams(imgParams);
        card.addView(productImage);

        // 2. Info Section
        LinearLayout infoSection = new LinearLayout(getContext());
        infoSection.setOrientation(LinearLayout.VERTICAL);
        infoSection.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));

        // Title + Badge Row
        LinearLayout titleRow = new LinearLayout(getContext());
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView titleText = new TextView(getContext());
        titleText.setText(title);
        titleText.setTextSize(18);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setTextColor(COLOR_TEXT_PRIMARY);
        titleText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView inStockBadge = new TextView(getContext());
        inStockBadge.setText("✓ Ready");
        inStockBadge.setTextSize(11);
        inStockBadge.setTypeface(null, Typeface.BOLD);
        inStockBadge.setTextColor(COLOR_PRIMARY);
        GradientDrawable stockBg = new GradientDrawable();
        stockBg.setColor(Color.parseColor("#ECFDF5"));
        stockBg.setCornerRadius(dpToPx(100));
        inStockBadge.setBackground(stockBg);
        inStockBadge.setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4));

        titleRow.addView(titleText);
        titleRow.addView(inStockBadge);
        infoSection.addView(titleRow);

        // Description
        TextView descText = new TextView(getContext());
        descText.setText(desc);
        descText.setTextSize(14);
        descText.setTextColor(COLOR_TEXT_SECONDARY);
        descText.setPadding(0, dpToPx(8), 0, dpToPx(16));
        infoSection.addView(descText);

        // Bottom Row: Price + Order Button
        LinearLayout bottomRow = new LinearLayout(getContext());
        bottomRow.setOrientation(LinearLayout.HORIZONTAL);
        bottomRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView priceText = new TextView(getContext());
        priceText.setText(price);
        priceText.setTextSize(20);
        priceText.setTypeface(null, Typeface.BOLD);
        priceText.setTextColor(COLOR_PRIMARY);
        priceText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        MaterialButton orderBtn = new MaterialButton(getContext());
        orderBtn.setText("Order Now");
        orderBtn.setAllCaps(false);
        orderBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(COLOR_PRIMARY));
        orderBtn.setTextColor(Color.WHITE);
        orderBtn.setCornerRadius(dpToPx(12));
        orderBtn.setPadding(dpToPx(16), dpToPx(10), dpToPx(16), dpToPx(10));
        orderBtn.setOnClickListener(v -> {
            try {
                double priceVal = Double.parseDouble(price.replaceAll("[^0-9.]", ""));
                showOrderConfirmationSheet(title, priceVal);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        bottomRow.addView(priceText);
        bottomRow.addView(orderBtn);
        infoSection.addView(bottomRow);

        card.addView(infoSection);
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
        tilQty.setHint("Quantity to Order");
        float r = (float) dpToPx(16);
        tilQty.setBoxCornerRadii(r, r, r, r);
        tilQty.setBoxStrokeColor(COLOR_PRIMARY);
        tilQty.setHintTextColor(android.content.res.ColorStateList.valueOf(COLOR_PRIMARY));
        
        TextInputEditText etQty = new TextInputEditText(tilQty.getContext());
        etQty.setText("10"); etQty.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        tilQty.addView(etQty);
        root.addView(tilQty);

        MaterialButton btnConfirm = new MaterialButton(requireContext());
        btnConfirm.setText("Place My Order");
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
