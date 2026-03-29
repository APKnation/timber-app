package com.timbertrade.app.dashboard;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.content.Context;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.timbertrade.app.auth.LoginActivity;
import android.content.SharedPreferences;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private final int COLOR_PRIMARY        = Color.parseColor("#059669");
    private final int COLOR_PRIMARY_DARK   = Color.parseColor("#047857");
    private final int COLOR_BG             = Color.parseColor("#F0FDF4");
    private final int COLOR_TEXT_PRIMARY   = Color.parseColor("#1F2937");
    private final int COLOR_TEXT_SECONDARY = Color.parseColor("#6B7280");
    private final int COLOR_WHITE          = Color.WHITE;

    // Persisted simple user info (in-memory for demo)
    private String userName  = "Atanasi Kafuka";
    private String userEmail = "atanasi@timberapp.com";
    private String userPhone = "+255 712 345 678";
    private String userBiz   = "Kafuka Timber Ltd.";

    private TextView profileNameTv;
    private TextView profileEmailTv;

    // In-memory payment methods list
    private static class PaymentMethod {
        String brand, number, detail, colorTop, colorBot;
        PaymentMethod(String brand, String number, String detail, String colorTop, String colorBot) {
            this.brand = brand; this.number = number; this.detail = detail;
            this.colorTop = colorTop; this.colorBot = colorBot;
        }
    }
    private final java.util.ArrayList<PaymentMethod> paymentMethods = new java.util.ArrayList<>();
    private LinearLayout paymentCardsContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            return buildLayout();
        } catch (Exception e) {
            Log.e(TAG, "Error rendering profile layout", e);
            return new FrameLayout(getContext());
        }
    }

    // ─── Root Layout ──────────────────────────────────────────────────────────
    private View buildLayout() {
        RelativeLayout root = new RelativeLayout(getContext());
        root.setBackgroundColor(COLOR_BG);

        // Hero gradient header
        View headerBg = new View(getContext());
        GradientDrawable gradientBg = new GradientDrawable(
                GradientDrawable.Orientation.BR_TL,
                new int[]{Color.parseColor("#065F46"), Color.parseColor("#059669"), Color.parseColor("#34D399")}
        );
        headerBg.setBackground(gradientBg);
        RelativeLayout.LayoutParams headerBgParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(260));
        headerBgParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(headerBg, headerBgParams);

        // Decorative circle
        View circle = new View(getContext());
        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        circleBg.setColor(Color.argb(25, 255, 255, 255));
        circle.setBackground(circleBg);
        RelativeLayout.LayoutParams cParams = new RelativeLayout.LayoutParams(dpToPx(200), dpToPx(200));
        cParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        cParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        cParams.setMargins(0, -dpToPx(50), -dpToPx(50), 0);
        root.addView(circle, cParams);

        // Toolbar title
        int toolbarId = View.generateViewId();
        LinearLayout toolbar = new LinearLayout(getContext());
        toolbar.setId(toolbarId);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setPadding(dpToPx(24), dpToPx(44), dpToPx(24), dpToPx(16));
        toolbar.setGravity(Gravity.CENTER_VERTICAL);

        TextView titleText = new TextView(getContext());
        titleText.setText("My Profile");
        titleText.setTextSize(26);
        titleText.setTextColor(COLOR_WHITE);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        toolbar.addView(titleText);

        RelativeLayout.LayoutParams toolbarParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        toolbarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(toolbar, toolbarParams);

        // Scrollable content
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setVerticalScrollBarEnabled(false);
        RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scrollParams.addRule(RelativeLayout.BELOW, toolbarId);

        LinearLayout scrollContent = new LinearLayout(getContext());
        scrollContent.setOrientation(LinearLayout.VERTICAL);
        scrollContent.setPadding(dpToPx(20), dpToPx(16), dpToPx(20), dpToPx(40));

        scrollContent.addView(createProfileInfoCard());

        scrollContent.addView(createSectionTitle("Account"));
        scrollContent.addView(createSettingsCard(new String[][]{
            {"Personal Details",  "Update name, email & phone", "P", "#4F46E5", "#EEF2FF"},
            {"Payment Methods",   "Manage cards & accounts",    "C", "#D97706", "#FEF3C7"},
            {"Notifications",     "Manage alerts & sounds",     "N", "#0891B2", "#E0F2FE"}
        }));

        scrollContent.addView(createSectionTitle("App Settings"));
        scrollContent.addView(createSettingsCard(new String[][]{
            {"Language",          "English (US)",               "L", "#7C3AED", "#F5F3FF"},
            {"Dark Mode",         "System Default",             "D", "#1F2937", "#F3F4F6"},
            {"Privacy & Security","Password and PIN",           "S", "#DC2626", "#FEE2E2"}
        }));

        scrollContent.addView(createLogoutButton());

        scrollView.addView(scrollContent);
        root.addView(scrollView, scrollParams);
        return root;
    }

    // ─── Profile Info Card ────────────────────────────────────────────────────
    private View createProfileInfoCard() {
        FrameLayout frame = new FrameLayout(getContext());
        frame.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(20), dpToPx(68), dpToPx(20), dpToPx(24));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_WHITE);
        bg.setCornerRadius(dpToPx(28));
        card.setBackground(bg);
        card.setElevation(dpToPx(10));

        FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, dpToPx(56), 0, 0);
        card.setLayoutParams(cardParams);

        profileNameTv = new TextView(getContext());
        profileNameTv.setText(userName);
        profileNameTv.setTextSize(22);
        profileNameTv.setTextColor(COLOR_TEXT_PRIMARY);
        profileNameTv.setTypeface(null, Typeface.BOLD);
        profileNameTv.setGravity(Gravity.CENTER);

        profileEmailTv = new TextView(getContext());
        profileEmailTv.setText(userEmail);
        profileEmailTv.setTextSize(14);
        profileEmailTv.setTextColor(COLOR_TEXT_SECONDARY);
        profileEmailTv.setGravity(Gravity.CENTER);
        profileEmailTv.setPadding(0, dpToPx(4), 0, dpToPx(4));

        // Premium badge
        TextView badge = new TextView(getContext());
        badge.setText("  ★ PREMIUM MEMBER  ");
        badge.setTextSize(11);
        badge.setTypeface(null, Typeface.BOLD);
        badge.setTextColor(Color.parseColor("#92400E"));
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(Color.parseColor("#FEF3C7"));
        badgeBg.setCornerRadius(dpToPx(100));
        badge.setBackground(badgeBg);
        badge.setPadding(dpToPx(8), dpToPx(5), dpToPx(8), dpToPx(5));
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        badgeParams.gravity = Gravity.CENTER_HORIZONTAL;
        badgeParams.topMargin = dpToPx(6);
        badge.setLayoutParams(badgeParams);

        // Separator
        View sep = new View(getContext());
        sep.setBackgroundColor(Color.parseColor("#F3F4F6"));
        LinearLayout.LayoutParams sepParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1));
        sepParams.setMargins(0, dpToPx(20), 0, dpToPx(16));
        sep.setLayoutParams(sepParams);

        // Stats row
        LinearLayout statsRow = new LinearLayout(getContext());
        statsRow.setOrientation(LinearLayout.HORIZONTAL);
        statsRow.setWeightSum(3f);
        statsRow.addView(createStatColumn("12",      "Orders",  "#059669"));
        statsRow.addView(createStatColDivider());
        statsRow.addView(createStatColumn("$45K",    "Revenue", "#D97706"));
        statsRow.addView(createStatColDivider());
        statsRow.addView(createStatColumn("Premium", "Tier",    "#7C3AED"));

        card.addView(profileNameTv);
        card.addView(profileEmailTv);
        card.addView(badge);
        card.addView(sep);
        card.addView(statsRow);
        frame.addView(card);

        // Floating avatar
        TextView avatar = new TextView(getContext());
        avatar.setText("AK");
        avatar.setTextColor(COLOR_WHITE);
        avatar.setTextSize(30);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setGravity(Gravity.CENTER);

        GradientDrawable avatarBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.parseColor("#059669"), Color.parseColor("#065F46")}
        );
        avatarBg.setShape(GradientDrawable.OVAL);
        avatarBg.setStroke(dpToPx(4), COLOR_WHITE);
        avatar.setBackground(avatarBg);
        avatar.setElevation(dpToPx(12));

        FrameLayout.LayoutParams avatarParams = new FrameLayout.LayoutParams(dpToPx(110), dpToPx(110));
        avatarParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        avatar.setLayoutParams(avatarParams);
        frame.addView(avatar);

        return frame;
    }

    private View createStatColumn(String value, String label, String color) {
        LinearLayout col = new LinearLayout(getContext());
        col.setOrientation(LinearLayout.VERTICAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        col.setGravity(Gravity.CENTER);

        TextView valTv = new TextView(getContext());
        valTv.setText(value);
        valTv.setTextSize(18);
        valTv.setTypeface(null, Typeface.BOLD);
        valTv.setTextColor(Color.parseColor(color));
        valTv.setGravity(Gravity.CENTER);

        TextView lblTv = new TextView(getContext());
        lblTv.setText(label);
        lblTv.setTextSize(11);
        lblTv.setTextColor(COLOR_TEXT_SECONDARY);
        lblTv.setGravity(Gravity.CENTER);

        col.addView(valTv);
        col.addView(lblTv);
        return col;
    }

    private View createStatColDivider() {
        View div = new View(getContext());
        div.setBackgroundColor(Color.parseColor("#E5E7EB"));
        div.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(1), dpToPx(32)));
        return div;
    }

    // ─── Section Title ────────────────────────────────────────────────────────
    private View createSectionTitle(String title) {
        TextView tv = new TextView(getContext());
        tv.setText(title.toUpperCase());
        tv.setTextSize(11);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(COLOR_TEXT_SECONDARY);
        tv.setLetterSpacing(0.12f);
        tv.setPadding(dpToPx(4), dpToPx(30), dpToPx(4), dpToPx(10));
        return tv;
    }

    // ─── Settings Card ────────────────────────────────────────────────────────
    private View createSettingsCard(String[][] items) {
        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_WHITE);
        bg.setCornerRadius(dpToPx(20));
        card.setBackground(bg);
        card.setElevation(dpToPx(5));
        card.setClipToOutline(true);

        for (int i = 0; i < items.length; i++) {
            boolean isLast = (i == items.length - 1);
            String[] item = items[i];
            card.addView(createSettingRow(item[0], item[1], item[2], item[3], item[4], isLast));
        }
        return card;
    }

    private View createSettingRow(String title, String subtitle, String iconLetter,
                                  String iconColor, String iconBgColor, boolean isLast) {
        LinearLayout rowWrapper = new LinearLayout(getContext());
        rowWrapper.setOrientation(LinearLayout.VERTICAL);

        TypedValue outValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        rowWrapper.setBackgroundResource(outValue.resourceId);
        rowWrapper.setClickable(true);
        rowWrapper.setOnClickListener(v -> openSheet(title));

        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dpToPx(20), dpToPx(16), dpToPx(20), dpToPx(16));
        row.setGravity(Gravity.CENTER_VERTICAL);

        // Icon badge
        TextView icon = new TextView(getContext());
        icon.setText(iconLetter);
        icon.setTextColor(Color.parseColor(iconColor));
        icon.setTextSize(15);
        icon.setTypeface(null, Typeface.BOLD);
        icon.setGravity(Gravity.CENTER);
        GradientDrawable icoB = new GradientDrawable();
        icoB.setCornerRadius(dpToPx(12));
        icoB.setColor(Color.parseColor(iconBgColor));
        icon.setBackground(icoB);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(42), dpToPx(42));
        iconParams.rightMargin = dpToPx(16);
        icon.setLayoutParams(iconParams);

        // Text
        LinearLayout textCol = new LinearLayout(getContext());
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView tv1 = new TextView(getContext());
        tv1.setText(title);
        tv1.setTextSize(15);
        tv1.setTextColor(COLOR_TEXT_PRIMARY);
        tv1.setTypeface(null, Typeface.BOLD);

        TextView tv2 = new TextView(getContext());
        tv2.setText(subtitle);
        tv2.setTextSize(12);
        tv2.setTextColor(COLOR_TEXT_SECONDARY);

        textCol.addView(tv1);
        textCol.addView(tv2);

        // Chevron
        TextView chevron = new TextView(getContext());
        chevron.setText("›");
        chevron.setTextSize(22);
        chevron.setTextColor(Color.parseColor("#D1D5DB"));
        chevron.setTypeface(null, Typeface.BOLD);

        row.addView(icon);
        row.addView(textCol);
        row.addView(chevron);
        rowWrapper.addView(row);

        if (!isLast) {
            View div = new View(getContext());
            div.setBackgroundColor(Color.parseColor("#F3F4F6"));
            LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1));
            dp.setMargins(dpToPx(78), 0, dpToPx(20), 0);
            div.setLayoutParams(dp);
            rowWrapper.addView(div);
        }
        return rowWrapper;
    }

    // ─── Sheet Router ─────────────────────────────────────────────────────────
    private void openSheet(String title) {
        switch (title) {
            case "Personal Details":  showPersonalDetailsSheet(); break;
            case "Payment Methods":   showPaymentMethodsSheet();  break;
            case "Notifications":     showNotificationsSheet();   break;
            case "Language":          showLanguageSheet();        break;
            case "Dark Mode":         showDarkModeSheet();        break;
            case "Privacy & Security":showSecuritySheet();        break;
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 1. PERSONAL DETAILS
    // ──────────────────────────────────────────────────────────────────────────
    private void showPersonalDetailsSheet() {
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        LinearLayout root = sheetRoot();

        sheetHandle(root);
        sheetTitle(root, "Personal Details");

        TextInputLayout tilName  = materialField(root, "Full Name",     userName);
        TextInputLayout tilEmail = materialField(root, "Email Address", userEmail);
        TextInputLayout tilPhone = materialField(root, "Phone Number",  userPhone);
        TextInputLayout tilBiz   = materialField(root, "Business Name", userBiz);

        MaterialButton btn = saveButton("Save Changes");
        btn.setOnClickListener(v -> {
            String n = text(tilName); String e = text(tilEmail);
            String p = text(tilPhone); String b = text(tilBiz);
            if (n.isEmpty() || e.isEmpty()) {
                Toast.makeText(getContext(), "Name and email are required", Toast.LENGTH_SHORT).show();
                return;
            }
            userName = n; userEmail = e; userPhone = p; userBiz = b;
            if (profileNameTv  != null) profileNameTv.setText(userName);
            if (profileEmailTv != null) profileEmailTv.setText(userEmail);
            sheet.dismiss();
            Toast.makeText(getContext(), "Profile updated ✓", Toast.LENGTH_SHORT).show();
        });
        root.addView(btn);
        sheet.setContentView(root);
        sheet.show();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 2. PAYMENT METHODS
    // ──────────────────────────────────────────────────────────────────────────
    private void showPaymentMethodsSheet() {
        // Seed defaults on first open
        if (paymentMethods.isEmpty()) {
            paymentMethods.add(new PaymentMethod("Visa",       "•••• •••• •••• 4242", "Expires 12/27", "#1A56DB", "#1E40AF"));
            paymentMethods.add(new PaymentMethod("Mastercard", "•••• •••• •••• 8891", "Expires 08/26", "#D97706", "#92400E"));
            paymentMethods.add(new PaymentMethod("M-Pesa",     "+255 712 345 678",    "Mobile Money",  "#059669", "#065F46"));
        }

        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());

        ScrollView sv = new ScrollView(requireContext());
        sv.setVerticalScrollBarEnabled(false);

        LinearLayout root = sheetRoot();
        sheetHandle(root);
        sheetTitle(root, "Payment Methods");

        // Dynamic container for cards
        paymentCardsContainer = new LinearLayout(requireContext());
        paymentCardsContainer.setOrientation(LinearLayout.VERTICAL);
        rebuildPaymentCards(paymentCardsContainer, sheet);
        root.addView(paymentCardsContainer);

        // Add button
        MaterialButton addBtn = new MaterialButton(requireContext());
        addBtn.setText("+ Add Payment Method");
        addBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F0FDF4")));
        addBtn.setTextColor(COLOR_PRIMARY);
        addBtn.setStrokeColor(ColorStateList.valueOf(COLOR_PRIMARY));
        addBtn.setStrokeWidth(dpToPx(1));
        addBtn.setCornerRadius(dpToPx(14));
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnLp.topMargin = dpToPx(8);
        addBtn.setLayoutParams(btnLp);
        addBtn.setOnClickListener(v -> addPaymentSubSheet(sheet));
        root.addView(addBtn);

        sv.addView(root);
        sheet.setContentView(sv);
        sheet.show();
    }

    private void rebuildPaymentCards(LinearLayout container, BottomSheetDialog parentSheet) {
        container.removeAllViews();
        for (int i = 0; i < paymentMethods.size(); i++) {
            PaymentMethod pm = paymentMethods.get(i);
            final int idx = i;
            container.addView(paymentCard(pm, () -> {
                paymentMethods.remove(idx);
                rebuildPaymentCards(container, parentSheet);
                Toast.makeText(getContext(), pm.brand + " removed", Toast.LENGTH_SHORT).show();
            }));
        }
    }

    /** Step 1 — choose type: Card or M-Pesa */
    private void addPaymentSubSheet(BottomSheetDialog parentSheet) {
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        LinearLayout root = sheetRoot();
        sheetHandle(root);
        sheetTitle(root, "Select Payment Type");

        root.addView(typeOptionRow("💳  Credit / Debit Card", "Visa, Mastercard, etc.", "#1A56DB", "#EEF2FF", v -> {
            sheet.dismiss();
            showAddCardForm(parentSheet);
        }));
        root.addView(sheetDivider());
        root.addView(typeOptionRow("📱  Mobile Money", "M-Pesa, Airtel Money, etc.", "#059669", "#F0FDF4", v -> {
            sheet.dismiss();
            showAddMpesaForm(parentSheet);
        }));

        sheet.setContentView(root);
        sheet.show();
    }

    private View typeOptionRow(String title, String subtitle, String iconColor,
                               String iconBg, View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dpToPx(4), dpToPx(16), dpToPx(4), dpToPx(16));
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setClickable(true);
        row.setOnClickListener(listener);
        TypedValue tv = new TypedValue();
        requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, tv, true);
        row.setBackgroundResource(tv.resourceId);

        LinearLayout textCol = new LinearLayout(requireContext());
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView t1 = new TextView(requireContext());
        t1.setText(title); t1.setTextSize(15); t1.setTypeface(null, Typeface.BOLD);
        t1.setTextColor(COLOR_TEXT_PRIMARY);
        TextView t2 = new TextView(requireContext());
        t2.setText(subtitle); t2.setTextSize(12); t2.setTextColor(COLOR_TEXT_SECONDARY);
        textCol.addView(t1); textCol.addView(t2);

        TextView chevron = new TextView(requireContext());
        chevron.setText("›"); chevron.setTextSize(22);
        chevron.setTextColor(Color.parseColor("#D1D5DB"));
        chevron.setTypeface(null, Typeface.BOLD);

        row.addView(textCol);
        row.addView(chevron);
        return row;
    }

    /** Step 2a — Card form */
    private void showAddCardForm(BottomSheetDialog parentSheet) {
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        LinearLayout root = sheetRoot();
        sheetHandle(root);
        sheetTitle(root, "Add Card");

        TextInputLayout tilName   = materialField(root, "Cardholder Name", "");
        TextInputLayout tilNumber = materialField(root, "Card Number (16 digits)", "");
        tilNumber.getEditText().setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        tilNumber.getEditText().setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(16)});

        // Expiry + CVV row
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowLp.bottomMargin = dpToPx(14);
        row.setLayoutParams(rowLp);

        TextInputLayout tilExpiry = buildInlineField("MM/YY");
        tilExpiry.getEditText().setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        tilExpiry.getEditText().setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(5)});
        LinearLayout.LayoutParams exLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        exLp.rightMargin = dpToPx(8);
        tilExpiry.setLayoutParams(exLp);

        TextInputLayout tilCvv = buildInlineField("CVV");
        tilCvv.getEditText().setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        tilCvv.getEditText().setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(4)});
        tilCvv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        row.addView(tilExpiry);
        row.addView(tilCvv);
        root.addView(row);

        MaterialButton btn = saveButton("Add Card");
        btn.setOnClickListener(v -> {
            String nameVal   = text(tilName);
            String numVal    = text(tilNumber);
            String expiryVal = text(tilExpiry);
            String cvvVal    = text(tilCvv);

            if (nameVal.isEmpty() || numVal.length() < 16 || expiryVal.length() < 5 || cvvVal.length() < 3) {
                Toast.makeText(getContext(), "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
                return;
            }
            String masked = "•••• •••• •••• " + numVal.substring(12);
            String brand  = detectCardBrand(numVal);
            String[] colors = cardColors(brand);

            paymentMethods.add(new PaymentMethod(brand, masked, "Expires " + expiryVal, colors[0], colors[1]));
            rebuildPaymentCards(paymentCardsContainer, parentSheet);

            sheet.dismiss();
            Toast.makeText(getContext(), brand + " card added ✓", Toast.LENGTH_SHORT).show();
        });
        root.addView(btn);
        sheet.setContentView(root);
        sheet.show();
    }

    /** Step 2b — M-Pesa / Mobile Money form */
    private void showAddMpesaForm(BottomSheetDialog parentSheet) {
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        LinearLayout root = sheetRoot();
        sheetHandle(root);
        sheetTitle(root, "Add Mobile Money");

        TextInputLayout tilPhone  = materialField(root, "Phone Number", "+255 ");
        tilPhone.getEditText().setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        TextInputLayout tilAlias  = materialField(root, "Account Nickname (optional)", "");

        // Provider selector label
        TextView provLabel = new TextView(requireContext());
        provLabel.setText("Provider");
        provLabel.setTextSize(13);
        provLabel.setTypeface(null, Typeface.BOLD);
        provLabel.setTextColor(COLOR_TEXT_SECONDARY);
        LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        plp.bottomMargin = dpToPx(8);
        provLabel.setLayoutParams(plp);
        root.addView(provLabel);

        // Provider chips row
        LinearLayout chipsRow = new LinearLayout(requireContext());
        chipsRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams crLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        crLp.bottomMargin = dpToPx(16);
        chipsRow.setLayoutParams(crLp);

        final String[] selectedProvider = {"M-Pesa"};
        String[] providers = {"M-Pesa", "Airtel", "Tigo", "Halotel"};
        final TextView[] chips = new TextView[providers.length];
        for (int i = 0; i < providers.length; i++) {
            final String prov = providers[i];
            TextView chip = new TextView(requireContext());
            chip.setText(prov);
            chip.setTextSize(12);
            chip.setTypeface(null, Typeface.BOLD);
            chip.setPadding(dpToPx(14), dpToPx(8), dpToPx(14), dpToPx(8));
            chip.setTextColor(i == 0 ? COLOR_WHITE : COLOR_TEXT_SECONDARY);
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cp.rightMargin = dpToPx(8);
            chip.setLayoutParams(cp);
            updateChipStyle(chip, i == 0);
            chips[i] = chip;
            final int idx = i;
            chip.setClickable(true);
            chip.setOnClickListener(cv -> {
                selectedProvider[0] = prov;
                for (int j = 0; j < chips.length; j++)
                    updateChipStyle(chips[j], j == idx);
            });
            chipsRow.addView(chip);
        }
        root.addView(chipsRow);

        MaterialButton btn = saveButton("Add Mobile Money");
        btn.setOnClickListener(v -> {
            String phone = text(tilPhone);
            String alias = text(tilAlias);
            if (phone.length() < 8) {
                Toast.makeText(getContext(), "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            String label = alias.isEmpty() ? selectedProvider[0] : alias;
            paymentMethods.add(new PaymentMethod(
                    selectedProvider[0], phone,
                    "Mobile Money · " + label,
                    "#059669", "#065F46"));
            rebuildPaymentCards(paymentCardsContainer, parentSheet);
            sheet.dismiss();
            Toast.makeText(getContext(), selectedProvider[0] + " account added ✓", Toast.LENGTH_SHORT).show();
        });
        root.addView(btn);
        sheet.setContentView(root);
        sheet.show();
    }

    private void updateChipStyle(TextView chip, boolean active) {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dpToPx(100));
        bg.setColor(active ? COLOR_PRIMARY : Color.parseColor("#F3F4F6"));
        chip.setBackground(bg);
        chip.setTextColor(active ? COLOR_WHITE : COLOR_TEXT_SECONDARY);
    }

    private TextInputLayout buildInlineField(String hint) {
        TextInputLayout til = new TextInputLayout(requireContext(), null,
                com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
        til.setHint(hint);
        float r = dpToPx(14);
        til.setBoxCornerRadii(r, r, r, r);
        til.setBoxStrokeColor(COLOR_PRIMARY);
        til.setHintTextColor(ColorStateList.valueOf(COLOR_PRIMARY));
        TextInputEditText et = new TextInputEditText(til.getContext());
        et.setTextSize(14);
        et.setTextColor(COLOR_TEXT_PRIMARY);
        til.addView(et);
        return til;
    }

    private String detectCardBrand(String number) {
        if (number.startsWith("4"))                          return "Visa";
        if (number.startsWith("5") || number.startsWith("2")) return "Mastercard";
        if (number.startsWith("3"))                          return "Amex";
        return "Card";
    }

    private String[] cardColors(String brand) {
        switch (brand) {
            case "Visa":       return new String[]{"#1A56DB", "#1E40AF"};
            case "Mastercard": return new String[]{"#D97706", "#92400E"};
            case "Amex":       return new String[]{"#6D28D9", "#4C1D95"};
            default:           return new String[]{"#374151", "#1F2937"};
        }
    }

    private View paymentCard(PaymentMethod pm, Runnable onRemove) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(20), dpToPx(18), dpToPx(20), dpToPx(18));

        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{Color.parseColor(pm.colorTop), Color.parseColor(pm.colorBot)});
        bg.setCornerRadius(dpToPx(18));
        card.setBackground(bg);
        card.setElevation(dpToPx(4));

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = dpToPx(12);
        card.setLayoutParams(p);

        TextView brandTv = new TextView(requireContext());
        brandTv.setText(pm.brand);
        brandTv.setTextSize(14);
        brandTv.setTypeface(null, Typeface.BOLD);
        brandTv.setTextColor(Color.argb(180, 255, 255, 255));

        TextView numTv = new TextView(requireContext());
        numTv.setText(pm.number);
        numTv.setTextSize(18);
        numTv.setTypeface(null, Typeface.BOLD);
        numTv.setTextColor(COLOR_WHITE);
        LinearLayout.LayoutParams nlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nlp.topMargin = dpToPx(8);
        numTv.setLayoutParams(nlp);

        LinearLayout bottom = new LinearLayout(requireContext());
        bottom.setOrientation(LinearLayout.HORIZONTAL);
        bottom.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        blp.topMargin = dpToPx(10);
        bottom.setLayoutParams(blp);

        TextView detailTv = new TextView(requireContext());
        detailTv.setText(pm.detail);
        detailTv.setTextSize(12);
        detailTv.setTextColor(Color.argb(180, 255, 255, 255));
        detailTv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView removeTv = new TextView(requireContext());
        removeTv.setText("Remove");
        removeTv.setTextSize(12);
        removeTv.setTextColor(Color.argb(220, 255, 180, 180));
        removeTv.setTypeface(null, Typeface.BOLD);
        removeTv.setClickable(true);
        removeTv.setOnClickListener(v -> onRemove.run());

        bottom.addView(detailTv);
        bottom.addView(removeTv);

        card.addView(brandTv);
        card.addView(numTv);
        card.addView(bottom);
        return card;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 3. NOTIFICATIONS
    // ──────────────────────────────────────────────────────────────────────────
    private void showNotificationsSheet() {
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        LinearLayout root = sheetRoot();
        sheetHandle(root);
        sheetTitle(root, "Notifications");

        root.addView(toggleRow("Order Updates",     "Get notified when orders change",   true));
        root.addView(sheetDivider());
        root.addView(toggleRow("Payment Alerts",    "Payments received or pending",      true));
        root.addView(sheetDivider());
        root.addView(toggleRow("Market Activity",   "New listings and price changes",    false));
        root.addView(sheetDivider());
        root.addView(toggleRow("Promotions & Deals","Special offers and discounts",      false));
        root.addView(sheetDivider());
        root.addView(toggleRow("App Sounds",        "Play sounds for notifications",     true));

        MaterialButton done = saveButton("Done");
        done.setOnClickListener(v -> {
            sheet.dismiss();
            Toast.makeText(getContext(), "Notification preferences saved ✓", Toast.LENGTH_SHORT).show();
        });
        root.addView(done);
        sheet.setContentView(root);
        sheet.show();
    }

    private View toggleRow(String title, String subtitle, boolean defaultOn) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dpToPx(4), dpToPx(12), dpToPx(4), dpToPx(12));
        row.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout textCol = new LinearLayout(requireContext());
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView ttv = new TextView(requireContext());
        ttv.setText(title);
        ttv.setTextSize(15);
        ttv.setTextColor(COLOR_TEXT_PRIMARY);
        ttv.setTypeface(null, Typeface.BOLD);

        TextView stv = new TextView(requireContext());
        stv.setText(subtitle);
        stv.setTextSize(12);
        stv.setTextColor(COLOR_TEXT_SECONDARY);

        textCol.addView(ttv);
        textCol.addView(stv);

        Switch sw = new Switch(requireContext());
        sw.setChecked(defaultOn);
        sw.setThumbTintList(ColorStateList.valueOf(defaultOn ? COLOR_PRIMARY : Color.parseColor("#9CA3AF")));
        sw.setOnCheckedChangeListener((btn, checked) ->
            sw.setThumbTintList(ColorStateList.valueOf(checked ? COLOR_PRIMARY : Color.parseColor("#9CA3AF"))));

        row.addView(textCol);
        row.addView(sw);
        return row;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 4. LANGUAGE
    // ──────────────────────────────────────────────────────────────────────────
    private void showLanguageSheet() {
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        LinearLayout root = sheetRoot();
        sheetHandle(root);
        sheetTitle(root, "Select Language");

        String[][] langs = {
            {"English (US)", "en", "true"},
            {"Swahili", "sw", "false"},
            {"French", "fr", "false"},
            {"Arabic", "ar", "false"},
            {"Portuguese", "pt", "false"}
        };

        for (String[] lang : langs) {
            root.addView(langRow(lang[0], lang[2].equals("true"), sheet));
        }
        sheet.setContentView(root);
        sheet.show();
    }

    private View langRow(String language, boolean selected, BottomSheetDialog sheet) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dpToPx(4), dpToPx(16), dpToPx(4), dpToPx(16));
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setClickable(true);
        row.setOnClickListener(v -> {
            sheet.dismiss();
            Toast.makeText(getContext(), language + " selected ✓", Toast.LENGTH_SHORT).show();
        });

        TextView langTv = new TextView(requireContext());
        langTv.setText(language);
        langTv.setTextSize(15);
        langTv.setTextColor(COLOR_TEXT_PRIMARY);
        if (selected) langTv.setTypeface(null, Typeface.BOLD);
        langTv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        if (selected) {
            TextView check = new TextView(requireContext());
            check.setText("✓");
            check.setTextSize(18);
            check.setTextColor(COLOR_PRIMARY);
            check.setTypeface(null, Typeface.BOLD);
            row.addView(langTv);
            row.addView(check);
        } else {
            row.addView(langTv);
        }
        return row;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 5. DARK MODE
    // ──────────────────────────────────────────────────────────────────────────
    private void showDarkModeSheet() {
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        LinearLayout root = sheetRoot();
        sheetHandle(root);
        sheetTitle(root, "Dark Mode");

        String[][] modes = {{"Light", "☀", "false"}, {"Dark", "🌙", "false"}, {"System Default", "⚙", "true"}};
        for (String[] mode : modes) {
            root.addView(darkModeRow(mode[0], mode[1], mode[2].equals("true"), sheet));
            root.addView(sheetDivider());
        }
        sheet.setContentView(root);
        sheet.show();
    }

    private View darkModeRow(String label, String emoji, boolean selected, BottomSheetDialog sheet) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dpToPx(4), dpToPx(14), dpToPx(4), dpToPx(14));
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setClickable(true);
        row.setOnClickListener(v -> {
            sheet.dismiss();
            Toast.makeText(getContext(), label + " mode selected ✓", Toast.LENGTH_SHORT).show();
        });

        TextView emojiTv = new TextView(requireContext());
        emojiTv.setText(emoji);
        emojiTv.setTextSize(20);
        LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(dpToPx(36), ViewGroup.LayoutParams.WRAP_CONTENT);
        emojiTv.setLayoutParams(ep);

        TextView labelTv = new TextView(requireContext());
        labelTv.setText(label);
        labelTv.setTextSize(15);
        labelTv.setTextColor(COLOR_TEXT_PRIMARY);
        if (selected) labelTv.setTypeface(null, Typeface.BOLD);
        labelTv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        row.addView(emojiTv);
        row.addView(labelTv);
        if (selected) {
            TextView check = new TextView(requireContext());
            check.setText("✓");
            check.setTextSize(18);
            check.setTextColor(COLOR_PRIMARY);
            check.setTypeface(null, Typeface.BOLD);
            row.addView(check);
        }
        return row;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 6. PRIVACY & SECURITY
    // ──────────────────────────────────────────────────────────────────────────
    private void showSecuritySheet() {
        BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
        LinearLayout root = sheetRoot();
        sheetHandle(root);
        sheetTitle(root, "Privacy & Security");

        TextInputLayout tilCurrent = materialField(root, "Current Password", "");
        tilCurrent.getEditText().setInputType(
                android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tilCurrent.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

        TextInputLayout tilNew = materialField(root, "New Password", "");
        tilNew.getEditText().setInputType(
                android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tilNew.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

        TextInputLayout tilConfirm = materialField(root, "Confirm New Password", "");
        tilConfirm.getEditText().setInputType(
                android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tilConfirm.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

        // PIN toggle
        root.addView(sheetDivider());
        root.addView(toggleRow("Biometric / PIN Lock",
                "Require authentication on app open", false));

        MaterialButton btn = saveButton("Update Password");
        btn.setOnClickListener(v -> {
            String cur = text(tilCurrent); String nw = text(tilNew); String conf = text(tilConfirm);
            if (cur.isEmpty() || nw.isEmpty() || conf.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show(); return;
            }
            if (!nw.equals(conf)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show(); return;
            }
            if (nw.length() < 6) {
                Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show(); return;
            }
            sheet.dismiss();
            Toast.makeText(getContext(), "Password updated ✓", Toast.LENGTH_SHORT).show();
        });
        root.addView(btn);
        sheet.setContentView(root);
        sheet.show();
    }

    // ─── Logout ───────────────────────────────────────────────────────────────
    private View createLogoutButton() {
        MaterialButton logoutBtn = new MaterialButton(requireContext());
        logoutBtn.setText("Sign Out");
        logoutBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FEE2E2")));
        logoutBtn.setTextColor(Color.parseColor("#DC2626"));
        logoutBtn.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#FECACA")));
        logoutBtn.setStrokeWidth(dpToPx(1));
        logoutBtn.setCornerRadius(dpToPx(16));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dpToPx(36), 0, dpToPx(20));
        logoutBtn.setLayoutParams(params);

        logoutBtn.setOnClickListener(v -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("TimberTradePrefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
        return logoutBtn;
    }

    // ─── Sheet Helpers ────────────────────────────────────────────────────────
    private LinearLayout sheetRoot() {
        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dpToPx(24), dpToPx(28), dpToPx(24), dpToPx(36));
        root.setBackgroundColor(COLOR_WHITE);
        return root;
    }

    private void sheetHandle(LinearLayout root) {
        View handle = new View(requireContext());
        GradientDrawable hBg = new GradientDrawable();
        hBg.setColor(Color.parseColor("#D1FAE5"));
        hBg.setCornerRadius(dpToPx(100));
        handle.setBackground(hBg);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(dpToPx(40), dpToPx(4));
        hp.gravity = Gravity.CENTER_HORIZONTAL;
        hp.bottomMargin = dpToPx(20);
        root.addView(handle, hp);
    }

    private void sheetTitle(LinearLayout root, String title) {
        TextView tv = new TextView(requireContext());
        tv.setText(title);
        tv.setTextSize(22);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(COLOR_TEXT_PRIMARY);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dpToPx(20);
        tv.setLayoutParams(lp);
        root.addView(tv);
    }

    private TextInputLayout materialField(LinearLayout root, String hint, String value) {
        TextInputLayout til = new TextInputLayout(requireContext(), null,
                com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
        til.setHint(hint);
        float r = dpToPx(14);
        til.setBoxCornerRadii(r, r, r, r);
        til.setBoxStrokeColor(COLOR_PRIMARY);
        til.setHintTextColor(ColorStateList.valueOf(COLOR_PRIMARY));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dpToPx(14);
        til.setLayoutParams(lp);

        TextInputEditText et = new TextInputEditText(til.getContext());
        et.setText(value);
        et.setTextSize(14);
        et.setTextColor(COLOR_TEXT_PRIMARY);
        til.addView(et);
        root.addView(til);
        return til;
    }

    private MaterialButton saveButton(String label) {
        MaterialButton btn = new MaterialButton(requireContext());
        btn.setText(label);
        btn.setBackgroundTintList(ColorStateList.valueOf(COLOR_PRIMARY));
        btn.setTextColor(COLOR_WHITE);
        btn.setCornerRadius(dpToPx(16));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dpToPx(8);
        btn.setLayoutParams(lp);
        return btn;
    }

    private View sheetDivider() {
        View div = new View(requireContext());
        div.setBackgroundColor(Color.parseColor("#F3F4F6"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1));
        lp.setMargins(0, dpToPx(4), 0, dpToPx(4));
        div.setLayoutParams(lp);
        return div;
    }

    private String text(TextInputLayout til) {
        return til.getEditText() != null ? til.getEditText().getText().toString().trim() : "";
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                requireContext().getResources().getDisplayMetrics());
    }
}
