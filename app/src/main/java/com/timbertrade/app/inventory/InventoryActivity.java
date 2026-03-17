package com.timbertrade.app.inventory;

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
import com.timbertrade.app.models.InventoryItem;
import com.timbertrade.app.orders.NewOrderActivity;
import com.timbertrade.app.reports.ReportsActivity;
import com.timbertrade.app.utils.DataManager;

import java.util.List;

public class InventoryActivity extends Activity {
    
    private static final String TAG = "InventoryActivity";
    private DataManager dataManager;
    private TableLayout inventoryTable;
    private LinearLayout formLayout;
    private boolean isFormVisible = false;
    private InventoryItem editingItem = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starting InventoryActivity");
        
        try {
            super.onCreate(savedInstanceState);
            dataManager = DataManager.getInstance();
            
            // Create main layout
            createMainLayout();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in InventoryActivity: " + e.getMessage(), e);
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
        
        // Inventory table
        createInventoryTable(contentLayout);
        
        // Add/Edit form (initially hidden)
        createInventoryForm(contentLayout);
        
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
                new int[]{Color.parseColor("#2196F3"), Color.parseColor("#1976D2")}
        );
        gradientDrawable.setCornerRadius(0);
        headerLayout.setBackground(gradientDrawable);
        headerLayout.setPadding(30, 40, 30, 40);
        
        // Title
        TextView titleText = new TextView(this);
        titleText.setText("Inventory Management");
        titleText.setTextSize(24);
        titleText.setTextColor(Color.WHITE);
        titleText.setGravity(Gravity.CENTER);
        titleText.setShadowLayer(2, 1, 1, Color.parseColor("#000000"));
        headerLayout.addView(titleText);
        
        // Subtitle
        TextView subtitleText = new TextView(this);
        subtitleText.setText("Manage Timber Stock and Supplies");
        subtitleText.setTextSize(14);
        subtitleText.setTextColor(Color.parseColor("#E3F2FD"));
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
        
