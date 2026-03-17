package com.timbertrade.app.reports;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.timbertrade.app.dashboard.RealDashboardActivity;
import com.timbertrade.app.inventory.InventoryActivity;
import com.timbertrade.app.models.Report;
import com.timbertrade.app.orders.NewOrderActivity;
import com.timbertrade.app.utils.DataManager;

import java.util.List;

public class ReportsActivity extends Activity {
    
    private static final String TAG = "ReportsActivity";
    private DataManager dataManager;
    private TableLayout reportsTable;
    private LinearLayout formLayout;
    private boolean isFormVisible = false;
    private Report editingReport = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starting ReportsActivity");
        
        try {
            super.onCreate(savedInstanceState);
            dataManager = DataManager.getInstance();
            
            // Create main layout
            createMainLayout();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in ReportsActivity: " + e.getMessage(), e);
        }
    }
    
    private void createMainLayout() {
        // Main container
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        
        // Header
        createHeader(mainLayout);
        
        // Navigation bar
        createNavigationBar(mainLayout);
        
        // Content area with scroll
        ScrollView scrollView = new ScrollView(this);
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(20, 20, 20, 20);
        
        // Reports table
        createReportsTable(contentLayout);
        
        // Add/Edit form (initially hidden)
        createReportForm(contentLayout);
        
        scrollView.addView(contentLayout);
        mainLayout.addView(scrollView);
        
        setContentView(mainLayout);
    }
    
    private void createHeader(LinearLayout parent) {
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.VERTICAL);
        
        // Gradient background
        android.graphics.drawable.GradientDrawable gradientDrawable = new android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#FF9800"), Color.parseColor("#F57C00")}
        );
        gradientDrawable.setCornerRadius(0);
        headerLayout.setBackground(gradientDrawable);
        headerLayout.setPadding(30, 40, 30, 40);
        
        // Title
        TextView titleText = new TextView(this);
        titleText.setText("Reports & Analytics");
        titleText.setTextSize(24);
        titleText.setTextColor(Color.WHITE);
        titleText.setGravity(Gravity.CENTER);
        titleText.setShadowLayer(2, 1, 1, Color.parseColor("#000000"));
        headerLayout.addView(titleText);
        
        // Subtitle
        TextView subtitleText = new TextView(this);
        subtitleText.setText("Business Intelligence and Analytics");
        subtitleText.setTextSize(14);
        subtitleText.setTextColor(Color.parseColor("#FFF3E0"));
        subtitleText.setGravity(Gravity.CENTER);
        subtitleText.setPadding(0, 10, 0, 0);
        headerLayout.addView(subtitleText);
        
        parent.addView(headerLayout);
    }
    
    private void createNavigationBar(LinearLayout parent) {
        LinearLayout navLayout = new LinearLayout(this);
        navLayout.setOrientation(LinearLayout.HORIZONTAL);
        navLayout.setBackgroundColor(Color.WHITE);
        navLayout.setPadding(10, 15, 10, 15);
        navLayout.setElevation(4);
        
        // Dashboard button
        Button dashboardBtn = createNavButton("Dashboard", Color.parseColor("#666666"));
        dashboardBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, RealDashboardActivity.class));
        });
        navLayout.addView(dashboardBtn);
        
        // Orders button
        Button ordersBtn = createNavButton("Orders", Color.parseColor("#666666"));
        ordersBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, NewOrderActivity.class));
        });
        navLayout.addView(ordersBtn);
        
        // Inventory button
        Button inventoryBtn = createNavButton("Inventory", Color.parseColor("#666666"));
        inventoryBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, InventoryActivity.class));
        });
        navLayout.addView(inventoryBtn);
        
        // Reports button (active)
        Button reportsBtn = createNavButton("Reports", Color.parseColor("#FF9800"));
        reportsBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Already on Reports page", Toast.LENGTH_SHORT).show();
        });
        navLayout.addView(reportsBtn);
        
        parent.addView(navLayout);
    }
    
    private Button createNavButton(String text, int color) {
        Button button = new Button(this);
        button.setText(text);
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setTextColor(color);
        button.setTextSize(12);
        button.setPadding(0, 5, 0, 5);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        );
        button.setLayoutParams(params);
        
        return button;
    }
    
    private void createReportsTable(LinearLayout parent) {
        // Section title with add button
        LinearLayout titleLayout = new LinearLayout(this);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView titleText = new TextView(this);
        titleText.setText("Reports List");
        titleText.setTextSize(20);
        titleText.setTextColor(Color.parseColor("#333333"));
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        titleText.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        ));
        titleLayout.addView(titleText);
        
        Button addBtn = new Button(this);
        addBtn.setText("Generate New Report");
        addBtn.setBackgroundColor(Color.parseColor("#FF9800"));
        addBtn.setTextColor(Color.WHITE);
        addBtn.setPadding(20, 10, 20, 10);
        addBtn.setOnClickListener(v -> showReportForm());
        titleLayout.addView(addBtn);
        
        parent.addView(titleLayout);
        
        // Table
        reportsTable = new TableLayout(this);
        reportsTable.setBackgroundColor(Color.WHITE);
        reportsTable.setPadding(10, 10, 10, 10);
        
        // Table header
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(Color.parseColor("#F5F5F5"));
        
        String[] headers = {"Title", "Type", "Period", "Revenue", "Orders", "Actions"};
        for (String header : headers) {
            TextView headerText = new TextView(this);
            headerText.setText(header);
            headerText.setTextSize(12);
            headerText.setTextColor(Color.parseColor("#333333"));
            headerText.setTypeface(null, android.graphics.Typeface.BOLD);
            headerText.setPadding(8, 10, 8, 10);
            headerText.setGravity(Gravity.CENTER);
            headerRow.addView(headerText);
        }
        
        reportsTable.addView(headerRow);
        
        // Load reports
        loadReports();
        
        parent.addView(reportsTable);
    }
    
    private void loadReports() {
        // Clear existing rows (except header)
        if (reportsTable.getChildCount() > 1) {
            reportsTable.removeViews(1, reportsTable.getChildCount() - 1);
        }
        
        List<Report> reports = dataManager.getAllReports();
        for (Report report : reports) {
            TableRow row = createReportRow(report);
            reportsTable.addView(row);
        }
    }
    
    private TableRow createReportRow(Report report) {
        TableRow row = new TableRow(this);
        row.setBackgroundColor(Color.WHITE);
        
        // Title
        TextView titleText = new TextView(this);
        titleText.setText(report.getTitle());
        titleText.setTextSize(12);
        titleText.setPadding(8, 10, 8, 10);
        titleText.setGravity(Gravity.CENTER);
        row.addView(titleText);
        
        // Type
        TextView typeText = new TextView(this);
        typeText.setText(report.getReportType());
        typeText.setTextSize(12);
        typeText.setPadding(8, 10, 8, 10);
        typeText.setGravity(Gravity.CENTER);
        
        // Color code report type
        switch (report.getReportType()) {
            case "Sales":
                typeText.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "Financial":
                typeText.setTextColor(Color.parseColor("#2196F3"));
                break;
            case "Inventory":
                typeText.setTextColor(Color.parseColor("#FF9800"));
                break;
            default:
                typeText.setTextColor(Color.parseColor("#666666"));
        }
        row.addView(typeText);
        
        // Period
        TextView periodText = new TextView(this);
        periodText.setText(report.getPeriod());
        periodText.setTextSize(12);
        periodText.setPadding(8, 10, 8, 10);
        periodText.setGravity(Gravity.CENTER);
        row.addView(periodText);
        
        // Revenue
        TextView revenueText = new TextView(this);
        revenueText.setText("$" + String.format("%.2f", report.getTotalRevenue()));
        revenueText.setTextSize(12);
        revenueText.setPadding(8, 10, 8, 10);
        revenueText.setGravity(Gravity.CENTER);
        revenueText.setTextColor(Color.parseColor("#4CAF50"));
        row.addView(revenueText);
        
        // Orders
        TextView ordersText = new TextView(this);
        ordersText.setText(String.valueOf(report.getTotalOrders()));
        ordersText.setTextSize(12);
        ordersText.setPadding(8, 10, 8, 10);
        ordersText.setGravity(Gravity.CENTER);
        row.addView(ordersText);
        
        // Actions
        LinearLayout actionsLayout = new LinearLayout(this);
        actionsLayout.setOrientation(LinearLayout.HORIZONTAL);
        actionsLayout.setGravity(Gravity.CENTER);
        
        Button viewBtn = new Button(this);
        viewBtn.setText("View");
        viewBtn.setBackgroundColor(Color.parseColor("#2196F3"));
        viewBtn.setTextColor(Color.WHITE);
        viewBtn.setTextSize(10);
        viewBtn.setPadding(8, 5, 8, 5);
        viewBtn.setOnClickListener(v -> viewReport(report));
        actionsLayout.addView(viewBtn);
        
        Button editBtn = new Button(this);
        editBtn.setText("Edit");
        editBtn.setBackgroundColor(Color.parseColor("#FF9800"));
        editBtn.setTextColor(Color.WHITE);
        editBtn.setTextSize(10);
        editBtn.setPadding(8, 5, 8, 5);
        editBtn.setOnClickListener(v -> editReport(report));
        actionsLayout.addView(editBtn);
        
        Button deleteBtn = new Button(this);
        deleteBtn.setText("Delete");
        deleteBtn.setBackgroundColor(Color.parseColor("#F44336"));
        deleteBtn.setTextColor(Color.WHITE);
        deleteBtn.setTextSize(10);
        deleteBtn.setPadding(8, 5, 8, 5);
        deleteBtn.setOnClickListener(v -> deleteReport(report));
        actionsLayout.addView(deleteBtn);
        
        row.addView(actionsLayout);
        
        return row;
    }
    
    private void createReportForm(LinearLayout parent) {
        formLayout = new LinearLayout(this);
        formLayout.setOrientation(LinearLayout.VERTICAL);
        formLayout.setBackgroundColor(Color.WHITE);
        formLayout.setPadding(20, 20, 20, 20);
        formLayout.setElevation(4);
        
        LinearLayout.LayoutParams formParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        formParams.setMargins(0, 20, 0, 0);
        formLayout.setLayoutParams(formParams);
        
        // Form title
        TextView formTitle = new TextView(this);
        formTitle.setText("Report Details");
        formTitle.setTextSize(18);
        formTitle.setTextColor(Color.parseColor("#333333"));
        formTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        formTitle.setPadding(0, 0, 0, 20);
        formLayout.addView(formTitle);
        
        // Title
        addFormField(formLayout, "Report Title:", "title");
        
        // Report Type
        addFormField(formLayout, "Report Type:", "reportType");
        
        // Period
        addFormField(formLayout, "Period:", "period");
        
        // Total Revenue
        addFormField(formLayout, "Total Revenue:", "totalRevenue");
        
        // Total Orders
        addFormField(formLayout, "Total Orders:", "totalOrders");
        
        // Top Product
        addFormField(formLayout, "Top Product:", "topProduct");
        
        // Summary
        addFormField(formLayout, "Summary:", "summary");
        
        // Details
        addFormField(formLayout, "Details:", "details");
        
        // Buttons
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 20, 0, 0);
        
        Button saveBtn = new Button(this);
        saveBtn.setText("Save Report");
        saveBtn.setBackgroundColor(Color.parseColor("#FF9800"));
        saveBtn.setTextColor(Color.WHITE);
        saveBtn.setPadding(30, 15, 30, 15);
        saveBtn.setOnClickListener(v -> saveReport());
        buttonLayout.addView(saveBtn);
        
        Button cancelBtn = new Button(this);
        cancelBtn.setText("Cancel");
        cancelBtn.setBackgroundColor(Color.parseColor("#666666"));
        cancelBtn.setTextColor(Color.WHITE);
        cancelBtn.setPadding(30, 15, 30, 15);
        cancelBtn.setOnClickListener(v -> hideReportForm());
        buttonLayout.addView(cancelBtn);
        
        formLayout.addView(buttonLayout);
        
        parent.addView(formLayout);
        
        // Initially hidden
        formLayout.setVisibility(View.GONE);
    }
    
    private void addFormField(LinearLayout parent, String label, String hint) {
        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setTextSize(14);
        labelView.setTextColor(Color.parseColor("#333333"));
        labelView.setPadding(0, 10, 0, 5);
        parent.addView(labelView);
        
        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setTextSize(14);
        editText.setPadding(15, 10, 15, 10);
        editText.setBackgroundColor(Color.parseColor("#F5F5F5"));
        editText.setTag(hint);
        parent.addView(editText);
    }
    
    private void showReportForm() {
        editingReport = null;
        clearForm();
        formLayout.setVisibility(View.VISIBLE);
        isFormVisible = true;
    }
    
    private void hideReportForm() {
        formLayout.setVisibility(View.GONE);
        isFormVisible = false;
        editingReport = null;
    }
    
    private void viewReport(Report report) {
        // Create a simple dialog to show report details
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(30, 30, 30, 30);
        dialogLayout.setBackgroundColor(Color.WHITE);
        
        TextView titleText = new TextView(this);
        titleText.setText(report.getTitle());
        titleText.setTextSize(18);
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        titleText.setPadding(0, 0, 0, 20);
        dialogLayout.addView(titleText);
        
        TextView summaryText = new TextView(this);
        summaryText.setText(report.getSummary());
        summaryText.setTextSize(14);
        summaryText.setPadding(0, 0, 0, 20);
        dialogLayout.addView(summaryText);
        
        TextView detailsText = new TextView(this);
        detailsText.setText(report.getDetails());
        detailsText.setTextSize(12);
        dialogLayout.addView(detailsText);
        
        Button closeBtn = new Button(this);
        closeBtn.setText("Close");
        closeBtn.setBackgroundColor(Color.parseColor("#FF9800"));
        closeBtn.setTextColor(Color.WHITE);
        closeBtn.setPadding(20, 10, 20, 10);
        closeBtn.setOnClickListener(v -> {
            // Remove dialog (simplified approach)
            if (dialogLayout.getParent() != null) {
                ((LinearLayout) dialogLayout.getParent()).removeView(dialogLayout);
            }
        });
        dialogLayout.addView(closeBtn);
        
        // Add dialog to main layout (simplified approach)
        LinearLayout mainLayout = (LinearLayout) findViewById(android.R.id.content).getRootView();
        if (mainLayout != null) {
            mainLayout.addView(dialogLayout);
        }
    }
    
    private void editReport(Report report) {
        editingReport = report;
        
        // Populate form with report data
        EditText titleField = formLayout.findViewWithTag("title");
        EditText typeField = formLayout.findViewWithTag("reportType");
        EditText periodField = formLayout.findViewWithTag("period");
        EditText revenueField = formLayout.findViewWithTag("totalRevenue");
        EditText ordersField = formLayout.findViewWithTag("totalOrders");
        EditText topProductField = formLayout.findViewWithTag("topProduct");
        EditText summaryField = formLayout.findViewWithTag("summary");
        EditText detailsField = formLayout.findViewWithTag("details");
        
        titleField.setText(report.getTitle());
        typeField.setText(report.getReportType());
        periodField.setText(report.getPeriod());
        revenueField.setText(String.valueOf(report.getTotalRevenue()));
        ordersField.setText(String.valueOf(report.getTotalOrders()));
        topProductField.setText(report.getTopProduct());
        summaryField.setText(report.getSummary());
        detailsField.setText(report.getDetails());
        
        formLayout.setVisibility(View.VISIBLE);
        isFormVisible = true;
    }
    
    private void clearForm() {
        EditText titleField = formLayout.findViewWithTag("title");
        EditText typeField = formLayout.findViewWithTag("reportType");
        EditText periodField = formLayout.findViewWithTag("period");
        EditText revenueField = formLayout.findViewWithTag("totalRevenue");
        EditText ordersField = formLayout.findViewWithTag("totalOrders");
        EditText topProductField = formLayout.findViewWithTag("topProduct");
        EditText summaryField = formLayout.findViewWithTag("summary");
        EditText detailsField = formLayout.findViewWithTag("details");
        
        titleField.setText("");
        typeField.setText("");
        periodField.setText("");
        revenueField.setText("");
        ordersField.setText("");
        topProductField.setText("");
        summaryField.setText("");
        detailsField.setText("");
    }
    
    private void saveReport() {
        try {
            EditText titleField = formLayout.findViewWithTag("title");
            EditText typeField = formLayout.findViewWithTag("reportType");
            EditText periodField = formLayout.findViewWithTag("period");
            EditText revenueField = formLayout.findViewWithTag("totalRevenue");
            EditText ordersField = formLayout.findViewWithTag("totalOrders");
            EditText topProductField = formLayout.findViewWithTag("topProduct");
            EditText summaryField = formLayout.findViewWithTag("summary");
            EditText detailsField = formLayout.findViewWithTag("details");
            
            String title = titleField.getText().toString().trim();
            String type = typeField.getText().toString().trim();
            String period = periodField.getText().toString().trim();
            String revenueStr = revenueField.getText().toString().trim();
            String ordersStr = ordersField.getText().toString().trim();
            String topProduct = topProductField.getText().toString().trim();
            String summary = summaryField.getText().toString().trim();
            String details = detailsField.getText().toString().trim();
            
            if (title.isEmpty() || type.isEmpty() || period.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            double revenue = revenueStr.isEmpty() ? 0.0 : Double.parseDouble(revenueStr);
            int orders = ordersStr.isEmpty() ? 0 : Integer.parseInt(ordersStr);
            
            if (editingReport != null) {
                // Update existing report
                editingReport.setTitle(title);
                editingReport.setReportType(type);
                editingReport.setPeriod(period);
                editingReport.setTotalRevenue(revenue);
                editingReport.setTotalOrders(orders);
                editingReport.setTopProduct(topProduct);
                editingReport.setSummary(summary);
                editingReport.setDetails(details);
                
                dataManager.updateReport(editingReport);
                Toast.makeText(this, "Report updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Create new report
                Report newReport = new Report(title, type, period, revenue, orders, topProduct, summary, details);
                dataManager.addReport(newReport);
                Toast.makeText(this, "Report generated successfully", Toast.LENGTH_SHORT).show();
            }
            
            hideReportForm();
            loadReports();
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for revenue and orders", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void deleteReport(Report report) {
        dataManager.deleteReport(report.getId());
        Toast.makeText(this, "Report deleted successfully", Toast.LENGTH_SHORT).show();
        loadReports();
    }
}
