package com.example.busgo.activities.user;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busgo.R;
import com.example.busgo.adapters.BookingAdapter;
import com.example.busgo.database.DAO.BookingDAO;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.Booking;
import com.example.busgo.until.BottomNavHelper;
import com.example.busgo.until.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {

    private static final String TAB_ACTIVE = "active";
    private static final String TAB_COMPLETED = "completed";
    private static final String TAB_CANCELLED = "cancelled";

    private TextView tabActive;
    private TextView tabCompleted;
    private TextView tabCancelled;
    private TextView tvEmpty;

    private final List<Booking> allBookings = new ArrayList<>();
    private BookingAdapter bookingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_history);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);

        setupViews();
        loadBookingData();
        selectTab(TAB_COMPLETED);

        BottomNavHelper.setup(this, BottomNavHelper.TAB_BOOKINGS);
    }

    private void setupViews() {
        tabActive = findViewById(R.id.tabActive);
        tabCompleted = findViewById(R.id.tabCompleted);
        tabCancelled = findViewById(R.id.tabCancelled);
        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rvBookings = findViewById(R.id.rvBookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        bookingAdapter = new BookingAdapter();
        rvBookings.setAdapter(bookingAdapter);

        tabActive.setOnClickListener(v -> selectTab(TAB_ACTIVE));
        tabCompleted.setOnClickListener(v -> selectTab(TAB_COMPLETED));
        tabCancelled.setOnClickListener(v -> selectTab(TAB_CANCELLED));
    }

    private void loadBookingData() {
        int userId = SessionManager.getInstance(this).getLoggedInUserId();
        if (userId <= 0) {
            return;
        }

        BookingDAO bookingDAO = new BookingDAO(new DatabaseHelper(this));
        allBookings.clear();
        allBookings.addAll(bookingDAO.getBookingsByUserId(userId));
    }

    private void selectTab(@NonNull String tab) {
        setTabState(tabActive, TAB_ACTIVE.equals(tab));
        setTabState(tabCompleted, TAB_COMPLETED.equals(tab));
        setTabState(tabCancelled, TAB_CANCELLED.equals(tab));

        List<Booking> filtered = filterByTab(tab);
        bookingAdapter.submitList(filtered);
        tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void setTabState(TextView tab, boolean selected) {
        tab.setBackgroundResource(selected ? R.drawable.bg_chip_booking_selected : R.drawable.bg_chip_booking_unselected);
        tab.setTextColor(getColor(selected ? R.color.white : R.color.text_primary));
    }

    private List<Booking> filterByTab(String tab) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : allBookings) {
            String status = booking.getBookingStatus();
            if (TAB_ACTIVE.equals(tab) && isActiveStatus(status)) {
                result.add(booking);
            } else if (TAB_COMPLETED.equals(tab) && isCompletedStatus(status)) {
                result.add(booking);
            } else if (TAB_CANCELLED.equals(tab) && "cancelled".equalsIgnoreCase(status)) {
                result.add(booking);
            }
        }
        return result;
    }

    private boolean isActiveStatus(String status) {
        return "pending".equalsIgnoreCase(status) || "processing".equalsIgnoreCase(status);
    }

    private boolean isCompletedStatus(String status) {
        return "confirmed".equalsIgnoreCase(status) || "completed".equalsIgnoreCase(status);
    }
}
