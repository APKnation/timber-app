package com.timbertrade.app.orders;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.content.Context;
import android.widget.FrameLayout;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;

import com.timbertrade.app.dashboard.RealDashboardActivity;
import com.timbertrade.app.inventory.InventoryActivity;
import com.timbertrade.app.marketplace.MarketplaceActivity;
import com.timbertrade.app.dashboard.ProfileActivity;
import com.timbertrade.app.models.Order;
import com.timbertrade.app.utils.DataManager;

import java.util.List;

public class NewOrderFragment extends Fragment {
    
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            dataManager = DataManager.getInstance();
            return createAdvancedLayout();
        } catch (Exception e) {
            Log.e(TAG, "Error in NewOrderActivity: " + e.getMessage(), e);
            return new FrameLayout(getContext());
        }
    }
    
    private View createAdvancedLayout()  {
        RelativeLayout root = new RelativeLayout(getContext());
        root.setBackgroundColor(COLOR_BG);
        
        // 1. Decorative Header Background
        View headerBg = new View(getContext());
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
        LinearLayout toolbar = new LinearLayout(getContext());
        toolbar.setId(toolbarId);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setPadding(dpToPx(20), dpToPx(40), dpToPx(20), dpToPx(20));
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView titleText = new TextView(getContext());
        titleText.setText("Order Management");
        titleText.setTextSize(24);
        titleText.setTextColor(COLOR_WHITE);
        titleText.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleText.setLayoutParams(titleParams);
        toolbar.addView(titleText);
        
        // Add Button inside Toolbar
        TextView addBtn = new TextView(getContext());
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

        // Bottom Nav handled by Host Activity
        
        // 3. Scrollable Foreground Content
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setVerticalScrollBarEnabled(false);
        RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        scrollParams.addRule(RelativeLayout.BELOW, toolbarId);
        
        LinearLayout scrollContent = new LinearLayout(getContext());
        scrollContent.setOrientation(LinearLayout.VERTICAL);
        scrollContent.setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(40));
        
        // Add/Edit Form Card (initially hidden)
        createOrderForm(scrollContent);
        
        // Orders List Container
        ordersListContainer = new LinearLayout(getContext());
        ordersListContainer.setOrientation(LinearLayout.VERTICAL);
        scrollContent.addView(ordersListContainer);
        
        loadOrders();
        
        scrollView.addView(scrollContent);
        root.addView(scrollView, scrollParams);
        
        return root;
    }
    
    private void loadOrders() {
        ordersListContainer.removeAllViews();
        
        List<Order> orders = dataManager.getAllOrders();
        
        if (orders.isEmpty()) {
            TextView emptyText = new TextView(getContext());
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
        LinearLayout outerLayout = new LinearLayout(getContext());
        outerLayout.setPadding(0, dpToPx(6), 0, dpToPx(6));
        
        LinearLayout card = new LinearLayout(getContext());
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
        LinearLayout headerRow = new LinearLayout(getContext());
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView customerText = new TextView(getContext());
        customerText.setText(order.getCustomerName());
        customerText.setTextSize(18);
        customerText.setTypeface(null, Typeface.BOLD);
        customerText.setTextColor(COLOR_TEXT_PRIMARY);
        LinearLayout.LayoutParams custParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        customerText.setLayoutParams(custParams);
        
        TextView statusChip = new TextView(getContext());
        statusChip.setText(order.getStatus());
        statusChip.setTextSize(12);
        statusChip.setTypeface(null, Typeface.BOLD);
        statusChip.setPadding(dpToPx(12), dpToPx(4), dpToPx(12), dpToPx(4));
        
        GradientDrawable statusBg = new GradientDrawable();
        statusBg.setCornerRadius(dpToPx(12));
        
        switch (order.getStatus()) {
            case "Pending":
                statusBg.setColor(Color.parseColor("#FEF3C7")); // Amber Light
                statusChip.setTextColor(Color.parseColor("#D97706"));
                break;
            case "Confirmed":
                statusBg.setColor(Color.parseColor("#D1FAE5")); // Green Light
                statusChip.setTextColor(COLOR_PRIMARY);
                break;
            case "Delivered":
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
        TextView detailsText = new TextView(getContext());
        detailsText.setText(order.getQuantity() + " units of " + order.getWoodType());
        detailsText.setTextSize(14);
        detailsText.setTextColor(COLOR_TEXT_SECONDARY);
        detailsText.setPadding(0, dpToPx(8), 0, dpToPx(8));
        
        TextView priceAndDateText = new TextView(getContext());
        priceAndDateText.setText(String.format("$%.2f • Delivery: %s", order.getPrice(), order.getDeliveryDate()));
        priceAndDateText.setTextSize(14);
        priceAndDateText.setTextColor(COLOR_TEXT_SECONDARY);
        
        // Action Buttons Row
        LinearLayout actionsRow = new LinearLayout(getContext());
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
        TextView btn = new TextView(getContext());
        btn.setText(text);
        btn.setTextColor(color);
        btn.setTextSize(14);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));
        
        TypedValue outValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        btn.setBackgroundResource(outValue.resourceId);
        btn.setClickable(true);
        
        return btn;
    }
       private void createOrderForm(LinearLayout parent) {
        // Form is now managed via BottomSheetDialog in showOrderForm and editOrder
        formLayout = new LinearLayout(getContext()); // Keep reference but it will be inside dialog
        formLayout.setVisibility(View.GONE);
    }
    
    private void showOrderBottomSheet(Order orderToEdit) {
        final BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        
        LinearLayout sheetView = new LinearLayout(requireContext());
        sheetView.setOrientation(LinearLayout.VERTICAL);
        sheetView.setPadding(dpToPx(24), dpToPx(32), dpToPx(24), dpToPx(32));
        sheetView.setBackgroundColor(COLOR_WHITE);
        
        // Handle visual cue
        View handle = new View(requireContext());
        handle.setBackgroundColor(Color.LTGRAY);
        LinearLayout.LayoutParams handleParams = new LinearLayout.LayoutParams(dpToPx(40), dpToPx(4));
        handleParams.gravity = Gravity.CENTER_HORIZONTAL;
        handleParams.bottomMargin = dpToPx(24);
        sheetView.addView(handle, handleParams);

        TextView title = new TextView(requireContext());
        title.setText(orderToEdit == null ? "New Order" : "Edit Order");
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(COLOR_TEXT_PRIMARY);
        title.setPadding(0, 0, 0, dpToPx(24));
        sheetView.addView(title);

        ScrollView scrollForm = new ScrollView(requireContext());
        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);

        TextInputLayout tilCustomer = createMaterialField("Customer Name", orderToEdit != null ? orderToEdit.getCustomerName() : "");
        TextInputLayout tilWood = createMaterialField("Wood Type", orderToEdit != null ? orderToEdit.getWoodType() : "");
        TextInputLayout tilQuantity = createMaterialField("Quantity", orderToEdit != null ? String.valueOf(orderToEdit.getQuantity()) : "");
        TextInputLayout tilPrice = createMaterialField("Price ($)", orderToEdit != null ? String.valueOf(orderToEdit.getPrice()) : "");
        TextInputLayout tilDate = createMaterialField("Delivery Date", orderToEdit != null ? orderToEdit.getDeliveryDate() : "");
        TextInputLayout tilNotes = createMaterialField("Order Notes", orderToEdit != null ? orderToEdit.getNotes() : "");

        container.addView(tilCustomer);
        container.addView(tilWood);
        
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowItemParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tilQuantity.setLayoutParams(rowItemParams);
        tilPrice.setLayoutParams(rowItemParams);
        row.addView(tilQuantity);
        row.addView(tilPrice);
        container.addView(row);
        
        container.addView(tilDate);
        container.addView(tilNotes);

        MaterialButton btnSave = new MaterialButton(requireContext());
        btnSave.setText(orderToEdit == null ? "Create Order" : "Save Changes");
        btnSave.setBackgroundTintList(ColorStateList.valueOf(COLOR_PRIMARY));
        btnSave.setTextColor(Color.WHITE);
        btnSave.setCornerRadius(dpToPx(16));
        btnSave.setPadding(0, dpToPx(16), 0, dpToPx(16));
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParams.topMargin = dpToPx(24);
        btnSave.setLayoutParams(btnParams);
        
        btnSave.setOnClickListener(v -> {
            String customer = tilCustomer.getEditText().getText().toString().trim();
            String wood = tilWood.getEditText().getText().toString().trim();
            String qty = tilQuantity.getEditText().getText().toString().trim();
            String price = tilPrice.getEditText().getText().toString().trim();
            String date = tilDate.getEditText().getText().toString().trim();
            String notes = tilNotes.getEditText().getText().toString().trim();

            if (customer.isEmpty() || wood.isEmpty() || qty.isEmpty() || price.isEmpty()) {
                Toast.makeText(getContext(), "Essential fields missing", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                if (orderToEdit != null) {
                    orderToEdit.setCustomerName(customer);
                    orderToEdit.setWoodType(wood);
                    orderToEdit.setQuantity(Integer.parseInt(qty));
                    orderToEdit.setPrice(Double.parseDouble(price));
                    orderToEdit.setDeliveryDate(date);
                    orderToEdit.setNotes(notes);
                    dataManager.updateOrder(orderToEdit);
                } else {
                    Order newOrder = new Order(customer, wood, Integer.parseInt(qty), Double.parseDouble(price), date, notes);
                    dataManager.addOrder(newOrder);
                }
                dialog.dismiss();
                loadOrders();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Invalid numbers entered", Toast.LENGTH_SHORT).show();
            }
        });

        container.addView(btnSave);
        scrollForm.addView(container);
        sheetView.addView(scrollForm);
        dialog.setContentView(sheetView);
        dialog.show();
    }

    private TextInputLayout createMaterialField(String hint, String value) {
        TextInputLayout til = new TextInputLayout(requireContext(), null, com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
        til.setHint(hint);
        til.setBoxCornerRadiusAround(dpToPx(16));
        til.setBoxStrokeColor(COLOR_PRIMARY);
        til.setHintTextColor(ColorStateList.valueOf(COLOR_PRIMARY));
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dpToPx(16);
        til.setLayoutParams(params);

        TextInputEditText et = new TextInputEditText(til.getContext());
        et.setText(value);
        et.setTextSize(14);
        et.setTextColor(COLOR_TEXT_PRIMARY);
        til.addView(et);
        
        return til;
    }
    
    private void addFormField(LinearLayout parent, String hint, String tag) {
        EditText editText = new EditText(getContext());
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
        showOrderBottomSheet(null);
    }
    
    private void hideOrderForm() {
        // Form is handled by sheet dismiss
    }
    
    private void editOrder(Order order) {
        showOrderBottomSheet(order);
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
                Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Order updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Order newOrder = new Order(customerName, woodType, quantity, price, deliveryDate, notes);
                dataManager.addOrder(newOrder);
                Toast.makeText(getContext(), "Order created successfully", Toast.LENGTH_SHORT).show();
            }
            
            hideOrderForm();
            loadOrders();
            
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid numbers for quantity and price", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error saving order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void deleteOrder(Order order) {
        dataManager.deleteOrder(order.getId());
        Toast.makeText(getContext(), "Order deleted successfully", Toast.LENGTH_SHORT).show();
        loadOrders();
    }

    private View createModernBottomNav() {
        LinearLayout navBar = new LinearLayout(getContext());
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
        LinearLayout tab = new LinearLayout(getContext());
        tab.setOrientation(LinearLayout.VERTICAL);
        tab.setGravity(Gravity.CENTER);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tab.setLayoutParams(params);
        tab.setPadding(dpToPx(6), dpToPx(8), dpToPx(6), dpToPx(8));
        
        int color = isActive ? COLOR_PRIMARY : Color.parseColor("#9CA3AF");

        TextView icon = new TextView(getContext());
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

        TextView label = new TextView(getContext());
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
        requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
        tab.setBackgroundResource(outValue.resourceId);
        tab.setClickable(true);

        if (text.equals("Home") && !isActive) {
            tab.setOnClickListener(v -> {
                startActivity(new Intent(getContext(), RealDashboardActivity.class));
            });
        } else if (text.equals("Market") && !isActive) {
            tab.setOnClickListener(v -> {
                startActivity(new Intent(getContext(), MarketplaceActivity.class));
            });
        } else if (text.equals("Profile") && !isActive) {
            tab.setOnClickListener(v -> {
                startActivity(new Intent(getContext(), ProfileActivity.class));
            });
        }

        return tab;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                requireContext().getResources().getDisplayMetrics()
        );
    }
}
