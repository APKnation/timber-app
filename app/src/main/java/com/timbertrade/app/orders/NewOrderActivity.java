package com.timbertrade.app.orders;

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
import com.timbertrade.app.models.Order;
import com.timbertrade.app.reports.ReportsActivity;
import com.timbertrade.app.utils.DataManager;

import java.util.List;

public class NewOrderActivity extends Activity {
    
    private static final String TAG = "NewOrderActivity";
    private DataManager dataManager;
    private TableLayout ordersTable;
    private LinearLayout formLayout;
    private boolean isFormVisible = false;
    private Order editingOrder = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starting NewOrderActivity");
        
        try {
            super.onCreate(savedInstanceState);
            dataManager = DataManager.getInstance();
            
            // Create main layout
            createMainLayout();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in NewOrderActivity: " + e.getMessage(), e);
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
        
        // Orders table
        createOrdersTable(contentLayout);
        
        // Add/Edit form (initially hidden)
        createOrderForm(contentLayout);
        
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
                new int[]{Color.parseColor("#4CAF50"), Color.parseColor("#388E3C")}
        );
        gradientDrawable.setCornerRadius(0);
        headerLayout.setBackground(gradientDrawable);
        headerLayout.setPadding(30, 40, 30, 40);
        
        // Title
        TextView titleText = new TextView(this);
        titleText.setText("Order Management");
        titleText.setTextSize(24);
        titleText.setTextColor(Color.WHITE);
        titleText.setGravity(Gravity.CENTER);
        titleText.setShadowLayer(2, 1, 1, Color.parseColor("#000000"));
        headerLayout.addView(titleText);
        
