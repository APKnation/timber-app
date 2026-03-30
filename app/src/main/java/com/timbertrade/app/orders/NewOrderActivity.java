package com.timbertrade.app.orders;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.timbertrade.app.dashboard.RealDashboardActivity;
import com.timbertrade.app.inventory.InventoryActivity;
import com.timbertrade.app.marketplace.MarketplaceActivity;
import com.timbertrade.app.dashboard.ProfileActivity;
import com.timbertrade.app.models.Order;
import com.timbertrade.app.utils.DataManager;

import java.util.List;

public class NewOrderActivity extends Activity {
    
    private static final String TAG = "NewOrderActivity";
    private DataManager dataManager;
    private LinearLayout ordersListContainer;
    private LinearLayout formLayout;
    private Order editingOrder = null;
    
    // Modern Color Palette
    private final int COLOR_PRIMARY = Color.parseColor("#059669"); 
    private final int COLOR_PRIMARY_DARK = Color.parseColor("#047857");
    private final int COLOR_ACCENT = Color.parseColor("#D97706"); 
    private final int COLOR_BG = Color.parseColor("#F3F4F6"); 
    private final int COLOR_TEXT_PRIMARY = Color.parseColor("#1F2937");
    private final int COLOR_TEXT_SECONDARY = Color.parseColor("#6B7280");
    private final int COLOR_WHITE = Color.WHITE;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting Advanced NewOrderActivity");
        
