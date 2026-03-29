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
import com.timbertrade.app.dashboard.adapters.PendingVerificationsAdapter;
import com.timbertrade.app.dashboard.adapters.RecentActivitiesAdapter;

public class AdminDashboardFragment extends Fragment {
    
    private TextView tvWelcome, tvTotalUsers, tvTotalProducts, tvActiveAuctions, tvPendingCount;
    private RecyclerView rvPendingVerifications, rvRecentActivities;
    private CardView btnManageUsers, btnVerifyListings, btnManageAuctions, btnViewReports;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
        
        initViews(view);
        setupRecyclerViews();
        setupClickListeners();
        loadDashboardData();
        
        return view;
    }
    
    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
        tvTotalProducts = view.findViewById(R.id.tvTotalProducts);
        tvActiveAuctions = view.findViewById(R.id.tvActiveAuctions);
        tvPendingCount = view.findViewById(R.id.tvPendingCount);
        rvPendingVerifications = view.findViewById(R.id.rvPendingVerifications);
        rvRecentActivities = view.findViewById(R.id.rvRecentActivities);
        btnManageUsers = view.findViewById(R.id.btnManageUsers);
        btnVerifyListings = view.findViewById(R.id.btnVerifyListings);
        btnManageAuctions = view.findViewById(R.id.btnManageAuctions);
        btnViewReports = view.findViewById(R.id.btnViewReports);
    }
    
    private void setupRecyclerViews() {
        // Setup Pending Verifications RecyclerView
        rvPendingVerifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPendingVerifications.setAdapter(new PendingVerificationsAdapter());
        
        // Setup Recent Activities RecyclerView
        rvRecentActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecentActivities.setAdapter(new RecentActivitiesAdapter());
    }
    
    private void setupClickListeners() {
        btnManageUsers.setOnClickListener(v -> {
            // TODO: Navigate to manage users
        });
        
        btnVerifyListings.setOnClickListener(v -> {
            // TODO: Navigate to verify listings
        });
        
        btnManageAuctions.setOnClickListener(v -> {
            // TODO: Navigate to manage auctions
        });
        
        btnViewReports.setOnClickListener(v -> {
            // TODO: Navigate to reports
        });
    }
    
    private void loadDashboardData() {
        // TODO: Load actual data from backend
        // For now, showing demo data
        tvTotalUsers.setText("1,234");
        tvTotalProducts.setText("567");
        tvActiveAuctions.setText("89");
        tvPendingCount.setText("12");
    }
}