        // Inventory button (active)
        Button inventoryBtn = createNavButton("Inventory", Color.parseColor("#2196F3"));
        inventoryBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Already on Inventory page", Toast.LENGTH_SHORT).show();
        });
        navLayout.addView(inventoryBtn);
        
        // Reports button
        Button reportsBtn = createNavButton("Reports", Color.parseColor("#666666"));
        reportsBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ReportsActivity.class));
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
    
    private void createInventoryTable(LinearLayout parent) {
        // Section title with add button
        LinearLayout titleLayout = new LinearLayout(this);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView titleText = new TextView(this);
        titleText.setText("Inventory List");
        titleText.setTextSize(20);
        titleText.setTextColor(Color.parseColor("#333333"));
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        titleText.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        ));
        titleLayout.addView(titleText);
        
        Button addBtn = new Button(this);
        addBtn.setText("Add New Item");
        addBtn.setBackgroundColor(Color.parseColor("#2196F3"));
        addBtn.setTextColor(Color.WHITE);
        addBtn.setPadding(20, 10, 20, 10);
        addBtn.setOnClickListener(v -> showInventoryForm());
        titleLayout.addView(addBtn);
        
        parent.addView(titleLayout);
        
        // Table
        inventoryTable = new TableLayout(this);
        inventoryTable.setBackgroundColor(Color.WHITE);
        inventoryTable.setPadding(10, 10, 10, 10);
        
        // Table header
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(Color.parseColor("#F5F5F5"));
        
        String[] headers = {"Wood Type", "Description", "Qty", "Price", "Supplier", "Actions"};
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
        
        inventoryTable.addView(headerRow);
        
        // Load inventory items
        loadInventoryItems();
        
        parent.addView(inventoryTable);
    }
    
    private void loadInventoryItems() {
        // Clear existing rows (except header)
        if (inventoryTable.getChildCount() > 1) {
            inventoryTable.removeViews(1, inventoryTable.getChildCount() - 1);
        }
        
        List<InventoryItem> items = dataManager.getAllInventoryItems();
        for (InventoryItem item : items) {
            TableRow row = createInventoryRow(item);
            inventoryTable.addView(row);
        }
    }
    
    private TableRow createInventoryRow(InventoryItem item) {
        TableRow row = new TableRow(this);
        row.setBackgroundColor(Color.WHITE);
        
        // Wood Type
        TextView woodText = new TextView(this);
        woodText.setText(item.getWoodType());
        woodText.setTextSize(12);
        woodText.setPadding(8, 10, 8, 10);
        woodText.setGravity(Gravity.CENTER);
        row.addView(woodText);
        
        // Description
        TextView descText = new TextView(this);
        descText.setText(item.getDescription());
        descText.setTextSize(12);
        descText.setPadding(8, 10, 8, 10);
        descText.setGravity(Gravity.CENTER);
        row.addView(descText);
        
        // Quantity
        TextView qtyText = new TextView(this);
        qtyText.setText(String.valueOf(item.getQuantity()));
        qtyText.setTextSize(12);
        qtyText.setPadding(8, 10, 8, 10);
        qtyText.setGravity(Gravity.CENTER);
        
        // Color code quantity
        if (item.getQuantity() < 100) {
            qtyText.setTextColor(Color.parseColor("#F44336")); // Red - Low stock
        } else if (item.getQuantity() < 300) {
            qtyText.setTextColor(Color.parseColor("#FF9800")); // Orange - Medium stock
        } else {
            qtyText.setTextColor(Color.parseColor("#4CAF50")); // Green - Good stock
        }
        row.addView(qtyText);
        
        // Price
        TextView priceText = new TextView(this);
        priceText.setText("$" + String.format("%.2f", item.getPricePerUnit()));
        priceText.setTextSize(12);
        priceText.setPadding(8, 10, 8, 10);
        priceText.setGravity(Gravity.CENTER);
        row.addView(priceText);
        
        // Supplier
        TextView supplierText = new TextView(this);
        supplierText.setText(item.getSupplier());
        supplierText.setTextSize(12);
        supplierText.setPadding(8, 10, 8, 10);
        supplierText.setGravity(Gravity.CENTER);
        row.addView(supplierText);
        
        // Actions
        LinearLayout actionsLayout = new LinearLayout(this);
        actionsLayout.setOrientation(LinearLayout.HORIZONTAL);
        actionsLayout.setGravity(Gravity.CENTER);
        
        Button editBtn = new Button(this);
        editBtn.setText("Edit");
        editBtn.setBackgroundColor(Color.parseColor("#2196F3"));
        editBtn.setTextColor(Color.WHITE);
        editBtn.setTextSize(10);
        editBtn.setPadding(8, 5, 8, 5);
        editBtn.setOnClickListener(v -> editInventoryItem(item));
        actionsLayout.addView(editBtn);
        
        Button deleteBtn = new Button(this);
        deleteBtn.setText("Delete");
        deleteBtn.setBackgroundColor(Color.parseColor("#F44336"));
        deleteBtn.setTextColor(Color.WHITE);
        deleteBtn.setTextSize(10);
        deleteBtn.setPadding(8, 5, 8, 5);
        deleteBtn.setOnClickListener(v -> deleteInventoryItem(item));
        actionsLayout.addView(deleteBtn);
        
        row.addView(actionsLayout);
        
        return row;
    }
    
    private void createInventoryForm(LinearLayout parent) {
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
        formTitle.setText("Inventory Item Details");
        formTitle.setTextSize(18);
        formTitle.setTextColor(Color.parseColor("#333333"));
        formTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        formTitle.setPadding(0, 0, 0, 20);
        formLayout.addView(formTitle);
        
        // Wood Type
        addFormField(formLayout, "Wood Type:", "woodType");
        
        // Description
        addFormField(formLayout, "Description:", "description");
        
        // Quantity
        addFormField(formLayout, "Quantity:", "quantity");
        
        // Price per Unit
        addFormField(formLayout, "Price per Unit:", "pricePerUnit");
        
        // Supplier
        addFormField(formLayout, "Supplier:", "supplier");
        
        // Location
        addFormField(formLayout, "Location:", "location");
        
        // Category
        addFormField(formLayout, "Category:", "category");
        
        // Buttons
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 20, 0, 0);
        
        Button saveBtn = new Button(this);
        saveBtn.setText("Save Item");
        saveBtn.setBackgroundColor(Color.parseColor("#2196F3"));
        saveBtn.setTextColor(Color.WHITE);
        saveBtn.setPadding(30, 15, 30, 15);
        saveBtn.setOnClickListener(v -> saveInventoryItem());
        buttonLayout.addView(saveBtn);
        
        Button cancelBtn = new Button(this);
        cancelBtn.setText("Cancel");
        cancelBtn.setBackgroundColor(Color.parseColor("#666666"));
        cancelBtn.setTextColor(Color.WHITE);
        cancelBtn.setPadding(30, 15, 30, 15);
        cancelBtn.setOnClickListener(v -> hideInventoryForm());
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
    
    private void showInventoryForm() {
        editingItem = null;
        clearForm();
        formLayout.setVisibility(View.VISIBLE);
        isFormVisible = true;
    }
    
    private void hideInventoryForm() {
        formLayout.setVisibility(View.GONE);
        isFormVisible = false;
        editingItem = null;
    }
    
    private void editInventoryItem(InventoryItem item) {
        editingItem = item;
        
        // Populate form with item data
        EditText woodTypeField = formLayout.findViewWithTag("woodType");
        EditText descriptionField = formLayout.findViewWithTag("description");
        EditText quantityField = formLayout.findViewWithTag("quantity");
        EditText priceField = formLayout.findViewWithTag("pricePerUnit");
        EditText supplierField = formLayout.findViewWithTag("supplier");
        EditText locationField = formLayout.findViewWithTag("location");
        EditText categoryField = formLayout.findViewWithTag("category");
        
        woodTypeField.setText(item.getWoodType());
        descriptionField.setText(item.getDescription());
        quantityField.setText(String.valueOf(item.getQuantity()));
        priceField.setText(String.valueOf(item.getPricePerUnit()));
        supplierField.setText(item.getSupplier());
        locationField.setText(item.getLocation());
        categoryField.setText(item.getCategory());
        
        formLayout.setVisibility(View.VISIBLE);
        isFormVisible = true;
    }
    
    private void clearForm() {
        EditText woodTypeField = formLayout.findViewWithTag("woodType");
        EditText descriptionField = formLayout.findViewWithTag("description");
        EditText quantityField = formLayout.findViewWithTag("quantity");
        EditText priceField = formLayout.findViewWithTag("pricePerUnit");
        EditText supplierField = formLayout.findViewWithTag("supplier");
        EditText locationField = formLayout.findViewWithTag("location");
        EditText categoryField = formLayout.findViewWithTag("category");
        
        woodTypeField.setText("");
        descriptionField.setText("");
        quantityField.setText("");
        priceField.setText("");
        supplierField.setText("");
        locationField.setText("");
        categoryField.setText("");
    }
    
    private void saveInventoryItem() {
        try {
            EditText woodTypeField = formLayout.findViewWithTag("woodType");
            EditText descriptionField = formLayout.findViewWithTag("description");
            EditText quantityField = formLayout.findViewWithTag("quantity");
            EditText priceField = formLayout.findViewWithTag("pricePerUnit");
            EditText supplierField = formLayout.findViewWithTag("supplier");
            EditText locationField = formLayout.findViewWithTag("location");
            EditText categoryField = formLayout.findViewWithTag("category");
            
            String woodType = woodTypeField.getText().toString().trim();
            String description = descriptionField.getText().toString().trim();
            String quantityStr = quantityField.getText().toString().trim();
            String priceStr = priceField.getText().toString().trim();
            String supplier = supplierField.getText().toString().trim();
            String location = locationField.getText().toString().trim();
            String category = categoryField.getText().toString().trim();
            
            if (woodType.isEmpty() || description.isEmpty() || quantityStr.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);
            
            if (editingItem != null) {
                // Update existing item
                editingItem.setWoodType(woodType);
                editingItem.setDescription(description);
                editingItem.setQuantity(quantity);
                editingItem.setPricePerUnit(price);
                editingItem.setSupplier(supplier);
                editingItem.setLocation(location);
                editingItem.setCategory(category);
                
                dataManager.updateInventoryItem(editingItem);
                Toast.makeText(this, "Inventory item updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Create new item
                InventoryItem newItem = new InventoryItem(woodType, description, quantity, price, supplier, location, category);
                dataManager.addInventoryItem(newItem);
                Toast.makeText(this, "Inventory item added successfully", Toast.LENGTH_SHORT).show();
            }
            
            hideInventoryForm();
            loadInventoryItems();
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for quantity and price", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving inventory item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void deleteInventoryItem(InventoryItem item) {
        dataManager.deleteInventoryItem(item.getId());
        Toast.makeText(this, "Inventory item deleted successfully", Toast.LENGTH_SHORT).show();
        loadInventoryItems();
    }
}
