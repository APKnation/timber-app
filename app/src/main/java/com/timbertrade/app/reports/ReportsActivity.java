package com.timbertrade.app.reports;

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

import com.timbertrade.app.models.Report;
import com.timbertrade.app.utils.DataManager;

import java.util.List;

public class ReportsActivity extends Activity {
    
    private static final String TAG = "ReportsActivity";
    private DataManager dataManager;
    private LinearLayout listContainer;
    private LinearLayout formLayout;
    private Report editingReport = null;
    
    // Modern Color Palette
    private final int COLOR_PRIMARY = Color.parseColor("#F97316"); // Orange
    private final int COLOR_PRIMARY_DARK = Color.parseColor("#EA580C");
    private final int COLOR_BG = Color.parseColor("#F3F4F6"); 
    private final int COLOR_TEXT_PRIMARY = Color.parseColor("#1F2937");
    private final int COLOR_TEXT_SECONDARY = Color.parseColor("#6B7280");
    private final int COLOR_WHITE = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting Advanced ReportsActivity");
        
        try {
            dataManager = DataManager.getInstance();
            createAdvancedMainLayout();
        } catch (Exception e) {
            Log.e(TAG, "Error in ReportsActivity: " + e.getMessage(), e);
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
        
        TextView backBtn = new TextView(this);
        backBtn.setText("← Back");
        backBtn.setTextColor(Color.parseColor("#FFEDD5")); // Light orange
        backBtn.setTextSize(16);
        backBtn.setTypeface(null, Typeface.BOLD);
        backBtn.setPadding(0, dpToPx(10), dpToPx(20), dpToPx(10));
        backBtn.setOnClickListener(v -> finish());
        toolbar.addView(backBtn);
        
        TextView titleText = new TextView(this);
        titleText.setText("Analytics");
        titleText.setTextSize(24);
        titleText.setTextColor(COLOR_WHITE);
        titleText.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleText.setLayoutParams(titleParams);
        toolbar.addView(titleText);
        
        // Add Button inside Toolbar
        TextView addBtn = new TextView(this);
        addBtn.setText("+ REPORT");
        addBtn.setTextColor(COLOR_WHITE);
        addBtn.setTextSize(14);
        addBtn.setTypeface(null, Typeface.BOLD);
        addBtn.setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(10));
        GradientDrawable addBtnBg = new GradientDrawable();
        addBtnBg.setColor(Color.parseColor("#C2410C")); // Darker orange
        addBtnBg.setCornerRadius(dpToPx(100));
        addBtn.setBackground(addBtnBg);
        addBtn.setOnClickListener(v -> showReportForm());
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
        createReportForm(scrollContent);
        
        // List Container
        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        scrollContent.addView(listContainer);
        
        loadReports();
        
        scrollView.addView(scrollContent);
        root.addView(scrollView, scrollParams);
        
