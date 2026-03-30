package com.timbertrade.app.marketplace;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.timbertrade.app.R;
import com.timbertrade.app.models.Product;
import com.timbertrade.app.marketplace.adapters.ProductAdapter;
import com.timbertrade.app.services.FirebaseAuthService;
import com.timbertrade.app.services.ProductService;
import com.timbertrade.app.services.SearchService;

import java.util.ArrayList;
import java.util.List;

public class MarketplaceFragment extends Fragment {
    
    private static final String TAG = "MarketplaceFragment";
    
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoProducts;
    private FloatingActionButton fabAddProduct;
    private LinearLayout categoryRow;
    
    private ProductAdapter productAdapter;
    private ProductService productService;
    private SearchService searchService;
    private FirebaseAuthService authService;
    
    private List<Product> productList;
    private String selectedCategory = "All";
    private String currentUserId;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        productService = ProductService.getInstance();
        searchService = SearchService.getInstance();
        authService = FirebaseAuthService.getInstance();
        
        productList = new ArrayList<>();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marketplace, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        loadProducts();
        
        return view;
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tvNoProducts = view.findViewById(R.id.tvNoProducts);
        fabAddProduct = view.findViewById(R.id.fabAddProduct);
        categoryRow = view.findViewById(R.id.categoryRow);
        
