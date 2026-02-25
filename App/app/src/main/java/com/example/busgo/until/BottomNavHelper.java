package com.example.busgo.until;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
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

    // Tên các tab
    public static final String TAB_HOME = "home";
    public static final String TAB_BOOKINGS = "bookings";
    public static final String TAB_PROFILE = "profile";

    // Thời gian animation (ms)
    private static final int ANIM_DURATION = 280;

    // Views
    private final Activity activity;
    private final LinearLayout navHome, navBookings, navProfile;
    private final ImageView navHomeIcon, navBookingsIcon, navProfileIcon;
    private final TextView navHomeLabel, navBookingsLabel, navProfileLabel;
    private LinearLayout currentSelected;
    private String currentTab;
    private View bottomNavBar;

    // Tránh spam click khi đang animate
    private boolean isAnimating = false;

    private boolean navigateToRight = true;

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
     * Khi user nhấn tab - animation mượt co giãn chiều rộng + fade alpha
     */
    private void onTabClicked(String tab) {
        if (tab.equals(currentTab)) return; // Đang ở tab này rồi
        if (isAnimating) return; // Đang animate, bỏ qua

        LinearLayout oldNav = currentSelected;
        ImageView oldIcon = getIconByTab(currentTab);
        TextView oldLabel = getLabelByTab(currentTab);

        LinearLayout newNav = getNavByTab(tab);
        ImageView newIcon = getIconByTab(tab);
        TextView newLabel = getLabelByTab(tab);

        isAnimating = true;

        // Xác định hướng chuyển màn hình dựa theo vị trí tab

        // === Đo chiều rộng trước khi animate ===
        // Chiều rộng hiện tại của tab cũ (đang mở rộng có label)
        int oldExpandedWidth = oldNav.getWidth();

        // Đo chiều rộng thu gọn của tab cũ (chỉ icon, không label)
        // = paddingLeft + iconWidth + paddingRight
        int oldCollapsedWidth = oldNav.getPaddingLeft() + oldIcon.getWidth() + oldNav.getPaddingRight();

        // Đo chiều rộng mở rộng của tab mới (icon + label)
        // Hiện label tạm để đo, sau đó ẩn lại
        newLabel.setVisibility(View.VISIBLE);
        newLabel.setAlpha(0f);
        newNav.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(newNav.getHeight(), View.MeasureSpec.EXACTLY)
        );
        int newExpandedWidth = newNav.getMeasuredWidth();
        int newCollapsedWidth = newNav.getWidth(); // Chiều rộng hiện tại (chỉ icon)

        // === 1. Animate tab cũ: thu nhỏ (expanded -> collapsed) ===
        ValueAnimator collapseAnim = ValueAnimator.ofInt(oldExpandedWidth, oldCollapsedWidth);
        collapseAnim.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = oldNav.getLayoutParams();
            params.width = value;
            oldNav.setLayoutParams(params);

            // Fade out label theo tiến trình thu nhỏ
            float fraction = animation.getAnimatedFraction();
            oldLabel.setAlpha(1f - fraction);
        });

        // === 2. Animate tab mới: mở rộng (collapsed -> expanded) ===
        ValueAnimator expandAnim = ValueAnimator.ofInt(newCollapsedWidth, newExpandedWidth);
        expandAnim.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = newNav.getLayoutParams();
            params.width = value;
            newNav.setLayoutParams(params);

            // Fade in label theo tiến trình mở rộng
            float fraction = animation.getAnimatedFraction();
            newLabel.setAlpha(fraction);
        });

        // === Đổi màu icon + nền ngay khi bắt đầu ===
        oldIcon.setColorFilter(ContextCompat.getColor(activity, R.color.white));
        oldNav.setBackgroundResource(0);
        newIcon.setColorFilter(ContextCompat.getColor(activity, R.color.text_primary));
        newNav.setBackgroundResource(R.drawable.bg_nav_item_selected);

        // === Chạy 2 animation đồng thời ===
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(collapseAnim, expandAnim);
        animatorSet.setDuration(ANIM_DURATION);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                // Dọn dẹp sau khi animation xong
                oldLabel.setVisibility(View.GONE);
                oldLabel.setAlpha(1f);

                // Reset width về wrap_content để layout tự nhiên
                ViewGroup.LayoutParams oldParams = oldNav.getLayoutParams();
                oldParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                oldNav.setLayoutParams(oldParams);

                ViewGroup.LayoutParams newParams = newNav.getLayoutParams();
                newParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                newNav.setLayoutParams(newParams);

                newLabel.setAlpha(1f);
                isAnimating = false;

                // Chuyển màn hình
                navigateTo(tab);
            }
        });
        animatorSet.start();

        currentSelected = newNav;
        currentTab = tab;
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
        // Hiệu ứng fade khi chuyển màn hình
        activity.overridePendingTransition(R.anim.nav_fade_in, R.anim.nav_fade_out);
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