        setContentView(root);
    }

    private void loadReports() {
        listContainer.removeAllViews();
        List<Report> reports = dataManager.getAllReports();
        
        if (reports.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No reports available.\nTap + REPORT to generate one.");
            emptyText.setTextSize(16);
            emptyText.setTextColor(COLOR_TEXT_SECONDARY);
            emptyText.setGravity(Gravity.CENTER);
            emptyText.setPadding(dpToPx(20), dpToPx(60), dpToPx(20), dpToPx(20));
            listContainer.addView(emptyText);
            return;
        }
        
        for (Report report : reports) {
            listContainer.addView(createReportCard(report));
        }
    }
    
    private View createReportCard(Report report) {
        LinearLayout outerLayout = new LinearLayout(this);
        outerLayout.setPadding(0, dpToPx(6), 0, dpToPx(6));
        
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(20), dpToPx(24), dpToPx(20), dpToPx(20));
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_WHITE);
        bg.setCornerRadius(dpToPx(20));
        card.setBackground(bg);
        card.setElevation(dpToPx(6));
        
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        card.setLayoutParams(cardParams);
        
        // Header Row (Title & Type Chip)
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        
        TextView titleText = new TextView(this);
        titleText.setText(report.getTitle());
        titleText.setTextSize(18);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setTextColor(COLOR_TEXT_PRIMARY);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleText.setLayoutParams(titleParams);
        
        TextView typeChip = new TextView(this);
        typeChip.setText(report.getReportType());
        typeChip.setTextSize(12);
        typeChip.setTypeface(null, Typeface.BOLD);
        typeChip.setPadding(dpToPx(12), dpToPx(4), dpToPx(12), dpToPx(4));
        
        GradientDrawable typeBg = new GradientDrawable();
        typeBg.setCornerRadius(dpToPx(12));
        
        switch (report.getReportType()) {
            case "Sales":
                typeBg.setColor(Color.parseColor("#D1FAE5")); // Green
                typeChip.setTextColor(Color.parseColor("#059669"));
                break;
            case "Financial":
                typeBg.setColor(Color.parseColor("#DBEAFE")); // Blue
                typeChip.setTextColor(Color.parseColor("#2563EB"));
                break;
            case "Inventory":
                typeBg.setColor(Color.parseColor("#FEF3C7")); // Amber
                typeChip.setTextColor(Color.parseColor("#D97706"));
                break;
            default:
                typeBg.setColor(Color.parseColor("#F3F4F6"));
                typeChip.setTextColor(COLOR_TEXT_SECONDARY);
        }
        typeChip.setBackground(typeBg);
        
        headerRow.addView(titleText);
        headerRow.addView(typeChip);
        
        // Revenue + Orders Row
        LinearLayout statsRow = new LinearLayout(this);
        statsRow.setOrientation(LinearLayout.HORIZONTAL);
        statsRow.setPadding(0, dpToPx(16), 0, dpToPx(12));
        statsRow.setWeightSum(2f);
        
        statsRow.addView(createMiniStat("Revenue", "TZS " + String.format("%,.0f", report.getTotalRevenue()), Color.parseColor("#059669")));
        statsRow.addView(createMiniStat("Orders", String.valueOf(report.getTotalOrders()), COLOR_TEXT_PRIMARY));
        
        TextView periodText = new TextView(this);
        periodText.setText("Period: " + report.getPeriod());
        periodText.setTextSize(14);
        periodText.setTextColor(COLOR_TEXT_SECONDARY);
        
        // Action Buttons Row
        LinearLayout actionsRow = new LinearLayout(this);
        actionsRow.setOrientation(LinearLayout.HORIZONTAL);
        actionsRow.setGravity(Gravity.END);
        actionsRow.setPadding(0, dpToPx(12), 0, 0);
        
        TextView editBtn = createActionButton("Edit", Color.parseColor("#F97316"));
        editBtn.setOnClickListener(v -> editReport(report));
        
        TextView deleteBtn = createActionButton("Delete", Color.parseColor("#EF4444")); 
        deleteBtn.setOnClickListener(v -> deleteReport(report));
        
        actionsRow.addView(editBtn);
        actionsRow.addView(deleteBtn);
        
        card.addView(headerRow);
        card.addView(statsRow);
        card.addView(periodText);
        card.addView(actionsRow);
        
        outerLayout.addView(card);
        return outerLayout;
    }

    private View createMiniStat(String label, String value, int valueColor) {
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        
        TextView lbl = new TextView(this);
        lbl.setText(label);
        lbl.setTextSize(12);
        lbl.setTextColor(COLOR_TEXT_SECONDARY);
        
        TextView val = new TextView(this);
        val.setText(value);
        val.setTextSize(20);
        val.setTypeface(null, Typeface.BOLD);
        val.setTextColor(valueColor);
        
        col.addView(lbl);
        col.addView(val);
        return col;
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

    private void createReportForm(LinearLayout parent) {
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
        formTitle.setText("Report Rules");
        formTitle.setTextSize(20);
        formTitle.setTextColor(COLOR_TEXT_PRIMARY);
        formTitle.setTypeface(null, Typeface.BOLD);
        formTitle.setPadding(0, 0, 0, dpToPx(20));
        formLayout.addView(formTitle);
        
        addFormField(formLayout, "Report Title", "title");
        
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setWeightSum(2f);
        
        LinearLayout col1 = new LinearLayout(this);
        col1.setOrientation(LinearLayout.VERTICAL);
        col1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        col1.setPadding(0, 0, dpToPx(10), 0);
        addFormField(col1, "Type (Sales/Inv)", "reportType");
        
        LinearLayout col2 = new LinearLayout(this);
        col2.setOrientation(LinearLayout.VERTICAL);
        col2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        col2.setPadding(dpToPx(10), 0, 0, 0);
        addFormField(col2, "Period", "period");
        
        row.addView(col1);
        row.addView(col2);
        formLayout.addView(row);
        
        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.setWeightSum(2f);
        
        LinearLayout col3 = new LinearLayout(this);
        col3.setOrientation(LinearLayout.VERTICAL);
        col3.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        col3.setPadding(0, 0, dpToPx(10), 0);
        addFormField(col3, "Total Revenue", "totalRevenue");
        
        LinearLayout col4 = new LinearLayout(this);
        col4.setOrientation(LinearLayout.VERTICAL);
        col4.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        col4.setPadding(dpToPx(10), 0, 0, 0);
        addFormField(col4, "Total Orders", "totalOrders");
        
        row2.addView(col3);
        row2.addView(col4);
        formLayout.addView(row2);
        
        addFormField(formLayout, "Top Product", "topProduct");
        addFormField(formLayout, "Summary", "summary");
        addFormField(formLayout, "Details", "details");
        
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
        cancelBtn.setOnClickListener(v -> hideReportForm());
        buttonLayout.addView(cancelBtn);

        Button saveBtn = new Button(this);
        saveBtn.setText("Generate");
        GradientDrawable saveBg = new GradientDrawable();
        saveBg.setColor(COLOR_PRIMARY); // Orange
        saveBg.setCornerRadius(dpToPx(12));
        saveBtn.setBackground(saveBg);
        saveBtn.setTextColor(COLOR_WHITE);
        saveBtn.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
        LinearLayout.LayoutParams saveParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        saveBtn.setLayoutParams(saveParams);
        saveBtn.setOnClickListener(v -> saveReport());
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

    private void showReportForm() {
        editingReport = null;
        clearForm();
        formLayout.setVisibility(View.VISIBLE);
        ((ScrollView)formLayout.getParent().getParent()).smoothScrollTo(0, 0);
    }

    private void hideReportForm() {
        formLayout.setVisibility(View.GONE);
        editingReport = null;
    }

    private void editReport(Report report) {
        editingReport = report;
        
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
        ((ScrollView)formLayout.getParent().getParent()).smoothScrollTo(0, 0);
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
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            double revenue = revenueStr.isEmpty() ? 0.0 : Double.parseDouble(revenueStr);
            int orders = ordersStr.isEmpty() ? 0 : Integer.parseInt(ordersStr);
            
            if (editingReport != null) {
                editingReport.setTitle(title);
                editingReport.setReportType(type);
                editingReport.setPeriod(period);
                editingReport.setTotalRevenue(revenue);
                editingReport.setTotalOrders(orders);
                editingReport.setTopProduct(topProduct);
                editingReport.setSummary(summary);
                editingReport.setDetails(details);
                dataManager.updateReport(editingReport);
                Toast.makeText(this, "Report updated", Toast.LENGTH_SHORT).show();
            } else {
                Report newReport = new Report(title, type, period, revenue, orders, topProduct, summary, details);
                dataManager.addReport(newReport);
                Toast.makeText(this, "Report generated", Toast.LENGTH_SHORT).show();
            }
            
            hideReportForm();
            loadReports();
            
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid numbers", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteReport(Report report) {
        dataManager.deleteReport(report.getId());
        Toast.makeText(this, "Report deleted", Toast.LENGTH_SHORT).show();
        loadReports();
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()
        );
    }
}
