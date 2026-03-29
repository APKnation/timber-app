package com.timbertrade.app.inventory;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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

import com.timbertrade.app.models.InventoryItem;
import com.timbertrade.app.utils.DataManager;

import java.util.List;

public class InventoryActivity extends Activity {
    
    private static final String TAG = "InventoryActivity";
    private DataManager dataManager;
    private LinearLayout listContainer;
    private LinearLayout formLayout;
    private InventoryItem editingItem = null;
    
    // Modern Color Palette mapping
    private final int COLOR_PRIMARY = Color.parseColor("#059669"); 
    private final int COLOR_PRIMARY_DARK = Color.parseColor("#047857");
    private final int COLOR_BG = Color.parseColor("#F3F4F6"); 
    private final int COLOR_TEXT_PRIMARY = Color.parseColor("#1F2937");
    private final int COLOR_TEXT_SECONDARY = Color.parseColor("#6B7280");
    private final int COLOR_WHITE = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting Advanced InventoryActivity");
        
        try {
            dataManager = DataManager.getInstance();
            createAdvancedMainLayout();
        } catch (Exception e) {
            Log.e(TAG, "Error in InventoryActivity: " + e.getMessage(), e);
        }
    }

    private void createAdvancedMainLayout() {
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(COLOR_BG);
        
        // 1. Decorative Header Background
        View headerBg = new View(this);
        GradientDrawable gradientBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.parseColor("#2563EB"), Color.parseColor("#1D4ED8")} // Deep Blue
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
        
        TextView backBtn = new TextView(this);
        backBtn.setText("← Back");
        backBtn.setTextColor(Color.parseColor("#DBEAFE")); // Light blue
        backBtn.setTextSize(16);
        backBtn.setTypeface(null, Typeface.BOLD);
        backBtn.setPadding(0, dpToPx(10), dpToPx(20), dpToPx(10));
        backBtn.setOnClickListener(v -> finish());
        toolbar.addView(backBtn);
        
        TextView titleText = new TextView(this);
        titleText.setText("Stock Inventory");
        titleText.setTextSize(22);
        titleText.setTextColor(COLOR_WHITE);
        titleText.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleText.setLayoutParams(titleParams);
        toolbar.addView(titleText);
        
        // Add Button inside Toolbar
        TextView addBtn = new TextView(this);
        addBtn.setText("+ STOCK");
        addBtn.setTextColor(COLOR_WHITE);
        addBtn.setTextSize(14);
        addBtn.setTypeface(null, Typeface.BOLD);
        addBtn.setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(10));
        GradientDrawable addBtnBg = new GradientDrawable();
        addBtnBg.setColor(Color.parseColor("#1E3A8A")); // Darker blue
        addBtnBg.setCornerRadius(dpToPx(100));
        addBtn.setBackground(addBtnBg);
        addBtn.setOnClickListener(v -> showInventoryForm());
        toolbar.addView(addBtn);
        
        RelativeLayout.LayoutParams toolbarParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        toolbarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(toolbar, toolbarParams);

        // 3. Scrollable Foreground Content
        ScrollView scrollView = new ScrollView(this);
        scrollView.setVerticalScrollBarEnabled(false);
        RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        scrollParams.addRule(RelativeLayout.BELOW, toolbarId);
        
        LinearLayout scrollContent = new LinearLayout(this);
        scrollContent.setOrientation(LinearLayout.VERTICAL);
        scrollContent.setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(40));
        
        // Add/Edit Form Card (initially hidden)
        createInventoryForm(scrollContent);
        
        // List Container
        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        scrollContent.addView(listContainer);
        
        loadInventoryItems();
        
        scrollView.addView(scrollContent);
        root.addView(scrollView, scrollParams);
        
        setContentView(root);
    }
    
    private void loadInventoryItems() {
        listContainer.removeAllViews();
        List<InventoryItem> items = dataManager.getAllInventoryItems();
        
        if (items.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Inventory is empty.\nTap + STOCK to add items.");
            emptyText.setTextSize(16);
            emptyText.setTextColor(COLOR_TEXT_SECONDARY);
            emptyText.setGravity(Gravity.CENTER);
            emptyText.setPadding(dpToPx(20), dpToPx(60), dpToPx(20), dpToPx(20));
            listContainer.addView(emptyText);
            return;
        }
        
        for (InventoryItem item : items) {
            listContainer.addView(createInventoryCard(item));
        }
    }
    
    private View createInventoryCard(InventoryItem item) {
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
        
        // Header Row (Wood Type & Stock Level Chip)
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView woodText = new TextView(this);
        woodText.setText(item.getWoodType());
        woodText.setTextSize(18);
        woodText.setTypeface(null, Typeface.BOLD);
        woodText.setTextColor(COLOR_TEXT_PRIMARY);
        LinearLayout.LayoutParams woodParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        woodText.setLayoutParams(woodParams);
        
        TextView qtyChip = new TextView(this);
        int qty = item.getQuantity();
        qtyChip.setText(qty + " Units");
        qtyChip.setTextSize(12);
        qtyChip.setTypeface(null, Typeface.BOLD);
        qtyChip.setPadding(dpToPx(12), dpToPx(4), dpToPx(12), dpToPx(4));
        
        GradientDrawable qtyBg = new GradientDrawable();
        qtyBg.setCornerRadius(dpToPx(12));
        
        if (qty < 100) {
            qtyBg.setColor(Color.parseColor("#FEE2E2"));
            qtyChip.setTextColor(Color.parseColor("#EF4444")); // Red for low
        } else if (qty < 300) {
            qtyBg.setColor(Color.parseColor("#FEF3C7")); 
            qtyChip.setTextColor(Color.parseColor("#D97706")); // Amber for med
        } else {
            qtyBg.setColor(Color.parseColor("#D1FAE5")); 
            qtyChip.setTextColor(COLOR_PRIMARY); // Green for good
        }
        qtyChip.setBackground(qtyBg);
        
        headerRow.addView(woodText);
        headerRow.addView(qtyChip);
        
        // Details
        TextView descText = new TextView(this);
        descText.setText(item.getDescription() + " • " + item.getSupplier());
        descText.setTextSize(14);
        descText.setTextColor(COLOR_TEXT_SECONDARY);
        descText.setPadding(0, dpToPx(8), 0, dpToPx(4));
        
        TextView priceLocText = new TextView(this);
        priceLocText.setText(String.format("Value: $%.2f | Loc: %s", item.getPricePerUnit(), item.getLocation()));
        priceLocText.setTextSize(14);
        priceLocText.setTypeface(null, Typeface.BOLD);
        priceLocText.setTextColor(Color.parseColor("#2563EB")); // Blue text
        
        // Action Buttons Row
        LinearLayout actionsRow = new LinearLayout(this);
        actionsRow.setOrientation(LinearLayout.HORIZONTAL);
        actionsRow.setGravity(Gravity.END);
        actionsRow.setPadding(0, dpToPx(12), 0, 0);
        
        TextView editBtn = createActionButton("Edit", Color.parseColor("#2563EB"));
        editBtn.setOnClickListener(v -> editInventoryItem(item));
        
        TextView deleteBtn = createActionButton("Delete", Color.parseColor("#EF4444")); 
        deleteBtn.setOnClickListener(v -> deleteInventoryItem(item));
        
        actionsRow.addView(editBtn);
        actionsRow.addView(deleteBtn);
        
        card.addView(headerRow);
        card.addView(descText);
        card.addView(priceLocText);
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

    private void createInventoryForm(LinearLayout parent) {
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
        
        TextView formTitle = new TextView(this);
        formTitle.setText("Stock Entry");
        formTitle.setTextSize(20);
        formTitle.setTextColor(COLOR_TEXT_PRIMARY);
        formTitle.setTypeface(null, Typeface.BOLD);
        formTitle.setPadding(0, 0, 0, dpToPx(20));
        formLayout.addView(formTitle);
        
        addFormField(formLayout, "Wood Type", "woodType");
        addFormField(formLayout, "Description", "description");
        
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
        addFormField(col2, "Unit Price", "pricePerUnit");
        
        row.addView(col1);
        row.addView(col2);
        formLayout.addView(row);
        
        addFormField(formLayout, "Supplier", "supplier");
        addFormField(formLayout, "Location Area", "location");
        addFormField(formLayout, "Category", "category");
        
        // Buttons
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setPadding(0, dpToPx(10), 0, 0);
        
        Button cancelBtn = new Button(this);
        cancelBtn.setText("Cancel");
        cancelBtn.setBackgroundColor(Color.TRANSPARENT);
        cancelBtn.setTextColor(COLOR_TEXT_SECONDARY);
        cancelBtn.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
        LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        cancelParams.setMargins(0, 0, dpToPx(10), 0);
        cancelBtn.setLayoutParams(cancelParams);
        cancelBtn.setOnClickListener(v -> hideInventoryForm());
        buttonLayout.addView(cancelBtn);

        Button saveBtn = new Button(this);
        saveBtn.setText("Save Stock");
        GradientDrawable saveBg = new GradientDrawable();
        saveBg.setColor(Color.parseColor("#2563EB")); // Blue
        saveBg.setCornerRadius(dpToPx(12));
        saveBtn.setBackground(saveBg);
        saveBtn.setTextColor(COLOR_WHITE);
        saveBtn.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
        LinearLayout.LayoutParams saveParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        saveBtn.setLayoutParams(saveParams);
        saveBtn.setOnClickListener(v -> saveInventoryItem());
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

    private void showInventoryForm() {
        editingItem = null;
        clearForm();
        formLayout.setVisibility(View.VISIBLE);
        ((ScrollView)formLayout.getParent().getParent()).smoothScrollTo(0, 0);
    }

    private void hideInventoryForm() {
        formLayout.setVisibility(View.GONE);
        editingItem = null;
    }

    private void editInventoryItem(InventoryItem item) {
        editingItem = item;
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
        ((ScrollView)formLayout.getParent().getParent()).smoothScrollTo(0, 0);
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
                Toast.makeText(this, "Please fill in required fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);
            
            if (editingItem != null) {
                editingItem.setWoodType(woodType);
                editingItem.setDescription(description);
                editingItem.setQuantity(quantity);
                editingItem.setPricePerUnit(price);
                editingItem.setSupplier(supplier);
                editingItem.setLocation(location);
                editingItem.setCategory(category);
                dataManager.updateInventoryItem(editingItem);
                Toast.makeText(this, "Stock updated", Toast.LENGTH_SHORT).show();
            } else {
                InventoryItem newItem = new InventoryItem(woodType, description, quantity, price, supplier, location, category);
                dataManager.addInventoryItem(newItem);
                Toast.makeText(this, "Stock added", Toast.LENGTH_SHORT).show();
            }
            
            hideInventoryForm();
            loadInventoryItems();
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid numbers", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteInventoryItem(InventoryItem item) {
        dataManager.deleteInventoryItem(item.getId());
        Toast.makeText(this, "Stock removed", Toast.LENGTH_SHORT).show();
        loadInventoryItems();
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()
        );
    }
}