        // Setup categories
        setupCategories();
    }
    
    private void setupCategories() {
        String[] categories = {"All", "Hardwood", "Softwood", "Exotic", "Treated", "Rough Sawn", "Planed"};
        
        for (String category : categories) {
            TextView categoryView = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 8, 8, 8);
            categoryView.setLayoutParams(params);
            categoryView.setText(category);
            categoryView.setPadding(16, 8, 16, 8);
            categoryView.setBackgroundResource(R.drawable.bg_category_unselected);
            categoryView.setTextColor(getResources().getColor(R.color.timber_text_secondary));
            
            categoryView.setOnClickListener(v -> {
                selectedCategory = category;
                updateCategoryUI();
                filterProductsByCategory();
            });
            
            categoryRow.addView(categoryView);
        }
        
        // Select "All" by default
        selectedCategory = "All";
        updateCategoryUI();
    }
    
    private void updateCategoryUI() {
        for (int i = 0; i < categoryRow.getChildCount(); i++) {
            TextView categoryView = (TextView) categoryRow.getChildAt(i);
            String category = categoryView.getText().toString();
            
            if (category.equals(selectedCategory)) {
                categoryView.setBackgroundResource(R.drawable.bg_category_selected);
                categoryView.setTextColor(getResources().getColor(R.color.timber_primary));
            } else {
                categoryView.setBackgroundResource(R.drawable.bg_category_unselected);
                categoryView.setTextColor(getResources().getColor(R.color.timber_text_secondary));
            }
        }
    }
    
    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(productList, getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(productAdapter);
        
        // Set item click listener
        productAdapter.setOnItemClickListener(position -> {
            Product product = productList.get(position);
            // Navigate to product details
            // TODO: Implement product details navigation
            Toast.makeText(getContext(), "Product: " + product.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }
    
    private void setupClickListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadProducts();
        });
        
        fabAddProduct.setOnClickListener(v -> {
            // Check if user is logged in and is a seller
            if (authService.isUserLoggedIn()) {
                authService.getCurrentUser(new FirebaseAuthService.AuthCallback() {
                    @Override
                    public void onSuccess(com.timbertrade.app.models.User user) {
                        if (user.getRole().equals("SELLER") || user.getRole().equals("ADMIN")) {
                            showAddProductDialog();
                        } else {
                            Toast.makeText(getContext(), "Only sellers can add products", Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Please login to add products", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadProducts() {
        showLoading(true);
        
        productService.getAllProducts(new ProductService.ProductCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);
                
                productList.clear();
                productList.addAll(products);
                
                filterProductsByCategory();
                updateEmptyState();
            }
            
            @Override
            public void onError(String error) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }
    
    private void filterProductsByCategory() {
        List<Product> filteredList = new ArrayList<>();
        
        if (selectedCategory.equals("All")) {
            filteredList.addAll(productList);
        } else {
            for (Product product : productList) {
                if (product.getCategory() != null && 
                    product.getCategory().toString().equalsIgnoreCase(selectedCategory)) {
                    filteredList.add(product);
                }
            }
        }
        
        productAdapter.updateList(filteredList);
        updateEmptyState();
    }
    
    private void showAddProductDialog() {
        AddProductDialog dialog = AddProductDialog.newInstance();
        dialog.setProductSavedListener(product -> {
            // Refresh the product list
            loadProducts();
        });
        dialog.show(getChildFragmentManager(), "add_product_dialog");
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void updateEmptyState() {
        if (productAdapter.getItemCount() == 0) {
            tvNoProducts.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoProducts.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment resumes
        loadProducts();
    }
}
        
        toolbar.addView(backBtn);
        toolbar.addView(titleText);
        toolbar.addView(addItem);
        
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
        scrollContent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        
        scrollContent.addView(createPromotionBanner());
        
        // Search Bar
        scrollContent.addView(createSearchBar());

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
        scrollContent.addView(createSectionTitle("Available Inventory"));
        productsContainer = new LinearLayout(getContext());
        productsContainer.setOrientation(LinearLayout.VERTICAL);
        productsContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        refreshProductsList();
        scrollContent.addView(productsContainer);
        
        scrollView.addView(scrollContent);
        root.addView(scrollView, scrollParams);
        
        return root;
    }

    private View createSearchBar() {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_WHITE);
        bg.setCornerRadius(dpToPx(16));
        row.setBackground(bg);
        row.setElevation(dpToPx(4));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dpToPx(24));
        row.setLayoutParams(lp);

        TextView icon = new TextView(getContext());
        icon.setText("🔍");
        icon.setPadding(0, 0, dpToPx(12), 0);
        
        TextView hint = new TextView(getContext());
        hint.setText("Search Timber, Grade, or Category...");
        hint.setTextColor(COLOR_TEXT_SECONDARY);
        hint.setTextSize(14);
        
        row.addView(icon);
        row.addView(hint);
        return row;
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
        card.setPadding(dpToPx(28), dpToPx(24), dpToPx(28), dpToPx(24));
        
        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.BL_TR,
                new int[]{Color.parseColor("#064E3B"), Color.parseColor("#10B981")}
        );
        bg.setCornerRadius(dpToPx(24));
        card.setBackground(bg);
        card.setElevation(dpToPx(8));
        
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, dpToPx(24));
        card.setLayoutParams(cardParams);
        
        TextView titleText = new TextView(getContext());
        titleText.setText("20% Wholesale Discount");
        titleText.setTextSize(22);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setTextColor(Color.WHITE);
        titleText.setPadding(0, 0, 0, dpToPx(4));
        
        TextView descText = new TextView(getContext());
        descText.setText("Applicable to all bulk orders this week.");
        descText.setTextSize(13);
        descText.setTextColor(Color.parseColor("#A7F3D0"));
        
        card.addView(titleText);
        card.addView(descText);
        return card;
    }

    private View createSectionTitle(String title) {
        TextView textView = new TextView(getContext());
        textView.setText(title);
        textView.setTextSize(16);
        textView.setLetterSpacing(0.02f);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(COLOR_TEXT_PRIMARY);
        textView.setPadding(0, dpToPx(8), 0, dpToPx(12));
        return textView;
    }
    
    private View createCategoryChip(String title, boolean isActive) {
        TextView chip = new TextView(getContext());
        chip.setText(title);
        chip.setTextColor(isActive ? COLOR_WHITE : COLOR_TEXT_PRIMARY);
        chip.setTextSize(13);
        chip.setTypeface(null, Typeface.BOLD);
        chip.setGravity(Gravity.CENTER);
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(isActive ? COLOR_PRIMARY : COLOR_WHITE);
        bg.setCornerRadius(dpToPx(100));
        chip.setBackground(bg);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, dpToPx(10), 0);
        chip.setPadding(dpToPx(18), dpToPx(10), dpToPx(18), dpToPx(10));
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
        outerParams.setMargins(0, 0, 0, dpToPx(24));
        outerLayout.setLayoutParams(outerParams);

        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(COLOR_WHITE);
        cardBg.setCornerRadius(dpToPx(24));
        card.setBackground(cardBg);
        card.setElevation(dpToPx(10));
        card.setClipToOutline(true);

        // 1. Product Image Section
        FrameLayout imageFrame = new FrameLayout(getContext());
        
        android.widget.ImageView productImage = new android.widget.ImageView(getContext());
        productImage.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
        GradientDrawable imageTexture = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{timberColor, adjustAlpha(timberColor, 0.4f)}
        );
        productImage.setBackground(imageTexture);
        imageFrame.addView(productImage, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(180)));
        
        // Overlay label on image to prevent it from looking empty
        TextView surfaceLabel = new TextView(getContext());
        surfaceLabel.setText(title.toUpperCase());
        surfaceLabel.setTextColor(Color.WHITE);
        surfaceLabel.setTextSize(10);
        surfaceLabel.setLetterSpacing(0.2f);
        surfaceLabel.setTypeface(null, Typeface.BOLD);
        surfaceLabel.setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));
        GradientDrawable labelBg = new GradientDrawable();
        labelBg.setColor(Color.argb(100, 0, 0, 0));
        labelBg.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, dpToPx(16), dpToPx(16)});
        surfaceLabel.setBackground(labelBg);
        
        FrameLayout.LayoutParams labelLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelLp.gravity = Gravity.BOTTOM | Gravity.LEFT;
        imageFrame.addView(surfaceLabel, labelLp);
        
        card.addView(imageFrame);

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
        titleText.setTextSize(20);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setTextColor(COLOR_TEXT_PRIMARY);
        titleText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView inStockBadge = new TextView(getContext());
        inStockBadge.setText("✓ In Stock");
        inStockBadge.setTextSize(11);
        inStockBadge.setTypeface(null, Typeface.BOLD);
        inStockBadge.setTextColor(COLOR_PRIMARY);
        GradientDrawable stockBg = new GradientDrawable();
        stockBg.setColor(Color.parseColor("#ECFDF5"));
        stockBg.setCornerRadius(dpToPx(100));
        inStockBadge.setBackground(stockBg);
        inStockBadge.setPadding(dpToPx(12), dpToPx(4), dpToPx(12), dpToPx(4));

        titleRow.addView(titleText);
        titleRow.addView(inStockBadge);
        infoSection.addView(titleRow);

        // Description
        TextView descText = new TextView(getContext());
        descText.setText(desc);
        descText.setTextSize(14);
        descText.setLineSpacing(dpToPx(2), 1f);
        descText.setTextColor(COLOR_TEXT_SECONDARY);
        descText.setPadding(0, dpToPx(8), 0, dpToPx(20));
        infoSection.addView(descText);

        // Bottom Row: Price + Order Button
        LinearLayout bottomRow = new LinearLayout(getContext());
        bottomRow.setOrientation(LinearLayout.HORIZONTAL);
        bottomRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView priceText = new TextView(getContext());
        priceText.setText(price);
        priceText.setTextSize(22);
        priceText.setTypeface(null, Typeface.BOLD);
        priceText.setTextColor(Color.BLACK);
        priceText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        MaterialButton orderBtn = new MaterialButton(getContext());
        orderBtn.setText("View Details");
        orderBtn.setAllCaps(false);
        orderBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(COLOR_PRIMARY));
        orderBtn.setTextColor(Color.WHITE);
        orderBtn.setCornerRadius(dpToPx(12));
        orderBtn.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));
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

    private void showAddProductSheet() {
        AddProductDialog dialog = AddProductDialog.newInstance(null);
        dialog.setOnProductSavedListener(product -> {
            Toast.makeText(getContext(), "Product added successfully! 🚀", Toast.LENGTH_LONG).show();
            refreshProductsList();
        });
        dialog.show(getChildFragmentManager(), "AddProductDialog");
    }

    private TextInputLayout createOutlinedInput(String hint) {
        TextInputLayout til = new TextInputLayout(requireContext(), null, com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
        til.setHint(hint);
        float r = (float) dpToPx(16);
        til.setBoxCornerRadii(r, r, r, r);
        til.setBoxStrokeColor(COLOR_PRIMARY);
        til.setHintTextColor(android.content.res.ColorStateList.valueOf(COLOR_PRIMARY));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = dpToPx(16);
        til.setLayoutParams(p);
        TextInputEditText et = new TextInputEditText(til.getContext());
        et.setTextSize(15);
        til.addView(et);
        return til;
    }

    private class HorizontalScrollView extends android.widget.HorizontalScrollView {
        public HorizontalScrollView(android.content.Context context) { super(context); }
    }
}
