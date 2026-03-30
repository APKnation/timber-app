package com.timbertrade.app.marketplace;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MarketplaceFragment_new extends Fragment {
    
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