        // Subtitle
        TextView subtitleText = new TextView(this);
        subtitleText.setText("Create and Manage Timber Orders");
        subtitleText.setTextSize(14);
        subtitleText.setTextColor(Color.parseColor("#E8F5E8"));
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
        Button dashboardBtn = createNavButton("Dashboard", Color.parseColor("#2E7D32"));
        dashboardBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, RealDashboardActivity.class));
        });
        navLayout.addView(dashboardBtn);
        
        // Orders button (active)
        Button ordersBtn = createNavButton("Orders", Color.parseColor("#4CAF50"));
        ordersBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Already on Orders page", Toast.LENGTH_SHORT).show();
        });
        navLayout.addView(ordersBtn);
        
        // Inventory button
        Button inventoryBtn = createNavButton("Inventory", Color.parseColor("#666666"));
        inventoryBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, InventoryActivity.class));
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
    
    private void createOrdersTable(LinearLayout parent) {
        // Section title with add button
        LinearLayout titleLayout = new LinearLayout(this);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView titleText = new TextView(this);
        titleText.setText("Orders List");
        titleText.setTextSize(20);
        titleText.setTextColor(Color.parseColor("#333333"));
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        titleText.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
        ));
        titleLayout.addView(titleText);
        
        Button addBtn = new Button(this);
        addBtn.setText("Add New Order");
        addBtn.setBackgroundColor(Color.parseColor("#4CAF50"));
        addBtn.setTextColor(Color.WHITE);
        addBtn.setPadding(20, 10, 20, 10);
        addBtn.setOnClickListener(v -> showOrderForm());
        titleLayout.addView(addBtn);
        
        parent.addView(titleLayout);
        
        // Table
        ordersTable = new TableLayout(this);
        ordersTable.setBackgroundColor(Color.WHITE);
        ordersTable.setPadding(10, 10, 10, 10);
        
        // Table header
        TableRow headerRow = new TableRow(this);
        headerRow.setBackgroundColor(Color.parseColor("#F5F5F5"));
        
        String[] headers = {"Customer", "Wood Type", "Qty", "Price", "Status", "Actions"};
        for (String header : headers) {
            TextView headerText = new TextView(this);
            headerText.setText(header);
            headerText.setTextSize(12);
            headerText.setTextColor(Color.parseColor("#333333"));
            headerText.setTypeface(null, android.graphics.Typeface.BOLD);
            headerText.setPadding(10, 10, 10, 10);
            headerText.setGravity(Gravity.CENTER);
            headerRow.addView(headerText);
        }
        
        ordersTable.addView(headerRow);
        
        // Load orders
        loadOrders();
        
        parent.addView(ordersTable);
    }
    
    private void loadOrders() {
        // Clear existing rows (except header)
        if (ordersTable.getChildCount() > 1) {
            ordersTable.removeViews(1, ordersTable.getChildCount() - 1);
        }
        
        List<Order> orders = dataManager.getAllOrders();
        for (Order order : orders) {
            TableRow row = createOrderRow(order);
            ordersTable.addView(row);
        }
    }
    
    private TableRow createOrderRow(Order order) {
        TableRow row = new TableRow(this);
        row.setBackgroundColor(Color.WHITE);
        
        // Customer
        TextView customerText = new TextView(this);
        customerText.setText(order.getCustomerName());
        customerText.setTextSize(12);
        customerText.setPadding(10, 10, 10, 10);
        customerText.setGravity(Gravity.CENTER);
        row.addView(customerText);
        
        // Wood Type
        TextView woodText = new TextView(this);
        woodText.setText(order.getWoodType());
        woodText.setTextSize(12);
        woodText.setPadding(10, 10, 10, 10);
        woodText.setGravity(Gravity.CENTER);
        row.addView(woodText);
        
        // Quantity
        TextView qtyText = new TextView(this);
        qtyText.setText(String.valueOf(order.getQuantity()));
        qtyText.setTextSize(12);
        qtyText.setPadding(10, 10, 10, 10);
        qtyText.setGravity(Gravity.CENTER);
        row.addView(qtyText);
        
        // Price
        TextView priceText = new TextView(this);
        priceText.setText("$" + String.format("%.2f", order.getPrice()));
        priceText.setTextSize(12);
        priceText.setPadding(10, 10, 10, 10);
        priceText.setGravity(Gravity.CENTER);
        row.addView(priceText);
        
        // Status
        TextView statusText = new TextView(this);
        statusText.setText(order.getStatus());
        statusText.setTextSize(12);
        statusText.setPadding(10, 10, 10, 10);
        statusText.setGravity(Gravity.CENTER);
        
        // Color code status
        switch (order.getStatus()) {
            case "Pending":
                statusText.setTextColor(Color.parseColor("#FF9800"));
                break;
            case "Confirmed":
                statusText.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "Delivered":
                statusText.setTextColor(Color.parseColor("#2196F3"));
                break;
            default:
                statusText.setTextColor(Color.parseColor("#666666"));
        }
        row.addView(statusText);
        
        // Actions
        LinearLayout actionsLayout = new LinearLayout(this);
        actionsLayout.setOrientation(LinearLayout.HORIZONTAL);
        actionsLayout.setGravity(Gravity.CENTER);
        
        Button editBtn = new Button(this);
        editBtn.setText("Edit");
        editBtn.setBackgroundColor(Color.parseColor("#2196F3"));
        editBtn.setTextColor(Color.WHITE);
        editBtn.setTextSize(10);
        editBtn.setPadding(10, 5, 10, 5);
        editBtn.setOnClickListener(v -> editOrder(order));
        actionsLayout.addView(editBtn);
        
        Button deleteBtn = new Button(this);
        deleteBtn.setText("Delete");
        deleteBtn.setBackgroundColor(Color.parseColor("#F44336"));
        deleteBtn.setTextColor(Color.WHITE);
        deleteBtn.setTextSize(10);
        deleteBtn.setPadding(10, 5, 10, 5);
        deleteBtn.setOnClickListener(v -> deleteOrder(order));
        actionsLayout.addView(deleteBtn);
        
        row.addView(actionsLayout);
        
        return row;
    }
    
    private void createOrderForm(LinearLayout parent) {
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
        formTitle.setText("Order Details");
        formTitle.setTextSize(18);
        formTitle.setTextColor(Color.parseColor("#333333"));
        formTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        formTitle.setPadding(0, 0, 0, 20);
        formLayout.addView(formTitle);
        
        // Customer Name
        addFormField(formLayout, "Customer Name:", "customerName");
        
        // Wood Type
        addFormField(formLayout, "Wood Type:", "woodType");
        
        // Quantity
        addFormField(formLayout, "Quantity:", "quantity");
        
        // Price
        addFormField(formLayout, "Price per Unit:", "price");
        
        // Delivery Date
        addFormField(formLayout, "Delivery Date:", "deliveryDate");
        
        // Notes
        addFormField(formLayout, "Notes:", "notes");
        
        // Buttons
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, 20, 0, 0);
        
        Button saveBtn = new Button(this);
        saveBtn.setText("Save Order");
        saveBtn.setBackgroundColor(Color.parseColor("#4CAF50"));
        saveBtn.setTextColor(Color.WHITE);
        saveBtn.setPadding(30, 15, 30, 15);
        saveBtn.setOnClickListener(v -> saveOrder());
        buttonLayout.addView(saveBtn);
        
        Button cancelBtn = new Button(this);
        cancelBtn.setText("Cancel");
        cancelBtn.setBackgroundColor(Color.parseColor("#666666"));
        cancelBtn.setTextColor(Color.WHITE);
        cancelBtn.setPadding(30, 15, 30, 15);
        cancelBtn.setOnClickListener(v -> hideOrderForm());
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
    
    private void showOrderForm() {
        editingOrder = null;
        clearForm();
        formLayout.setVisibility(View.VISIBLE);
        isFormVisible = true;
    }
    
    private void hideOrderForm() {
        formLayout.setVisibility(View.GONE);
        isFormVisible = false;
        editingOrder = null;
    }
    
    private void editOrder(Order order) {
        editingOrder = order;
        
        // Populate form with order data
        EditText customerNameField = formLayout.findViewWithTag("customerName");
        EditText woodTypeField = formLayout.findViewWithTag("woodType");
        EditText quantityField = formLayout.findViewWithTag("quantity");
        EditText priceField = formLayout.findViewWithTag("price");
        EditText deliveryDateField = formLayout.findViewWithTag("deliveryDate");
        EditText notesField = formLayout.findViewWithTag("notes");
        
        customerNameField.setText(order.getCustomerName());
        woodTypeField.setText(order.getWoodType());
        quantityField.setText(String.valueOf(order.getQuantity()));
        priceField.setText(String.valueOf(order.getPrice()));
        deliveryDateField.setText(order.getDeliveryDate());
        notesField.setText(order.getNotes());
        
        formLayout.setVisibility(View.VISIBLE);
        isFormVisible = true;
    }
    
    private void clearForm() {
        EditText customerNameField = formLayout.findViewWithTag("customerName");
        EditText woodTypeField = formLayout.findViewWithTag("woodType");
        EditText quantityField = formLayout.findViewWithTag("quantity");
        EditText priceField = formLayout.findViewWithTag("price");
        EditText deliveryDateField = formLayout.findViewWithTag("deliveryDate");
        EditText notesField = formLayout.findViewWithTag("notes");
        
        customerNameField.setText("");
        woodTypeField.setText("");
        quantityField.setText("");
        priceField.setText("");
        deliveryDateField.setText("");
        notesField.setText("");
    }
    
    private void saveOrder() {
        try {
            EditText customerNameField = formLayout.findViewWithTag("customerName");
            EditText woodTypeField = formLayout.findViewWithTag("woodType");
            EditText quantityField = formLayout.findViewWithTag("quantity");
            EditText priceField = formLayout.findViewWithTag("price");
            EditText deliveryDateField = formLayout.findViewWithTag("deliveryDate");
            EditText notesField = formLayout.findViewWithTag("notes");
            
            String customerName = customerNameField.getText().toString().trim();
            String woodType = woodTypeField.getText().toString().trim();
            String quantityStr = quantityField.getText().toString().trim();
            String priceStr = priceField.getText().toString().trim();
            String deliveryDate = deliveryDateField.getText().toString().trim();
            String notes = notesField.getText().toString().trim();
            
            if (customerName.isEmpty() || woodType.isEmpty() || quantityStr.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);
            
            if (editingOrder != null) {
                // Update existing order
                editingOrder.setCustomerName(customerName);
                editingOrder.setWoodType(woodType);
                editingOrder.setQuantity(quantity);
                editingOrder.setPrice(price);
                editingOrder.setDeliveryDate(deliveryDate);
                editingOrder.setNotes(notes);
                
                dataManager.updateOrder(editingOrder);
                Toast.makeText(this, "Order updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Create new order
                Order newOrder = new Order(customerName, woodType, quantity, price, deliveryDate, notes);
                dataManager.addOrder(newOrder);
                Toast.makeText(this, "Order created successfully", Toast.LENGTH_SHORT).show();
            }
            
            hideOrderForm();
            loadOrders();
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for quantity and price", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void deleteOrder(Order order) {
        dataManager.deleteOrder(order.getId());
        Toast.makeText(this, "Order deleted successfully", Toast.LENGTH_SHORT).show();
        loadOrders();
    }
}
