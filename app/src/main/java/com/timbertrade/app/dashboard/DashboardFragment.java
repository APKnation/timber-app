package com.timbertrade.app.dashboard;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.timbertrade.app.dashboard.RealDashboardActivity;
import com.timbertrade.app.auth.LoginActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

public class DashboardFragment extends Fragment {

    // Core palette
    private final int COLOR_PRIMARY       = Color.parseColor("#059669");
    private final int COLOR_PRIMARY_DARK  = Color.parseColor("#047857");
    private final int COLOR_ACCENT        = Color.parseColor("#D97706");
    private final int COLOR_BG            = Color.parseColor("#F0FDF4");
    private final int COLOR_TEXT_PRIMARY  = Color.parseColor("#1F2937");
    private final int COLOR_TEXT_SECONDARY= Color.parseColor("#6B7280");
    private final int COLOR_WHITE         = Color.WHITE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout root = new RelativeLayout(requireContext());
        root.setBackgroundColor(COLOR_BG);

        // Deep gradient hero header (taller, more vibrant)
        View headerBg = new View(requireContext());
        GradientDrawable gradientBg = new GradientDrawable(
                GradientDrawable.Orientation.BR_TL,
                new int[]{Color.parseColor("#065F46"), Color.parseColor("#059669"), Color.parseColor("#34D399")}
        );
        headerBg.setBackground(gradientBg);
        RelativeLayout.LayoutParams headerBgParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(300)
        );
        headerBgParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(headerBg, headerBgParams);

        // Decorative circle overlay top-right
        View circle1 = new View(requireContext());
        GradientDrawable circleBg1 = new GradientDrawable();
        circleBg1.setShape(GradientDrawable.OVAL);
        circleBg1.setColor(Color.argb(30, 255, 255, 255));
        circle1.setBackground(circleBg1);
        RelativeLayout.LayoutParams c1Params = new RelativeLayout.LayoutParams(dpToPx(180), dpToPx(180));
        c1Params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        c1Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        c1Params.setMargins(0, -dpToPx(40), -dpToPx(40), 0);
        root.addView(circle1, c1Params);

        // Decorative circle overlay bottom-left
        View circle2 = new View(requireContext());
        GradientDrawable circleBg2 = new GradientDrawable();
        circleBg2.setShape(GradientDrawable.OVAL);
        circleBg2.setColor(Color.argb(20, 255, 255, 255));
        circle2.setBackground(circleBg2);
        RelativeLayout.LayoutParams c2Params = new RelativeLayout.LayoutParams(dpToPx(120), dpToPx(120));
        c2Params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        c2Params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        c2Params.setMargins(dpToPx(30), dpToPx(120), 0, 0);
        root.addView(circle2, c2Params);

        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.setVerticalScrollBarEnabled(false);
        RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );

        LinearLayout scrollContent = new LinearLayout(requireContext());
        scrollContent.setOrientation(LinearLayout.VERTICAL);
        scrollContent.setPadding(0, 0, 0, dpToPx(30));

        scrollContent.addView(createGreetingSection());
        scrollContent.addView(createStatsRow());
        scrollContent.addView(createOverviewCard());
        scrollContent.addView(createServicesSection());
        scrollContent.addView(createActivitySection());

        scrollView.addView(scrollContent);
        root.addView(scrollView, scrollParams);

        return root;
    }

    // ─── Greeting ─────────────────────────────────────────────────────────────
    private View createGreetingSection() {
        LinearLayout headerLayout = new LinearLayout(requireContext());
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setPadding(dpToPx(24), dpToPx(48), dpToPx(24), dpToPx(16));
        headerLayout.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout textLayout = new LinearLayout(requireContext());
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView greeting = new TextView(requireContext());
        greeting.setText("Good day, Trader 👋");
        greeting.setTextSize(14);
        greeting.setTextColor(Color.parseColor("#A7F3D0"));

        TextView subTitle = new TextView(requireContext());
        subTitle.setText("Timber Dashboard");
        subTitle.setTextSize(28);
        subTitle.setTypeface(null, Typeface.BOLD);
        subTitle.setTextColor(COLOR_WHITE);

        textLayout.addView(greeting);
        textLayout.addView(subTitle);

        // Avatar button
        TextView avatar = new TextView(requireContext());
        avatar.setText("APK");
        avatar.setTextColor(COLOR_PRIMARY);
        avatar.setTextSize(13);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setGravity(Gravity.CENTER);

        GradientDrawable avatarBg = new GradientDrawable();
        avatarBg.setShape(GradientDrawable.OVAL);
        avatarBg.setColor(COLOR_WHITE);
        avatarBg.setStroke(dpToPx(2), Color.parseColor("#A7F3D0"));
        avatar.setBackground(avatarBg);

        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(dpToPx(52), dpToPx(52));
        avatar.setLayoutParams(avatarParams);
        avatar.setElevation(dpToPx(4));
        avatar.setClickable(true);
        avatar.setOnClickListener(v -> showLogoutMenu(v));

        headerLayout.addView(textLayout);
        headerLayout.addView(avatar);
        return headerLayout;
    }

    // ─── Quick Stats Row (3 mini-cards) ──────────────────────────────────────
    private View createStatsRow() {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));
        row.setWeightSum(3f);

        row.addView(createStatCard("$45.2K",  "Revenue",      "#10B981", "#065F46"));
        row.addView(createStatCard("12",       "Orders",       "#F59E0B", "#92400E"));
        row.addView(createStatCard("94%",      "Satisfaction", "#6366F1", "#3730A3"));

        return row;
    }

    private View createStatCard(String value, String label, String colorTop, String colorBottom) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(dpToPx(12), dpToPx(20), dpToPx(12), dpToPx(20));

        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor(colorTop), Color.parseColor(colorBottom)}
        );
        bg.setCornerRadius(dpToPx(20));
        card.setBackground(bg);
        card.setElevation(dpToPx(6));

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        p.setMargins(dpToPx(6), 0, dpToPx(6), 0);
        card.setLayoutParams(p);

        TextView valText = new TextView(requireContext());
        valText.setText(value);
        valText.setTextSize(22);
        valText.setTypeface(null, Typeface.BOLD);
        valText.setTextColor(COLOR_WHITE);
        valText.setGravity(Gravity.CENTER);

        TextView lblText = new TextView(requireContext());
        lblText.setText(label);
        lblText.setTextSize(11);
        lblText.setTextColor(Color.argb(200, 255, 255, 255));
        lblText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dpToPx(4);
        lblText.setLayoutParams(lp);

        card.addView(valText);
        card.addView(lblText);
        return card;
    }

    // ─── Overview Card ────────────────────────────────────────────────────────
    private View createOverviewCard() {
        LinearLayout wrapper = new LinearLayout(requireContext());
        wrapper.setPadding(dpToPx(20), dpToPx(8), dpToPx(20), dpToPx(8));

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.parseColor("#1F2937"), Color.parseColor("#111827")}
        );
        bg.setCornerRadius(dpToPx(24));
        card.setBackground(bg);
        card.setElevation(dpToPx(12));
        card.setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(24));
        card.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Label
        TextView cardLabel = new TextView(requireContext());
        cardLabel.setText("BUSINESS OVERVIEW");
        cardLabel.setTextSize(10);
        cardLabel.setTypeface(null, Typeface.BOLD);
        cardLabel.setTextColor(Color.parseColor("#6EE7B7"));
        cardLabel.setLetterSpacing(0.15f);
        card.addView(cardLabel);

        // Top row: revenue + orders
        LinearLayout topRow = new LinearLayout(requireContext());
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setWeightSum(2f);
        LinearLayout.LayoutParams topRowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        topRowParams.topMargin = dpToPx(16);
        topRow.setLayoutParams(topRowParams);

        topRow.addView(createOverviewMetric("$45,230", "Total Revenue", "#34D399"));
        topRow.addView(createOverviewMetric("12 Active", "Live Orders", "#FBBF24"));

        card.addView(topRow);

        // Separator
        View sep = new View(requireContext());
        sep.setBackgroundColor(Color.parseColor("#374151"));
        LinearLayout.LayoutParams sepParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1));
        sepParams.setMargins(0, dpToPx(20), 0, dpToPx(16));
        card.addView(sep, sepParams);

        // Growth badge row
        LinearLayout badgeRow = new LinearLayout(requireContext());
        badgeRow.setOrientation(LinearLayout.HORIZONTAL);
        badgeRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView growthBadge = new TextView(requireContext());
        growthBadge.setText("  ↗ +15.3% growth this month  ");
        growthBadge.setTextSize(12);
        growthBadge.setTypeface(null, Typeface.BOLD);
        growthBadge.setTextColor(Color.parseColor("#065F46"));
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(Color.parseColor("#34D399"));
        badgeBg.setCornerRadius(dpToPx(100));
        growthBadge.setBackground(badgeBg);
        growthBadge.setPadding(dpToPx(4), dpToPx(6), dpToPx(4), dpToPx(6));

        View spacer = new View(requireContext());
        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1f));

        TextView viewAll = new TextView(requireContext());
        viewAll.setText("View Report →");
        viewAll.setTextSize(12);
        viewAll.setTextColor(Color.parseColor("#6EE7B7"));

        badgeRow.addView(growthBadge);
        badgeRow.addView(spacer);
        badgeRow.addView(viewAll);
        card.addView(badgeRow);

        wrapper.addView(card);
        return wrapper;
    }

    private View createOverviewMetric(String value, String label, String valueColor) {
        LinearLayout col = new LinearLayout(requireContext());
        col.setOrientation(LinearLayout.VERTICAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView valTv = new TextView(requireContext());
        valTv.setText(value);
        valTv.setTextSize(22);
        valTv.setTypeface(null, Typeface.BOLD);
        valTv.setTextColor(Color.parseColor(valueColor));

        TextView lblTv = new TextView(requireContext());
        lblTv.setText(label);
        lblTv.setTextSize(12);
        lblTv.setTextColor(Color.parseColor("#9CA3AF"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dpToPx(4);
        lblTv.setLayoutParams(lp);

        col.addView(valTv);
        col.addView(lblTv);
        return col;
    }

    // ─── Services Grid ────────────────────────────────────────────────────────
    private View createServicesSection() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(8));

        TextView title = new TextView(requireContext());
        title.setText("Quick Services");
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(COLOR_TEXT_PRIMARY);
        title.setPadding(dpToPx(4), 0, 0, dpToPx(16));
        layout.addView(title);

        LinearLayout row1 = new LinearLayout(requireContext());
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setWeightSum(2f);
        row1.addView(createServiceCard("Orders",   "Manage Now",  "#4F46E5", "#7C3AED", v -> switchTab(3)));
        row1.addView(createServiceCard("Market",   "Trade Now",   "#BE185D", "#E11D48", v -> switchTab(1)));

        LinearLayout row2 = new LinearLayout(requireContext());
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.setWeightSum(2f);
        LinearLayout.LayoutParams row2Params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        row2Params.topMargin = dpToPx(12);
        row2.setLayoutParams(row2Params);
        row2.addView(createServiceCard("Inventory","Check Stock", "#0369A1", "#0891B2", v -> switchTab(2)));
        row2.addView(createServiceCard("Profile",  "My Account",  "#065F46", "#059669", v -> switchTab(4)));

        layout.addView(row1);
        layout.addView(row2);
        return layout;
    }

    private void switchTab(int tabIndex) {
        if (getActivity() instanceof RealDashboardActivity) {
            ((RealDashboardActivity) getActivity()).switchTab(tabIndex);
        }
    }

    private View createServiceCard(String title, String subtitle,
                                    String colorStart, String colorEnd,
                                    View.OnClickListener listener) {
        FrameLayout container = new FrameLayout(requireContext());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        p.setMargins(dpToPx(6), 0, dpToPx(6), 0);
        container.setLayoutParams(p);

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(20), dpToPx(24), dpToPx(20), dpToPx(24));
        card.setElevation(dpToPx(6));

        GradientDrawable cardBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.parseColor(colorStart), Color.parseColor(colorEnd)}
        );
        cardBg.setCornerRadius(dpToPx(20));
        card.setBackground(cardBg);

        // Icon circle
        TextView iconView = new TextView(requireContext());
        iconView.setText(title.substring(0, 1));
        iconView.setTextSize(20);
        iconView.setTextColor(Color.parseColor(colorStart));
        iconView.setTypeface(null, Typeface.BOLD);
        iconView.setGravity(Gravity.CENTER);

        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setShape(GradientDrawable.OVAL);
        iconBg.setColor(Color.argb(50, 255, 255, 255));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(44), dpToPx(44));
        iconParams.bottomMargin = dpToPx(14);
        iconView.setLayoutParams(iconParams);
        iconView.setBackground(iconBg);
        iconView.setTextColor(COLOR_WHITE);

        TextView titleText = new TextView(requireContext());
        titleText.setText(title);
        titleText.setTextSize(16);
        titleText.setTextColor(COLOR_WHITE);
        titleText.setTypeface(null, Typeface.BOLD);

        TextView subTitleText = new TextView(requireContext());
        subTitleText.setText(subtitle);
        subTitleText.setTextSize(12);
        subTitleText.setTextColor(Color.argb(200, 255, 255, 255));
        LinearLayout.LayoutParams stlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        stlp.topMargin = dpToPx(4);
        subTitleText.setLayoutParams(stlp);

        card.addView(iconView);
        card.addView(titleText);
        card.addView(subTitleText);

        RippleDrawable ripple = new RippleDrawable(
            ColorStateList.valueOf(Color.argb(60, 255, 255, 255)),
            cardBg,
            null
        );
        card.setBackground(ripple);
        card.setClickable(true);
        card.setOnClickListener(listener);

        container.addView(card);
        return container;
    }

    // ─── Activity Feed ────────────────────────────────────────────────────────
    private View createActivitySection() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(40));

        TextView title = new TextView(requireContext());
        title.setText("Recent Activity");
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(COLOR_TEXT_PRIMARY);
        title.setPadding(dpToPx(4), 0, 0, dpToPx(16));
        layout.addView(title);

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(COLOR_WHITE);
        cardBg.setCornerRadius(dpToPx(24));
        card.setBackground(cardBg);
        card.setElevation(dpToPx(6));
        card.setPadding(0, dpToPx(8), 0, dpToPx(8));

        card.addView(createActivityItem("Order Approved",    "Order #4092 shipped to warehouse",  "Just now",  "#059669", "#D1FAE5", true));
        card.addView(createActivityItem("Payment Received",  "$1,450.00 received from John Doe",  "2 hr ago",  "#D97706", "#FEF3C7", true));
        card.addView(createActivityItem("Stock Alert",       "Pine wood inventory running low",   "1 day ago", "#EF4444", "#FEE2E2", false));

        layout.addView(card);
        return layout;
    }

    private View createActivityItem(String title, String subtitle, String time,
                                     String dotColorStr, String dotBgStr, boolean showLine) {
        LinearLayout item = new LinearLayout(requireContext());
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(dpToPx(20), dpToPx(14), dpToPx(20), dpToPx(14));

        // Timeline column
        LinearLayout timelineCol = new LinearLayout(requireContext());
        timelineCol.setOrientation(LinearLayout.VERTICAL);
        timelineCol.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams tcParams = new LinearLayout.LayoutParams(dpToPx(36), ViewGroup.LayoutParams.MATCH_PARENT);
        tcParams.rightMargin = dpToPx(14);
        timelineCol.setLayoutParams(tcParams);

        // Colored dot badge
        TextView dotBadge = new TextView(requireContext());
        dotBadge.setGravity(Gravity.CENTER);
        dotBadge.setText(title.substring(0, 1));
        dotBadge.setTextSize(13);
        dotBadge.setTypeface(null, Typeface.BOLD);
        dotBadge.setTextColor(Color.parseColor(dotColorStr));
        GradientDrawable dotBg = new GradientDrawable();
        dotBg.setShape(GradientDrawable.OVAL);
        dotBg.setColor(Color.parseColor(dotBgStr));
        dotBadge.setBackground(dotBg);
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dpToPx(36), dpToPx(36));
        dotBadge.setLayoutParams(dotParams);

        timelineCol.addView(dotBadge);

        if (showLine) {
            View line = new View(requireContext());
            line.setBackgroundColor(Color.parseColor("#E5E7EB"));
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(dpToPx(2), 0, 1f);
            lineParams.topMargin = dpToPx(4);
            lineParams.gravity = Gravity.CENTER_HORIZONTAL;
            line.setLayoutParams(lineParams);
            timelineCol.addView(line);
        }

        // Text content
        LinearLayout contentLayout = new LinearLayout(requireContext());
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView titleText = new TextView(requireContext());
        titleText.setText(title);
        titleText.setTextSize(15);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setTextColor(COLOR_TEXT_PRIMARY);

        TextView subText = new TextView(requireContext());
        subText.setText(subtitle);
        subText.setTextSize(13);
        subText.setTextColor(COLOR_TEXT_SECONDARY);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        slp.topMargin = dpToPx(2);
        subText.setLayoutParams(slp);

        TextView timeText = new TextView(requireContext());
        timeText.setText(time);
        timeText.setTextSize(11);
        timeText.setTextColor(Color.parseColor(dotColorStr));
        timeText.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tlp.topMargin = dpToPx(4);
        timeText.setLayoutParams(tlp);

        contentLayout.addView(titleText);
        contentLayout.addView(subText);
        contentLayout.addView(timeText);

        item.addView(timelineCol);
        item.addView(contentLayout);
        return item;
    }

    // ─── Logout Sheet ─────────────────────────────────────────────────────────
    private void showLogoutMenu(View anchor) {
        BottomSheetDialog logoutSheet = new BottomSheetDialog(requireContext());

        LinearLayout sheetView = new LinearLayout(requireContext());
        sheetView.setOrientation(LinearLayout.VERTICAL);
        sheetView.setPadding(dpToPx(24), dpToPx(32), dpToPx(24), dpToPx(32));
        sheetView.setBackgroundColor(Color.WHITE);

        View handle = new View(requireContext());
        GradientDrawable handleBg = new GradientDrawable();
        handleBg.setColor(Color.parseColor("#D1FAE5"));
        handleBg.setCornerRadius(dpToPx(100));
        handle.setBackground(handleBg);
        LinearLayout.LayoutParams handleParams = new LinearLayout.LayoutParams(dpToPx(40), dpToPx(4));
        handleParams.gravity = Gravity.CENTER_HORIZONTAL;
        handleParams.bottomMargin = dpToPx(24);
        sheetView.addView(handle, handleParams);

        TextView title = new TextView(requireContext());
        title.setText("Sign Out");
        title.setTextSize(20);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(COLOR_TEXT_PRIMARY);
        title.setGravity(Gravity.CENTER);
        sheetView.addView(title);

        TextView message = new TextView(requireContext());
        message.setText("Are you sure you want to sign out?");
        message.setTextSize(14);
        message.setTextColor(COLOR_TEXT_SECONDARY);
        message.setGravity(Gravity.CENTER);
        message.setPadding(0, dpToPx(12), 0, dpToPx(32));
        sheetView.addView(message);

        MaterialButton btnConfirm = new MaterialButton(requireContext());
        btnConfirm.setText("Sign Out");
        btnConfirm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EF4444")));
        btnConfirm.setTextColor(Color.WHITE);
        btnConfirm.setCornerRadius(dpToPx(16));
        btnConfirm.setPadding(0, dpToPx(16), 0, dpToPx(16));
        sheetView.addView(btnConfirm);

        btnConfirm.setOnClickListener(v -> {
            logoutSheet.dismiss();
            SharedPreferences prefs = requireContext().getSharedPreferences("TimberTradePrefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        TextView btnCancel = new TextView(requireContext());
        btnCancel.setText("Nevermind, stay logged in");
        btnCancel.setTextSize(14);
        btnCancel.setGravity(Gravity.CENTER);
        btnCancel.setTextColor(COLOR_TEXT_SECONDARY);
        btnCancel.setPadding(0, dpToPx(20), 0, 0);
        btnCancel.setClickable(true);
        btnCancel.setOnClickListener(v -> logoutSheet.dismiss());
        sheetView.addView(btnCancel);

        logoutSheet.setContentView(sheetView);
        logoutSheet.show();
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
