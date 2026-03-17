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
import com.timbertrade.app.dashboard.adapters.FeaturedProductsAdapter;
import com.timbertrade.app.dashboard.adapters.LiveAuctionsAdapter;

public class BuyerDashboardFragment extends Fragment {
    
    private TextView tvWelcome, tvActiveListings, tvActiveAuctions;
    private TextView tvViewAllProducts, tvViewAllAuctions;
    private RecyclerView rvFeaturedProducts, rvLiveAuctions;
    private CardView btnSearchProducts, btnViewOrders;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buyer_dashboard, container, false);
        
        initViews(view);
        setupRecyclerViews();
        setupClickListeners();
        loadDashboardData();
        
        return view;
    }
    
    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvActiveListings = view.findViewById(R.id.tvActiveListings);
        tvActiveAuctions = view.findViewById(R.id.tvActiveAuctions);
        tvViewAllProducts = view.findViewById(R.id.tvViewAllProducts);
        tvViewAllAuctions = view.findViewById(R.id.tvViewAllAuctions);
        rvFeaturedProducts = view.findViewById(R.id.rvFeaturedProducts);
        rvLiveAuctions = view.findViewById(R.id.rvLiveAuctions);
        btnSearchProducts = view.findViewById(R.id.btnSearchProducts);
        btnViewOrders = view.findViewById(R.id.btnViewOrders);
    }
    
    private void setupRecyclerViews() {
        // Setup Featured Products RecyclerView
        rvFeaturedProducts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFeaturedProducts.setAdapter(new FeaturedProductsAdapter());
        
        // Setup Live Auctions RecyclerView
        rvLiveAuctions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLiveAuctions.setAdapter(new LiveAuctionsAdapter());
    }
    
    private void setupClickListeners() {
        tvViewAllProducts.setOnClickListener(v -> {
            // TODO: Navigate to marketplace
        });
        
        tvViewAllAuctions.setOnClickListener(v -> {
            // TODO: Navigate to auctions
        });
        
        btnSearchProducts.setOnClickListener(v -> {
            // TODO: Navigate to search
        });
        
        btnViewOrders.setOnClickListener(v -> {
            // TODO: Navigate to orders
        });
    }
    
    private void loadDashboardData() {
        // TODO: Load actual data from backend
        // For now, showing demo data
        tvActiveListings.setText("150");
        tvActiveAuctions.setText("25");
    }
}
