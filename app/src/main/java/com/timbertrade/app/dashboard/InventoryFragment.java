package com.timbertrade.app.dashboard;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class InventoryFragment extends Fragment {

    private final int COLOR_PRIMARY = Color.parseColor("#059669");
    private final int COLOR_BG = Color.parseColor("#F3F4F6");
    private final int COLOR_TEXT_PRIMARY = Color.parseColor("#111827");
    private final int COLOR_TEXT_SECONDARY = Color.parseColor("#4B5563");
    private final int COLOR_WHITE = Color.WHITE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout root = new RelativeLayout(requireContext());
        root.setBackgroundColor(COLOR_BG);

        // Header Background
        View headerBg = new View(requireContext());
        GradientDrawable gradientBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.parseColor("#111827"), Color.parseColor("#374151")}
        );
        headerBg.setBackground(gradientBg);
        RelativeLayout.LayoutParams headerParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(240));
        root.addView(headerBg, headerParams);

        // Toolbar
        int toolbarId = View.generateViewId();
        LinearLayout toolbar = new LinearLayout(requireContext());
        toolbar.setId(toolbarId);
        toolbar.setPadding(dpToPx(24), dpToPx(56), dpToPx(24), dpToPx(24));
        toolbar.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = new TextView(requireContext());
        title.setText("Inventory");
        title.setTextSize(26);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(COLOR_WHITE);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        
        TextView addBtn = new TextView(requireContext());
        addBtn.setText("+ Add Stock");
        addBtn.setTextColor(Color.parseColor("#34D399"));
        addBtn.setTypeface(null, Typeface.BOLD);
        addBtn.setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));
        addBtn.setClickable(true);
        addBtn.setOnClickListener(v -> showAddStockSheet());

        toolbar.addView(title);
        toolbar.addView(addBtn);
        root.addView(toolbar, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Scroll Content
        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.setVerticalScrollBarEnabled(false);
        RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollParams.addRule(RelativeLayout.BELOW, toolbarId);
        
        LinearLayout scrollContent = new LinearLayout(requireContext());
        scrollContent.setOrientation(LinearLayout.VERTICAL);
        scrollContent.setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(40));

        // Summary Cards
        scrollContent.addView(createSummaryRow());

        // Inventory List
        scrollContent.addView(createSectionTitle("Current Stock Levels"));
        scrollContent.addView(createInventoryItem("Oak Timber", "Grade A · Hardwood", 85, 120, "#10B981"));
        scrollContent.addView(createInventoryItem("Pine Logs", "Construction Grade", 42, 200, "#F59E0B"));
        scrollContent.addView(createInventoryItem("Teak Planks", "Furniture Grade", 15, 50, "#EF4444"));
        scrollContent.addView(createInventoryItem("Plywood Sheets", "Industrial Use", 150, 300, "#3B82F6"));
        scrollContent.addView(createInventoryItem("Mahogany Beams", "Premium structural", 28, 40, "#8B5CF6"));

        scrollView.addView(scrollContent);
        root.addView(scrollView, scrollParams);

        return root;
    }

    private View createSummaryRow() {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setWeightSum(2f);
        row.setPadding(0, dpToPx(10), 0, dpToPx(24));

        row.addView(createMiniCard("Total Items", "1,240", "#ECFDF5", "#059669"));
        row.addView(createMiniCard("Low Stock", "3 Categories", "#FEF2F2", "#DC2626"));

        return row;
    }

    private View createMiniCard(String label, String value, String bgColor, String textColor) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor(bgColor));
        bg.setCornerRadius(dpToPx(20));
        card.setBackground(bg);
        
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(dpToPx(6), 0, dpToPx(6), 0);
        card.setLayoutParams(lp);

        TextView labelTv = new TextView(requireContext());
        labelTv.setText(label);
        labelTv.setTextSize(12);
        labelTv.setTextColor(Color.parseColor(textColor));
        labelTv.setTypeface(null, Typeface.BOLD);

        TextView valTv = new TextView(requireContext());
        valTv.setText(value);
        valTv.setTextSize(24);
        valTv.setTypeface(null, Typeface.BOLD);
        valTv.setTextColor(Color.parseColor(textColor));
        valTv.setPadding(0, dpToPx(4), 0, 0);

        card.addView(labelTv);
        card.addView(valTv);
        return card;
    }

    private View createSectionTitle(String title) {
        TextView tv = new TextView(requireContext());
        tv.setText(title);
        tv.setTextSize(18);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(COLOR_TEXT_PRIMARY);
        tv.setPadding(0, dpToPx(10), 0, dpToPx(16));
        return tv;
    }

    private View createInventoryItem(String name, String desc, int current, int max, String color) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_WHITE);
        bg.setCornerRadius(dpToPx(20));
        card.setBackground(bg);
        card.setElevation(dpToPx(4));

        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.bottomMargin = dpToPx(16);
        card.setLayoutParams(cardLp);

        // Header: Name + Percent
        LinearLayout header = new LinearLayout(requireContext());
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout textCol = new LinearLayout(requireContext());
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView nameTv = new TextView(requireContext());
        nameTv.setText(name);
        nameTv.setTextSize(16);
        nameTv.setTypeface(null, Typeface.BOLD);
        nameTv.setTextColor(COLOR_TEXT_PRIMARY);

        TextView descTv = new TextView(requireContext());
        descTv.setText(desc);
        descTv.setTextSize(12);
        descTv.setTextColor(COLOR_TEXT_SECONDARY);
        
        textCol.addView(nameTv);
        textCol.addView(descTv);

        TextView percentTv = new TextView(requireContext());
        int percent = (int) ((current / (float) max) * 100);
        percentTv.setText(percent + "%");
        percentTv.setTextSize(14);
        percentTv.setTypeface(null, Typeface.BOLD);
        percentTv.setTextColor(Color.parseColor(color));

        header.addView(textCol);
        header.addView(percentTv);
        card.addView(header);

        // Progress Bar
        ProgressBar pb = new ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal);
        pb.setMax(max);
        pb.setProgress(current);
        pb.setProgressTintList(ColorStateList.valueOf(Color.parseColor(color)));
        pb.setProgressBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F3F4F6")));
        
        LinearLayout.LayoutParams pbLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(10));
        pbLp.topMargin = dpToPx(16);
        pbLp.bottomMargin = dpToPx(12);
        pb.setLayoutParams(pbLp);
        card.addView(pb);

        // Footer: Current / Max
        TextView footerTv = new TextView(requireContext());
        footerTv.setText(current + " / " + max + " units available");
        footerTv.setTextSize(12);
        footerTv.setTextColor(COLOR_TEXT_SECONDARY);
        footerTv.setGravity(Gravity.RIGHT);
        card.addView(footerTv);

        return card;
    }

    private void showAddStockSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dpToPx(24), dpToPx(32), dpToPx(24), dpToPx(32));
        root.setBackgroundColor(COLOR_WHITE);

        TextView title = new TextView(requireContext());
        title.setText("Add New Stock Item");
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(COLOR_TEXT_PRIMARY);
        title.setPadding(0, 0, 0, dpToPx(24));
        root.addView(title);

        TextInputLayout tilName = createField("Item Name (e.g. Pine Timber)");
        TextInputLayout tilCat = createField("Category (e.g. Softwood)");
        TextInputLayout tilQty = createField("Current Quantity");
        tilQty.getEditText().setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        TextInputLayout tilMax = createField("Warehouse Capacity");
        tilMax.getEditText().setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        root.addView(tilName);
        root.addView(tilCat);
        root.addView(tilQty);
        root.addView(tilMax);

        MaterialButton btn = new MaterialButton(requireContext());
        btn.setText("Save to Inventory");
        btn.setBackgroundTintList(ColorStateList.valueOf(COLOR_PRIMARY));
        btn.setTextColor(COLOR_WHITE);
        btn.setCornerRadius(dpToPx(16));
        btn.setPadding(0, dpToPx(16), 0, dpToPx(16));
        
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLp.topMargin = dpToPx(16);
        btn.setLayoutParams(btnLp);
        btn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Inventory item added! 📦", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        root.addView(btn);

        dialog.setContentView(root);
        dialog.show();
    }

    private TextInputLayout createField(String hint) {
        TextInputLayout til = new TextInputLayout(requireContext(), null, com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
        til.setHint(hint);
        float r = (float) dpToPx(14);
        til.setBoxCornerRadii(r, r, r, r);
        til.setBoxStrokeColor(COLOR_PRIMARY);
        til.setHintTextColor(ColorStateList.valueOf(COLOR_PRIMARY));
        
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = dpToPx(14);
        til.setLayoutParams(p);

        TextInputEditText et = new TextInputEditText(til.getContext());
        et.setTextSize(14);
        til.addView(et);
        return til;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, requireContext().getResources().getDisplayMetrics());
    }
}
