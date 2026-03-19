package com.timbertrade.app.dashboard;

import android.content.res.ColorStateList;
import android.graphics.Color;
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
import android.widget.PopupMenu;
import android.view.Menu;

public class DashboardFragment extends Fragment {

    private final int COLOR_PRIMARY = Color.parseColor("#059669");
    private final int COLOR_PRIMARY_DARK = Color.parseColor("#047857");
    private final int COLOR_ACCENT = Color.parseColor("#D97706");
    private final int COLOR_BG = Color.parseColor("#F3F4F6");
    private final int COLOR_TEXT_PRIMARY = Color.parseColor("#1F2937");
    private final int COLOR_TEXT_SECONDARY = Color.parseColor("#6B7280");
    private final int COLOR_WHITE = Color.WHITE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout root = new RelativeLayout(requireContext());
        root.setBackgroundColor(COLOR_BG);

        View headerBg = new View(requireContext());
        GradientDrawable gradientBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{COLOR_PRIMARY, COLOR_PRIMARY_DARK}
        );
        headerBg.setBackground(gradientBg);
        RelativeLayout.LayoutParams headerBgParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(280)
        );
        headerBgParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(headerBg, headerBgParams);

        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.setVerticalScrollBarEnabled(false);
        RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        
        LinearLayout scrollContent = new LinearLayout(requireContext());
        scrollContent.setOrientation(LinearLayout.VERTICAL);
        scrollContent.setPadding(0, 0, 0, dpToPx(30)); 

        scrollContent.addView(createGreetingSection());
        scrollContent.addView(createOverlappingOverviewCard());
        scrollContent.addView(createServicesSection());
        scrollContent.addView(createTimelineActivitySection());

        scrollView.addView(scrollContent);
        root.addView(scrollView, scrollParams);

        return root;
    }

    private View createGreetingSection() {
        LinearLayout headerLayout = new LinearLayout(requireContext());
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setPadding(dpToPx(24), dpToPx(40), dpToPx(24), dpToPx(20));
        headerLayout.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout textLayout = new LinearLayout(requireContext());
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textLayout.setLayoutParams(textParams);

        TextView greeting = new TextView(requireContext());
        greeting.setText("Hello, Atanasi 👋");
        greeting.setTextSize(16);
        greeting.setTextColor(Color.parseColor("#A7F3D0"));

        TextView subTitle = new TextView(requireContext());
        subTitle.setText("Timber Dashboard");
        subTitle.setTextSize(26);
        subTitle.setTypeface(null, Typeface.BOLD);
        subTitle.setTextColor(COLOR_WHITE);

        textLayout.addView(greeting);
        textLayout.addView(subTitle);

        TextView avatar = new TextView(requireContext());
        avatar.setText("AK");
        avatar.setTextColor(COLOR_PRIMARY);
        avatar.setTextSize(18);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setGravity(Gravity.CENTER);
        
        GradientDrawable avatarBg = new GradientDrawable();
        avatarBg.setShape(GradientDrawable.OVAL);
        avatarBg.setColor(COLOR_WHITE);
        avatar.setBackground(avatarBg);
        
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(dpToPx(50), dpToPx(50));
        avatar.setLayoutParams(avatarParams);
        avatar.setElevation(dpToPx(4));

        // Add Logout feature here
        avatar.setClickable(true);
        avatar.setOnClickListener(v -> showLogoutMenu(v));

        headerLayout.addView(textLayout);
        headerLayout.addView(avatar);

        return headerLayout;
    }

    private View createOverlappingOverviewCard() {
        LinearLayout wrapper = new LinearLayout(requireContext());
        wrapper.setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(10));
        
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_WHITE);
        bg.setCornerRadius(dpToPx(24));
        card.setBackground(bg);
        card.setElevation(dpToPx(12));
        card.setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(24));
        
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        card.setLayoutParams(cardParams);

        LinearLayout topRow = new LinearLayout(requireContext());
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setWeightSum(2f);

        LinearLayout leftCol = new LinearLayout(requireContext());
        leftCol.setOrientation(LinearLayout.VERTICAL);
        leftCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        
        TextView balanceLabel = new TextView(requireContext());
        balanceLabel.setText("Total Revenue");
        balanceLabel.setTextSize(14);
        balanceLabel.setTextColor(COLOR_TEXT_SECONDARY);
        
        TextView balanceText = new TextView(requireContext());
        balanceText.setText("$45,230.00");
        balanceText.setTextSize(26);
        balanceText.setTypeface(null, Typeface.BOLD);
        balanceText.setTextColor(COLOR_TEXT_PRIMARY);
        
        leftCol.addView(balanceLabel);
        leftCol.addView(balanceText);
        
        LinearLayout rightCol = new LinearLayout(requireContext());
        rightCol.setOrientation(LinearLayout.VERTICAL);
        rightCol.setGravity(Gravity.END);
        rightCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        
        TextView orderLabel = new TextView(requireContext());
        orderLabel.setText("Active Orders");
        orderLabel.setTextSize(14);
        orderLabel.setTextColor(COLOR_TEXT_SECONDARY);
        
        TextView orderText = new TextView(requireContext());
        orderText.setText("12");
        orderText.setTextSize(26);
        orderText.setTypeface(null, Typeface.BOLD);
        orderText.setTextColor(COLOR_ACCENT);
        
        rightCol.addView(orderLabel);
        rightCol.addView(orderText);
        
        topRow.addView(leftCol);
        topRow.addView(rightCol);
        card.addView(topRow);

        View separator = new View(requireContext());
        separator.setBackgroundColor(Color.parseColor("#E5E7EB"));
        LinearLayout.LayoutParams sepParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1));
        sepParams.setMargins(0, dpToPx(20), 0, dpToPx(20));
        card.addView(separator, sepParams);

        TextView growthMetric = new TextView(requireContext());
        growthMetric.setText("+15.3% growth this month ↗");
        growthMetric.setTextSize(13);
        growthMetric.setTypeface(null, Typeface.BOLD);
        growthMetric.setTextColor(Color.parseColor("#10B981"));
        card.addView(growthMetric);

        wrapper.addView(card);
        return wrapper;
    }

    private View createServicesSection() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(20), dpToPx(15), dpToPx(20), dpToPx(5));

        TextView title = new TextView(requireContext());
        title.setText("Quick Services");
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(COLOR_TEXT_PRIMARY);
        title.setPadding(dpToPx(5), 0, 0, dpToPx(15));
        layout.addView(title);

        LinearLayout row1 = new LinearLayout(requireContext());
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setWeightSum(2f);
        row1.addView(createServiceCard("Orders", "Manage", "#E0E7FF", "#4338CA", v -> {
            switchTab(2); // Orders is tab 2
        }));
        row1.addView(createServiceCard("Market", "Trade Now", "#FCE7F3", "#BE185D", v -> {
            switchTab(1); // Market is tab 1
        }));

        layout.addView(row1);
        return layout;
    }

    private void switchTab(int tabIndex) {
        if (getActivity() instanceof RealDashboardActivity) {
            ((RealDashboardActivity) getActivity()).switchTab(tabIndex);
        }
    }

    private View createServiceCard(String title, String subtitle, String bgColorStr, String iconColorStr, View.OnClickListener listener) {
        FrameLayout container = new FrameLayout(requireContext());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        p.setMargins(dpToPx(6), 0, dpToPx(6), 0);
        container.setLayoutParams(p);

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(20), dpToPx(24), dpToPx(20), dpToPx(24));
        
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_WHITE);
        bg.setCornerRadius(dpToPx(20));
        card.setBackground(bg);
        card.setElevation(dpToPx(4));

        TextView iconView = new TextView(requireContext());
        iconView.setText(title.substring(0, 1));
        iconView.setTextSize(22);
        iconView.setTextColor(Color.parseColor(iconColorStr));
        iconView.setTypeface(null, Typeface.BOLD);
        iconView.setGravity(Gravity.CENTER);
        
        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setShape(GradientDrawable.OVAL);
        iconBg.setColor(Color.parseColor(bgColorStr));
        
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(48), dpToPx(48));
        iconParams.setMargins(0, 0, 0, dpToPx(16));
        iconView.setLayoutParams(iconParams);
        iconView.setBackground(iconBg);
        
        TextView titleText = new TextView(requireContext());
        titleText.setText(title);
        titleText.setTextSize(16);
        titleText.setTextColor(COLOR_TEXT_PRIMARY);
        titleText.setTypeface(null, Typeface.BOLD);
        
        TextView subTitleText = new TextView(requireContext());
        subTitleText.setText(subtitle);
        subTitleText.setTextSize(12);
        subTitleText.setTextColor(COLOR_TEXT_SECONDARY);

        card.addView(iconView);
        card.addView(titleText);
        card.addView(subTitleText);
        
        RippleDrawable ripple = new RippleDrawable(
            ColorStateList.valueOf(Color.parseColor("#1A000000")), 
            bg, 
            null
        );
        card.setBackground(ripple);
        card.setClickable(true);
        card.setOnClickListener(listener);

        container.addView(card);
        return container;
    }

    private View createTimelineActivitySection() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(40));

        TextView title = new TextView(requireContext());
        title.setText("Recent Activity");
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(COLOR_TEXT_PRIMARY);
        title.setPadding(dpToPx(5), 0, 0, dpToPx(15));
        layout.addView(title);

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_WHITE);
        bg.setCornerRadius(dpToPx(20));
        card.setBackground(bg);
        card.setElevation(dpToPx(4));
        card.setPadding(0, dpToPx(15), 0, dpToPx(15));
        
        card.addView(createTimelineItem("Order Approved", "Order #4092 shipped to warehouse", "Just now", COLOR_PRIMARY, true));
        card.addView(createTimelineItem("Payment Received", "$1,450.00 from John Doe", "2 hr ago", COLOR_ACCENT, true));
        card.addView(createTimelineItem("Stock Alert", "Pine wood inventory low", "1 day ago", Color.parseColor("#EF4444"), false));

        layout.addView(card);
        return layout;
    }

    private View createTimelineItem(String title, String subtitle, String time, int dotColor, boolean hasLineIndicator) {
        LinearLayout item = new LinearLayout(requireContext());
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(10));
        
        LinearLayout timelineCol = new LinearLayout(requireContext());
        timelineCol.setOrientation(LinearLayout.VERTICAL);
        timelineCol.setGravity(Gravity.CENTER_HORIZONTAL);
        
        View dot = new View(requireContext());
        GradientDrawable dotBg = new GradientDrawable();
        dotBg.setShape(GradientDrawable.OVAL);
        dotBg.setColor(dotColor);
        dot.setBackground(dotBg);
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(dpToPx(12), dpToPx(12));
        dotParams.setMargins(0, dpToPx(6), dpToPx(15), 0);
        
        timelineCol.addView(dot, dotParams);

        if (hasLineIndicator) {
            View line = new View(requireContext());
            line.setBackgroundColor(Color.parseColor("#E5E7EB"));
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(dpToPx(2), ViewGroup.LayoutParams.MATCH_PARENT);
            lineParams.setMargins(0, dpToPx(4), dpToPx(15), dpToPx(4));
            line.setLayoutParams(lineParams);
            timelineCol.addView(line);
        }

        LinearLayout contentLayout = new LinearLayout(requireContext());
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        contentLayout.setLayoutParams(contentParams);

        TextView titleText = new TextView(requireContext());
        titleText.setText(title);
        titleText.setTextSize(15);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setTextColor(COLOR_TEXT_PRIMARY);

        TextView subTitleText = new TextView(requireContext());
        subTitleText.setText(subtitle);
        subTitleText.setTextSize(13);
        subTitleText.setTextColor(COLOR_TEXT_SECONDARY);
        subTitleText.setPadding(0, dpToPx(2), 0, dpToPx(2));
        
        TextView timeText = new TextView(requireContext());
        timeText.setText(time);
        timeText.setTextSize(11);
        timeText.setTextColor(Color.parseColor("#9CA3AF"));

        contentLayout.addView(titleText);
        contentLayout.addView(subTitleText);
        contentLayout.addView(timeText);

        item.addView(timelineCol);
        item.addView(contentLayout);
        
        return item;
    }

    private void showLogoutMenu(View anchor) {
        PopupMenu popup = new PopupMenu(requireContext(), anchor);
        popup.getMenu().add(Menu.NONE, 1, Menu.NONE, "Sign Out");
        
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                SharedPreferences prefs = requireContext().getSharedPreferences("TimberTradePrefs", Context.MODE_PRIVATE);
                prefs.edit().clear().apply();
                
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private int dpToPx(int dp) { return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()); }
}
