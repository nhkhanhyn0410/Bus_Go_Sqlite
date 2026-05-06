package com.example.busgo.until;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.busgo.R;
import com.example.busgo.activities.user.BookingHistoryActivity;
import com.example.busgo.activities.user.MainActivity;
import com.example.busgo.activities.user.ProfileActivity;


public class BottomNavHelper {

    public static final String TAB_HOME = "home";
    public static final String TAB_BOOKINGS = "bookings";
    public static final String TAB_PROFILE = "profile";

    private final Activity activity;
    private final LinearLayout navHome, navBookings, navProfile;
    private final ImageView navHomeIcon, navBookingsIcon, navProfileIcon;
    private final TextView navHomeLabel, navBookingsLabel, navProfileLabel;
    private final View bottomNavBar;
    private String currentTab;

    public static void setup(Activity activity, String selectedTab) {
        new BottomNavHelper(activity, selectedTab);
    }

    private BottomNavHelper(Activity activity, String selectedTab) {
        this.activity = activity;
        this.currentTab = selectedTab;

        navHome = activity.findViewById(R.id.navHome);
        navBookings = activity.findViewById(R.id.navBookings);
        navProfile = activity.findViewById(R.id.navProfile);
        navHomeIcon = activity.findViewById(R.id.navHomeIcon);
        navBookingsIcon = activity.findViewById(R.id.navBookingsIcon);
        navProfileIcon = activity.findViewById(R.id.navProfileIcon);
        navHomeLabel = activity.findViewById(R.id.navHomeLabel);
        navBookingsLabel = activity.findViewById(R.id.navBookingsLabel);
        navProfileLabel = activity.findViewById(R.id.navProfileLabel);
        bottomNavBar = activity.findViewById(R.id.bottomNavBar);

        initSelectedState(selectedTab);

        navHome.setOnClickListener(v -> onTabClicked(TAB_HOME));
        navBookings.setOnClickListener(v -> onTabClicked(TAB_BOOKINGS));
        navProfile.setOnClickListener(v -> onTabClicked(TAB_PROFILE));

        if (bottomNavBar != null) {
            final int originalBottomMargin = ((ViewGroup.MarginLayoutParams) bottomNavBar.getLayoutParams()).bottomMargin;
            ViewCompat.setOnApplyWindowInsetsListener(bottomNavBar, (view, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                params.bottomMargin = originalBottomMargin + insets.bottom;
                view.setLayoutParams(params);
                return windowInsets;
            });
            ViewCompat.requestApplyInsets(bottomNavBar);
        }
    }

    private void initSelectedState(String tab) {
        setUnselected(navHome, navHomeIcon, navHomeLabel);
        setUnselected(navBookings, navBookingsIcon, navBookingsLabel);
        setUnselected(navProfile, navProfileIcon, navProfileLabel);

        switch (tab) {
            case TAB_HOME:
                setSelected(navHome, navHomeIcon, navHomeLabel);
                break;
            case TAB_BOOKINGS:
                setSelected(navBookings, navBookingsIcon, navBookingsLabel);
                break;
            case TAB_PROFILE:
                setSelected(navProfile, navProfileIcon, navProfileLabel);
                break;
        }
    }

    private void onTabClicked(String tab) {
        if (tab.equals(currentTab)) return;
        navigateTo(tab);
    }

    private void navigateTo(String tab) {
        Class<?> targetClass;
        switch (tab) {
            case TAB_HOME:
                targetClass = MainActivity.class;
                break;
            case TAB_BOOKINGS:
                targetClass = BookingHistoryActivity.class;
                break;
            case TAB_PROFILE:
                targetClass = ProfileActivity.class;
                break;
            default:
                return;
        }

        Intent intent = new Intent(activity, targetClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.nav_fade_in, R.anim.nav_fade_out);
        activity.finish();
    }

    private void setSelected(LinearLayout nav, ImageView icon, TextView label) {
        nav.setBackgroundResource(R.drawable.bg_nav_item_selected);
        icon.setColorFilter(ContextCompat.getColor(activity, R.color.text_primary));
        label.setVisibility(View.VISIBLE);
    }

    private void setUnselected(LinearLayout nav, ImageView icon, TextView label) {
        nav.setBackgroundResource(0);
        icon.setColorFilter(ContextCompat.getColor(activity, R.color.white));
        label.setVisibility(View.GONE);
    }
}
