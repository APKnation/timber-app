package com.timbertrade.app.marketplace;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.timbertrade.app.R;
import com.timbertrade.app.marketplace.adapters.ProductAdapter;
import com.timbertrade.app.models.Product;

import java.util.ArrayList;
import java.util.List;

public class MarketplaceFragment extends Fragment {
    
    private TextInputLayout tilSearch;
    private TextInputEditText etSearch;
    private AutoCompleteTextView spinnerCategory, spinnerSort;
    private MaterialButton btnPriceFilter, btnAdvancedFilter;
    private RecyclerView rvProducts;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState;
    
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> filteredProductList;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marketplace, container, false);
        
        initViews(view);
        setupSpinners();
        setupRecyclerView();
        setupClickListeners();
        setupSearchListener();
        loadProducts();
        
        return view;
    }
    
    private void initViews(View view) {
        tilSearch = view.findViewById(R.id.tilSearch);
        etSearch = view.findViewById(R.id.etSearch);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerSort = view.findViewById(R.id.spinnerSort);
        btnPriceFilter = view.findViewById(R.id.btnPriceFilter);
        btnAdvancedFilter = view.findViewById(R.id.btnAdvancedFilter);
        rvProducts = view.findViewById(R.id.rvProducts);
        progressBar = view.findViewById(R.id.progressBar);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        
        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();
    }
    
    private void setupSpinners() {
        // Category spinner
        String[] categories = {"All Categories", "Mahogany", "Teak", "Pine", "Oak", "Cedar"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_dropdown_item_1line, categories);
        spinnerCategory.setText("All Categories");
        spinnerCategory.setAdapter(categoryAdapter);
        
        // Sort spinner
        String[] sortOptions = {"Newest First", "Price: Low to High", "Price: High to Low", "Rating", "Most Popular"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_dropdown_item_1line, sortOptions);
        spinnerSort.setText("Newest First");
        spinnerSort.setAdapter(sortAdapter);
        
        // Setup listeners
        spinnerCategory.setOnItemClickListener((parent, view, position, id) -> {
            filterProducts();
        });
        
        spinnerSort.setOnItemClickListener((parent, view, position, id) -> {
            sortProducts(position);
        });
    }
    
    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(filteredProductList, product -> {
            // TODO: Navigate to product details
        });
        
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvProducts.setAdapter(productAdapter);
    }
    
    private void setupClickListeners() {
        btnPriceFilter.setOnClickListener(v -> {
            // TODO: Show price filter dialog
        });
        
        btnAdvancedFilter.setOnClickListener(v -> {
            // TODO: Show advanced filter dialog
        });
    }
    
    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    
    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);
        
        // TODO: Load actual products from backend
        // For now, load demo data
        loadDemoProducts();
        
        progressBar.setVisibility(View.GONE);
        filterProducts();
    }
    
    private void loadDemoProducts() {
        productList.clear();
        productList.addAll(com.timbertrade.app.utils.DemoDataGenerator.generateDemoProducts());
        filteredProductList.clear();
        filteredProductList.addAll(productList);
        productAdapter.notifyDataSetChanged();
    }
    
    private void filterProducts() {
        String searchTerm = etSearch.getText().toString().toLowerCase().trim();
        String selectedCategory = spinnerCategory.getText().toString();
        
        filteredProductList.clear();
        
        for (Product product : productList) {
            boolean matchesSearch = product.getTitle().toLowerCase().contains(searchTerm) ||
                    product.getDescription().toLowerCase().contains(searchTerm);
            
            boolean matchesCategory = selectedCategory.equals("All Categories") ||
                    product.getCategory().getDisplayName().equals(selectedCategory);
            
            if (matchesSearch && matchesCategory) {
                filteredProductList.add(product);
            }
        }
        
        productAdapter.notifyDataSetChanged();
        updateEmptyState();
    }
    
    private void sortProducts(int sortOption) {
        // TODO: Implement sorting logic
        switch (sortOption) {
            case 0: // Newest First
                // Sort by date
                break;
            case 1: // Price: Low to High
                filteredProductList.sort((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
                break;
            case 2: // Price: High to Low
                filteredProductList.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                break;
            case 3: // Rating
                filteredProductList.sort((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()));
                break;
            case 4: // Most Popular
                // Sort by review count or popularity
                break;
        }
        productAdapter.notifyDataSetChanged();
    }
    
    private void updateEmptyState() {
        if (filteredProductList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvProducts.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);
        }
    }
}
