package com.timbertrade.app.analytics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.button.MaterialButton;
import com.timbertrade.app.R;
import com.timbertrade.app.analytics.adapters.TopProductAdapter;
import com.timbertrade.app.models.Product;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsFragment extends Fragment {
    
    private MaterialButton btnPeriodDay, btnPeriodWeek, btnPeriodMonth;
    private TextView tvTotalSales, tvTotalRevenue;
    private LineChart chartRevenue;
    private PieChart chartCategories;
    private RecyclerView rvTopProducts;
    private TopProductAdapter topProductAdapter;
    private List<Product> topProductList;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);
        
        initViews(view);
        setupCharts();
        setupRecyclerView();
        setupClickListeners();
        loadAnalyticsData();
        
        return view;
    }
    
    private void initViews(View view) {
        btnPeriodDay = view.findViewById(R.id.btnPeriodDay);
        btnPeriodWeek = view.findViewById(R.id.btnPeriodWeek);
        btnPeriodMonth = view.findViewById(R.id.btnPeriodMonth);
        tvTotalSales = view.findViewById(R.id.tvTotalSales);
        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue);
        chartRevenue = view.findViewById(R.id.chartRevenue);
        chartCategories = view.findViewById(R.id.chartCategories);
        rvTopProducts = view.findViewById(R.id.rvTopProducts);
        
        topProductList = new ArrayList<>();
    }
    
    private void setupCharts() {
        // TODO: Setup revenue line chart
        setupRevenueChart();
        
        // TODO: Setup category pie chart
        setupCategoryChart();
    }
    
    private void setupRevenueChart() {
        // TODO: Configure line chart for revenue trends
        // This would show revenue over time (day/week/month)
    }
    
    private void setupCategoryChart() {
        // TODO: Configure pie chart for category distribution
        // This would show sales by timber category
    }
    
    private void setupRecyclerView() {
        topProductAdapter = new TopProductAdapter(topProductList);
        rvTopProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTopProducts.setAdapter(topProductAdapter);
    }
    
    private void setupClickListeners() {
        btnPeriodDay.setOnClickListener(v -> {
            selectPeriod(0);
            loadAnalyticsData();
        });
        
        btnPeriodWeek.setOnClickListener(v -> {
            selectPeriod(1);
            loadAnalyticsData();
        });
        
        btnPeriodMonth.setOnClickListener(v -> {
            selectPeriod(2);
            loadAnalyticsData();
        });
    }
    
    private void selectPeriod(int period) {
        // Reset all buttons
        btnPeriodDay.setBackgroundColor(getResources().getColor(R.color.timber_surface));
        btnPeriodWeek.setBackgroundColor(getResources().getColor(R.color.timber_surface));
        btnPeriodMonth.setBackgroundColor(getResources().getColor(R.color.timber_surface));
        
        // Highlight selected button
        MaterialButton selectedButton;
        switch (period) {
            case 0:
                selectedButton = btnPeriodDay;
                break;
            case 1:
                selectedButton = btnPeriodWeek;
                break;
            case 2:
            default:
                selectedButton = btnPeriodMonth;
                break;
        }
        
        selectedButton.setBackgroundColor(getResources().getColor(R.color.timber_primary));
        selectedButton.setTextColor(getResources().getColor(R.color.white));
    }
    
    private void loadAnalyticsData() {
        // TODO: Load actual analytics data from backend
        // For now, load demo data
        loadDemoAnalytics();
    }
    
    private void loadDemoAnalytics() {
        // Load demo analytics data
        List<com.timbertrade.app.models.Product> allProducts = com.timbertrade.app.utils.DemoDataGenerator.generateDemoProducts();
        
        // Set demo values
        tvTotalSales.setText("45");
        tvTotalRevenue.setText("12.5M");
        
        // Load demo top products (top 5)
        topProductList.clear();
        for (int i = 0; i < Math.min(5, allProducts.size()); i++) {
            topProductList.add(allProducts.get(i));
        }
        topProductAdapter.notifyDataSetChanged();
        
        // Update charts with demo data
        updateChartsWithDemoData();
    }
    
    private void updateChartsWithDemoData() {
        // TODO: Update charts with demo data
        // This would populate the revenue and category charts
    }
}
