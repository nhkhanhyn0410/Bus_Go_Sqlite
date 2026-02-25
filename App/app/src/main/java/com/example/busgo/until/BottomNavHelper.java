package com.example.busgo.until;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.busgo.R;
import com.example.busgo.activities.user.BookingHistoryActivity;
import com.example.busgo.activities.user.MainActivity;
import com.example.busgo.activities.user.ProfileActivity;

/**
 * BottomNavHelper - Quản lý Bottom Navigation Bar pill dùng chung
 *
 * Chức năng:
 * - Hiển thị trạng thái selected: nền trắng, icon đen, hiện label
 * - Hiển thị trạng thái unselected: không nền, icon trắng, ẩn label
 * - Animation fade khi chuyển tab
 * - Chuyển màn hình giữa Main, BookingHistory, Profile
 *
 * Sử dụng: gọi BottomNavHelper.setup(activity, "home") trong onCreate()
 */
public class BottomNavHelper {

    // Tên các tab
    public static final String TAB_HOME = "home";
    public static final String TAB_BOOKINGS = "bookings";
    public static final String TAB_PROFILE = "profile";

    // Views
    private final Activity activity;
    private final LinearLayout navHome, navBookings, navProfile;
    private final ImageView navHomeIcon, navBookingsIcon, navProfileIcon;
    private final TextView navHomeLabel, navBookingsLabel, navProfileLabel;
    private LinearLayout currentSelected;
    private String currentTab;

    /**
     * Setup Bottom Navigation cho Activity
     * @param activity Activity chứa bottom nav
     * @param selectedTab Tab đang chọn: "home", "bookings", "profile"
     */
    public static void setup(Activity activity, String selectedTab) {
        new BottomNavHelper(activity, selectedTab);
    }

    private BottomNavHelper(Activity activity, String selectedTab) {
        this.activity = activity;
        this.currentTab = selectedTab;

        // Tìm views
        navHome = activity.findViewById(R.id.navHome);
        navBookings = activity.findViewById(R.id.navBookings);
        navProfile = activity.findViewById(R.id.navProfile);
        navHomeIcon = activity.findViewById(R.id.navHomeIcon);
        navBookingsIcon = activity.findViewById(R.id.navBookingsIcon);
        navProfileIcon = activity.findViewById(R.id.navProfileIcon);
        navHomeLabel = activity.findViewById(R.id.navHomeLabel);
        navBookingsLabel = activity.findViewById(R.id.navBookingsLabel);
        navProfileLabel = activity.findViewById(R.id.navProfileLabel);

        // Set trạng thái ban đầu (không animation)
        initSelectedState(selectedTab);

        // Set listeners
        navHome.setOnClickListener(v -> onTabClicked(TAB_HOME));
        navBookings.setOnClickListener(v -> onTabClicked(TAB_BOOKINGS));
        navProfile.setOnClickListener(v -> onTabClicked(TAB_PROFILE));

        Log.d("CHECK_NAV", "navHome = " + navHome);
        Log.d("CHECK_NAV", "navBookings = " + navBookings);
        Log.d("CHECK_NAV", "navProfile = " + navProfile);
    }

    /**
     * Set trạng thái selected ban đầu (không animation)
     */
    private void initSelectedState(String tab) {
        // Reset tất cả về unselected
        setUnselected(navHome, navHomeIcon, navHomeLabel);
        setUnselected(navBookings, navBookingsIcon, navBookingsLabel);
        setUnselected(navProfile, navProfileIcon, navProfileLabel);

        // Set selected cho tab hiện tại
        switch (tab) {
            case TAB_HOME:
                setSelected(navHome, navHomeIcon, navHomeLabel);
                currentSelected = navHome;
                break;
            case TAB_BOOKINGS:
                setSelected(navBookings, navBookingsIcon, navBookingsLabel);
                currentSelected = navBookings;
                break;
            case TAB_PROFILE:
                setSelected(navProfile, navProfileIcon, navProfileLabel);
                currentSelected = navProfile;
                break;
        }
    }

    /**
     * Khi user nhấn tab
     */
    private void onTabClicked(String tab) {
        if (tab.equals(currentTab)) return; // Đang ở tab này rồi

        // Animation chuyển tab
        LinearLayout newNav = getNavByTab(tab);
        ImageView newIcon = getIconByTab(tab);
        TextView newLabel = getLabelByTab(tab);

        // Animate ra item cũ
        ImageView oldIcon = getIconByTab(currentTab);
        TextView oldLabel = getLabelByTab(currentTab);

        // Fade out label cũ + đổi icon cũ sang trắng
        if (oldLabel != null) {
            oldLabel.animate()
                    .alpha(0f)
                    .setDuration(150)
                    .withEndAction(() -> {
                        oldLabel.setVisibility(View.GONE);
                        oldLabel.setAlpha(1f);
                    })
                    .start();
        }
        if (oldIcon != null) {
            oldIcon.setColorFilter(ContextCompat.getColor(activity, R.color.white));
        }
        currentSelected.setBackgroundResource(0);

        // Fade in label mới + đổi icon mới sang đen
        if (newLabel != null) {
            newLabel.setAlpha(0f);
            newLabel.setVisibility(View.VISIBLE);
            newLabel.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .setStartDelay(80)
                    .start();
        }
        if (newIcon != null) {
            newIcon.setColorFilter(ContextCompat.getColor(activity, R.color.text_primary));
        }
        if (newNav != null) {
            newNav.setBackgroundResource(R.drawable.bg_nav_item_selected);
        }

        currentSelected = newNav;
        currentTab = tab;

        // Chuyển màn hình sau khi animation hoàn tất
        if (newNav != null) {
            newNav.postDelayed(() -> navigateTo(tab), 250);
        }
    }

    /**
     * Chuyển sang Activity tương ứng
     */
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
        // Không dùng transition animation mặc định
        activity.overridePendingTransition(0, 0);
        activity.finish();
    }

    // ==================== Helper: set trạng thái ====================

    /**
     * Set item thành selected: nền trắng, icon đen, hiện label
     */
    private void setSelected(LinearLayout nav, ImageView icon, TextView label) {
        nav.setBackgroundResource(R.drawable.bg_nav_item_selected);
        icon.setColorFilter(ContextCompat.getColor(activity, R.color.text_primary));
        label.setVisibility(View.VISIBLE);
        label.setAlpha(1f);
    }

    /**
     * Set item thành unselected: không nền, icon trắng, ẩn label
     */
    private void setUnselected(LinearLayout nav, ImageView icon, TextView label) {
        nav.setBackgroundResource(0);
        icon.setColorFilter(ContextCompat.getColor(activity, R.color.white));
        label.setVisibility(View.GONE);
    }

    // ==================== Helper: lấy views theo tab ====================

    private LinearLayout getNavByTab(String tab) {
        switch (tab) {
            case TAB_HOME: return navHome;
            case TAB_BOOKINGS: return navBookings;
            case TAB_PROFILE: return navProfile;
        }
        return null;
    }

    private ImageView getIconByTab(String tab) {
        switch (tab) {
            case TAB_HOME: return navHomeIcon;
            case TAB_BOOKINGS: return navBookingsIcon;
            case TAB_PROFILE: return navProfileIcon;
        }
        return null;
    }

    private TextView getLabelByTab(String tab) {
        switch (tab) {
            case TAB_HOME: return navHomeLabel;
            case TAB_BOOKINGS: return navBookingsLabel;
            case TAB_PROFILE: return navProfileLabel;
        }
        return null;
    }
}

