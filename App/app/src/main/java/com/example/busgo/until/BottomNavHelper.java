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

    public static final String TAB_HOME = "home";
    public static final String TAB_BOOKINGS = "bookings";
    public static final String TAB_PROFILE = "profile";

    private static final int ANIM_DURATION = 280;

    // Views
    private final Activity activity;
    private final LinearLayout navHome, navBookings, navProfile;
    private final ImageView navHomeIcon, navBookingsIcon, navProfileIcon;
    private final TextView navHomeLabel, navBookingsLabel, navProfileLabel;
    private LinearLayout currentSelected;
    private String currentTab;
    private View bottomNavBar;

    private boolean isAnimating = false;

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


    private void initSelectedState(String tab) {
        setUnselected(navHome, navHomeIcon, navHomeLabel);
        setUnselected(navBookings, navBookingsIcon, navBookingsLabel);
        setUnselected(navProfile, navProfileIcon, navProfileLabel);

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
        if (tab.equals(currentTab)) return;
        if (isAnimating) return;

        LinearLayout oldNav = currentSelected;
        ImageView oldIcon = getIconByTab(currentTab);
        TextView oldLabel = getLabelByTab(currentTab);

        LinearLayout newNav = getNavByTab(tab);
        ImageView newIcon = getIconByTab(tab);
        TextView newLabel = getLabelByTab(tab);

        isAnimating = true;
        int oldExpandedWidth = oldNav.getWidth();
        int oldCollapsedWidth = oldNav.getPaddingLeft() + oldIcon.getWidth() + oldNav.getPaddingRight();

        newLabel.setVisibility(View.VISIBLE);
        newLabel.setAlpha(0f);
        newNav.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(newNav.getHeight(), View.MeasureSpec.EXACTLY)
        );
        int newExpandedWidth = newNav.getMeasuredWidth();
        int newCollapsedWidth = newNav.getWidth();

        ValueAnimator collapseAnim = ValueAnimator.ofInt(oldExpandedWidth, oldCollapsedWidth);
        collapseAnim.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = oldNav.getLayoutParams();
            params.width = value;
            oldNav.setLayoutParams(params);

            float fraction = animation.getAnimatedFraction();
            oldLabel.setAlpha(1f - fraction);
        });

        ValueAnimator expandAnim = ValueAnimator.ofInt(newCollapsedWidth, newExpandedWidth);
        expandAnim.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = newNav.getLayoutParams();
            params.width = value;
            newNav.setLayoutParams(params);

            float fraction = animation.getAnimatedFraction();
            newLabel.setAlpha(fraction);
        });

        oldIcon.setColorFilter(ContextCompat.getColor(activity, R.color.white));
        oldNav.setBackgroundResource(0);
        newIcon.setColorFilter(ContextCompat.getColor(activity, R.color.text_primary));
        newNav.setBackgroundResource(R.drawable.bg_nav_item_selected);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(collapseAnim, expandAnim);
        animatorSet.setDuration(ANIM_DURATION);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                oldLabel.setVisibility(View.GONE);
                oldLabel.setAlpha(1f);

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
