package com.timbertrade.app.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.timbertrade.app.R;
import com.timbertrade.app.dashboard.adapters.MyProductsAdapter;
import com.timbertrade.app.dashboard.adapters.RecentOrdersAdapter;

public class SellerDashboardFragment extends Fragment {
    
    private TextView tvWelcome, tvTotalListings, tvTotalSales, tvRevenue;
    private TextView tvViewAllProducts, tvViewAllOrders;
    private RecyclerView rvMyProducts, rvRecentOrders;
    private CardView btnAddProduct, btnCreateAuction;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_dashboard, container, false);
        
        initViews(view);
        setupRecyclerViews();
        setupClickListeners();
        loadDashboardData();
        
        return view;
    }
    
    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvTotalListings = view.findViewById(R.id.tvTotalListings);
        tvTotalSales = view.findViewById(R.id.tvTotalSales);
        tvRevenue = view.findViewById(R.id.tvRevenue);
        tvViewAllProducts = view.findViewById(R.id.tvViewAllProducts);
        tvViewAllOrders = view.findViewById(R.id.tvViewAllOrders);
        rvMyProducts = view.findViewById(R.id.rvMyProducts);
        rvRecentOrders = view.findViewById(R.id.rvRecentOrders);
        btnAddProduct = view.findViewById(R.id.btnAddProduct);
        btnCreateAuction = view.findViewById(R.id.btnCreateAuction);
    }
    
    private void setupRecyclerViews() {
        // Setup My Products RecyclerView
        rvMyProducts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvMyProducts.setAdapter(new MyProductsAdapter());
        
        // Setup Recent Orders RecyclerView
        rvRecentOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecentOrders.setAdapter(new RecentOrdersAdapter());
    }
    
    private void setupClickListeners() {
        tvViewAllProducts.setOnClickListener(v -> {
            // TODO: Navigate to my products
        });
        
        tvViewAllOrders.setOnClickListener(v -> {
            // TODO: Navigate to orders
        });
        
        btnAddProduct.setOnClickListener(v -> {
            // TODO: Navigate to add product
        });
        
        btnCreateAuction.setOnClickListener(v -> {
            // TODO: Navigate to create auction
        });
    }
    
    private void loadDashboardData() {
        // TODO: Load actual data from backend
        // For now, showing demo data
        tvTotalListings.setText("45");
        tvTotalSales.setText("12");
        tvRevenue.setText("2.5M");
    }
}