        try {
            dataManager = DataManager.getInstance();
            createAdvancedMainLayout();
        } catch (Exception e) {
            Log.e(TAG, "Error in NewOrderActivity: " + e.getMessage(), e);
        }
    }
    
    private void createAdvancedMainLayout() {
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(COLOR_BG);
        
        // 1. Decorative Header Background
        View headerBg = new View(this);
        GradientDrawable gradientBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{COLOR_PRIMARY, COLOR_PRIMARY_DARK}
        );
        headerBg.setBackground(gradientBg);
        
        RelativeLayout.LayoutParams headerBgParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(200)
        );
        headerBgParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(headerBg, headerBgParams);

        // Custom App Bar Toolbar
        int toolbarId = View.generateViewId();
        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setId(toolbarId);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setPadding(dpToPx(20), dpToPx(40), dpToPx(20), dpToPx(20));
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView titleText = new TextView(this);
        titleText.setText("Order Management");
        titleText.setTextSize(24);
        titleText.setTextColor(COLOR_WHITE);
        titleText.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleText.setLayoutParams(titleParams);
        toolbar.addView(titleText);
        
        // Add Button inside Toolbar
        TextView addBtn = new TextView(this);
        addBtn.setText("+ NEW");
        addBtn.setTextColor(Color.parseColor("#D1FAE5"));
        addBtn.setTextSize(14);
        addBtn.setTypeface(null, Typeface.BOLD);
        addBtn.setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(10));
        GradientDrawable addBtnBg = new GradientDrawable();
        addBtnBg.setColor(Color.parseColor("#047857")); // Darker green
        addBtnBg.setCornerRadius(dpToPx(100));
        addBtn.setBackground(addBtnBg);
        addBtn.setOnClickListener(v -> showOrderForm());
        toolbar.addView(addBtn);
        
        RelativeLayout.LayoutParams toolbarParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        toolbarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(toolbar, toolbarParams);

        // 2. Bottom Navigation
        int bottomNavId = View.generateViewId();
        View bottomNav = createModernBottomNav();
        bottomNav.setId(bottomNavId);

        RelativeLayout.LayoutParams navParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        navParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        root.addView(bottomNav, navParams);
        
        // 3. Scrollable Foreground Content
        ScrollView scrollView = new ScrollView(this);
        scrollView.setVerticalScrollBarEnabled(false);
        RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        scrollParams.addRule(RelativeLayout.BELOW, toolbarId);
        scrollParams.addRule(RelativeLayout.ABOVE, bottomNavId);
        
        LinearLayout scrollContent = new LinearLayout(this);
        scrollContent.setOrientation(LinearLayout.VERTICAL);
        scrollContent.setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(40));
        
        // Add/Edit Form Card (initially hidden)
        createOrderForm(scrollContent);
        
        // Orders List Container
        ordersListContainer = new LinearLayout(this);
        ordersListContainer.setOrientation(LinearLayout.VERTICAL);
        scrollContent.addView(ordersListContainer);
        
        loadOrders();
        
        scrollView.addView(scrollContent);
        root.addView(scrollView, scrollParams);
        
        setContentView(root);
    }
    
    private void loadOrders() {
        ordersListContainer.removeAllViews();
        
        List<Order> orders = dataManager.getAllOrders();
        
        if (orders.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No orders found.\nTap + NEW to create one.");
            emptyText.setTextSize(16);
            emptyText.setTextColor(COLOR_TEXT_SECONDARY);
            emptyText.setGravity(Gravity.CENTER);
            emptyText.setPadding(dpToPx(20), dpToPx(60), dpToPx(20), dpToPx(20));
            ordersListContainer.addView(emptyText);
            return;
        }
        
        for (Order order : orders) {
            ordersListContainer.addView(createOrderCard(order));
        }
    }
    
    private View createOrderCard(Order order) {
        LinearLayout outerLayout = new LinearLayout(this);
        outerLayout.setPadding(0, dpToPx(6), 0, dpToPx(6));
        
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_WHITE);
        bg.setCornerRadius(dpToPx(20));
        card.setBackground(bg);
        card.setElevation(dpToPx(6));
        
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        card.setLayoutParams(cardParams);
        
        // Header Row (Customer & Status)
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView customerText = new TextView(this);
        customerText.setText(order.getCustomerName());
        customerText.setTextSize(18);
        customerText.setTypeface(null, Typeface.BOLD);
        customerText.setTextColor(COLOR_TEXT_PRIMARY);
        LinearLayout.LayoutParams custParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        customerText.setLayoutParams(custParams);
        
        TextView statusChip = new TextView(this);
        statusChip.setText(order.getStatus().toString());
        statusChip.setTextSize(12);
        statusChip.setTypeface(null, Typeface.BOLD);
        statusChip.setPadding(dpToPx(12), dpToPx(4), dpToPx(12), dpToPx(4));
        
        GradientDrawable statusBg = new GradientDrawable();
        statusBg.setCornerRadius(dpToPx(12));
        
        switch (order.getStatus()) {
            case PENDING:
                statusBg.setColor(Color.parseColor("#FEF3C7")); // Amber Light
                statusChip.setTextColor(Color.parseColor("#D97706"));
                break;
            case CONFIRMED:
                statusBg.setColor(Color.parseColor("#D1FAE5")); // Green Light
                statusChip.setTextColor(COLOR_PRIMARY);
                break;
            case DELIVERED:
                statusBg.setColor(Color.parseColor("#DBEAFE")); // Blue Light
                statusChip.setTextColor(Color.parseColor("#2563EB"));
                break;
            default:
                statusBg.setColor(Color.parseColor("#F3F4F6"));
                statusChip.setTextColor(COLOR_TEXT_SECONDARY);
        }
        statusChip.setBackground(statusBg);
        
        headerRow.addView(customerText);
        headerRow.addView(statusChip);
        
        // Details Row
        TextView detailsText = new TextView(this);
        detailsText.setText(order.getQuantity() + " units of " + order.getWoodType());
        detailsText.setTextSize(14);
        detailsText.setTextColor(COLOR_TEXT_SECONDARY);
        detailsText.setPadding(0, dpToPx(8), 0, dpToPx(8));
        
        TextView priceAndDateText = new TextView(this);
        priceAndDateText.setText(String.format("$%.2f • Delivery: %s", order.getPrice(), order.getDeliveryDate()));
        priceAndDateText.setTextSize(14);
        priceAndDateText.setTextColor(COLOR_TEXT_SECONDARY);
        
        // Action Buttons Row
        LinearLayout actionsRow = new LinearLayout(this);
        actionsRow.setOrientation(LinearLayout.HORIZONTAL);
        actionsRow.setGravity(Gravity.END);
        actionsRow.setPadding(0, dpToPx(12), 0, 0);
        
        TextView editBtn = createActionButton("Edit", COLOR_PRIMARY);
        editBtn.setOnClickListener(v -> editOrder(order));
        
        TextView deleteBtn = createActionButton("Delete", Color.parseColor("#EF4444")); // Red
        deleteBtn.setOnClickListener(v -> deleteOrder(order));
        
        actionsRow.addView(editBtn);
        actionsRow.addView(deleteBtn);
        
        card.addView(headerRow);
        card.addView(detailsText);
        card.addView(priceAndDateText);
        card.addView(actionsRow);
        
        outerLayout.addView(card);
        return outerLayout;
    }
    
    private TextView createActionButton(String text, int color) {
        TextView btn = new TextView(this);
        btn.setText(text);
        btn.setTextColor(color);
        btn.setTextSize(14);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));
        
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        btn.setBackgroundResource(outValue.resourceId);
        btn.setClickable(true);
        
        return btn;
    }
    
    private void createOrderForm(LinearLayout parent) {
        formLayout = new LinearLayout(this);
        formLayout.setOrientation(LinearLayout.VERTICAL);
        formLayout.setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(24));
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_WHITE);
        bg.setCornerRadius(dpToPx(24));
        formLayout.setBackground(bg);
        formLayout.setElevation(dpToPx(12));
        
        LinearLayout.LayoutParams formParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        formParams.setMargins(0, 0, 0, dpToPx(20));
        formLayout.setLayoutParams(formParams);
        
        // Form title
        TextView formTitle = new TextView(this);
        formTitle.setText("Order Details");
        formTitle.setTextSize(20);
        formTitle.setTextColor(COLOR_TEXT_PRIMARY);
        formTitle.setTypeface(null, Typeface.BOLD);
        formTitle.setPadding(0, 0, 0, dpToPx(20));
        formLayout.addView(formTitle);
        
        addFormField(formLayout, "Customer Name", "customerName");
        addFormField(formLayout, "Wood Type", "woodType");
        
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setWeightSum(2f);
        
        LinearLayout col1 = new LinearLayout(this);
        col1.setOrientation(LinearLayout.VERTICAL);
        col1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        col1.setPadding(0, 0, dpToPx(10), 0);
        addFormField(col1, "Quantity", "quantity");
        
        LinearLayout col2 = new LinearLayout(this);
        col2.setOrientation(LinearLayout.VERTICAL);
        col2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        col2.setPadding(dpToPx(10), 0, 0, 0);
        addFormField(col2, "Price per Unit", "price");
        
        row.addView(col1);
        row.addView(col2);
        formLayout.addView(row);
        
        addFormField(formLayout, "Delivery Date", "deliveryDate");
        addFormField(formLayout, "Notes", "notes");
        
        // Buttons
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, dpToPx(20), 0, 0);
        
        Button cancelBtn = new Button(this);
        cancelBtn.setText("Cancel");
        cancelBtn.setBackgroundColor(Color.TRANSPARENT);
        cancelBtn.setTextColor(COLOR_TEXT_SECONDARY);
        cancelBtn.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        cancelParams.setMargins(0, 0, dpToPx(10), 0);
        cancelBtn.setLayoutParams(cancelParams);
        cancelBtn.setOnClickListener(v -> hideOrderForm());
        buttonLayout.addView(cancelBtn);

        Button saveBtn = new Button(this);
        saveBtn.setText("Save Update");
        
        GradientDrawable saveBg = new GradientDrawable();
        saveBg.setColor(COLOR_PRIMARY);
        saveBg.setCornerRadius(dpToPx(12));
        saveBtn.setBackground(saveBg);
        
        saveBtn.setTextColor(COLOR_WHITE);
        saveBtn.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
        LinearLayout.LayoutParams saveParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        saveBtn.setLayoutParams(saveParams);
        saveBtn.setOnClickListener(v -> saveOrder());
        buttonLayout.addView(saveBtn);
        
        formLayout.addView(buttonLayout);
        
        parent.addView(formLayout);
        formLayout.setVisibility(View.GONE);
    }
    
    private void addFormField(LinearLayout parent, String hint, String tag) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setTextSize(14);
        editText.setTextColor(COLOR_TEXT_PRIMARY);
        editText.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        editText.setTag(tag);
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_BG);
        bg.setCornerRadius(dpToPx(12));
        editText.setBackground(bg);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(16));
        editText.setLayoutParams(params);
        
        parent.addView(editText);
    }
    
    private void showOrderForm() {
        editingOrder = null;
        clearForm();
        formLayout.setVisibility(View.VISIBLE);
        // Ensure user can see form at top
        ((ScrollView)formLayout.getParent().getParent()).smoothScrollTo(0, 0);
    }
    
    private void hideOrderForm() {
        formLayout.setVisibility(View.GONE);
        editingOrder = null;
    }
    
    private void editOrder(Order order) {
        editingOrder = order;
        
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
        ((ScrollView)formLayout.getParent().getParent()).smoothScrollTo(0, 0);
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
                editingOrder.setCustomerName(customerName);
                editingOrder.setWoodType(woodType);
                editingOrder.setQuantity(quantity);
                editingOrder.setPrice(price);
                editingOrder.setDeliveryDate(deliveryDate);
                editingOrder.setNotes(notes);
                dataManager.updateOrder(editingOrder);
                Toast.makeText(this, "Order updated successfully", Toast.LENGTH_SHORT).show();
            } else {
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

    private View createModernBottomNav() {
        LinearLayout navBar = new LinearLayout(this);
        navBar.setOrientation(LinearLayout.HORIZONTAL);
        navBar.setBackgroundColor(COLOR_WHITE);
        navBar.setElevation(dpToPx(16)); 
        navBar.setPadding(0, dpToPx(10), 0, dpToPx(10));
        navBar.setWeightSum(4f);
        
        GradientDrawable topBorderBg = new GradientDrawable();
        topBorderBg.setColor(COLOR_WHITE);
        navBar.setBackground(topBorderBg);
        
        navBar.addView(createModernNavTab("Home", "H", false));
        navBar.addView(createModernNavTab("Market", "M", false));
        navBar.addView(createModernNavTab("Orders", "O", true));
        navBar.addView(createModernNavTab("Profile", "P", false));

        return navBar;
    }

    private View createModernNavTab(String text, String letterIcon, boolean isActive) {
        LinearLayout tab = new LinearLayout(this);
        tab.setOrientation(LinearLayout.VERTICAL);
        tab.setGravity(Gravity.CENTER);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tab.setLayoutParams(params);
        tab.setPadding(dpToPx(6), dpToPx(8), dpToPx(6), dpToPx(8));
        
        int color = isActive ? COLOR_PRIMARY : Color.parseColor("#9CA3AF");

        TextView icon = new TextView(this);
        icon.setText(letterIcon);
        icon.setTextSize(20);
        icon.setTypeface(null, Typeface.BOLD);
        icon.setTextColor(color);

        if (isActive) {
            GradientDrawable activeBg = new GradientDrawable();
            activeBg.setColor(Color.parseColor("#D1FAE5")); 
            activeBg.setCornerRadius(dpToPx(16));
            icon.setBackground(activeBg);
            icon.setPadding(dpToPx(20), dpToPx(4), dpToPx(20), dpToPx(4));
        } else {
            icon.setPadding(dpToPx(20), dpToPx(4), dpToPx(20), dpToPx(4));
        }

        TextView label = new TextView(this);
        label.setText(text);
        label.setTextSize(11);
        label.setTextColor(color);
        label.setPadding(0, dpToPx(4), 0, 0);

        if (isActive) {
            label.setTypeface(null, Typeface.BOLD);
        }

        tab.addView(icon);
        tab.addView(label);

        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
        tab.setBackgroundResource(outValue.resourceId);
        tab.setClickable(true);

        if (text.equals("Home") && !isActive) {
            tab.setOnClickListener(v -> {
                startActivity(new Intent(NewOrderActivity.this, RealDashboardActivity.class));
            });
        } else if (text.equals("Market") && !isActive) {
            tab.setOnClickListener(v -> {
                startActivity(new Intent(NewOrderActivity.this, MarketplaceActivity.class));
            });
        } else if (text.equals("Profile") && !isActive) {
            tab.setOnClickListener(v -> {
                startActivity(new Intent(NewOrderActivity.this, ProfileActivity.class));
            });
        }

        return tab;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }
}
