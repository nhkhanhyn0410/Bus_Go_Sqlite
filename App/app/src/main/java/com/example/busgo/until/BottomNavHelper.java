package com.example.busgo.until;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.busgo.R;
import com.example.busgo.activities.user.BookingHistoryActivity;
import com.example.busgo.activities.user.MainActivity;
import com.example.busgo.activities.user.ProfileActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

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
    private View bottomNavBar;


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
        bottomNavBar = activity.findViewById(R.id.bottomNavBar);

        // Set trạng thái ban đầu (không animation)
        initSelectedState(selectedTab);

        // Set listeners
        navHome.setOnClickListener(v -> onTabClicked(TAB_HOME));
        navBookings.setOnClickListener(v -> onTabClicked(TAB_BOOKINGS));
        navProfile.setOnClickListener(v -> onTabClicked(TAB_PROFILE));

        // Lấy margin gốc từ XML (24dp) để cộng thêm insets
        final int originalBottomMargin = ((ViewGroup.MarginLayoutParams) bottomNavBar.getLayoutParams()).bottomMargin;

        // Đẩy toàn bộ thanh nav (cả background) lên trên thanh điều hướng hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavBar, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.bottomMargin = originalBottomMargin + insets.bottom;
            view.setLayoutParams(params);
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(bottomNavBar);
    }


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

    private void setSelected(LinearLayout nav, ImageView icon, TextView label) {
        nav.setBackgroundResource(R.drawable.bg_nav_item_selected);
        icon.setColorFilter(ContextCompat.getColor(activity, R.color.text_primary));
        label.setVisibility(View.VISIBLE);
        label.setAlpha(1f);
    }


    private void setUnselected(LinearLayout nav, ImageView icon, TextView label) {
        nav.setBackgroundResource(0);
        icon.setColorFilter(ContextCompat.getColor(activity, R.color.white));
        label.setVisibility(View.GONE);
    }

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
