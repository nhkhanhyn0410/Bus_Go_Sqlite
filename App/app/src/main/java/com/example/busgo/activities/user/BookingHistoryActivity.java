package com.example.busgo.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.busgo.database.model.Route;
import com.example.busgo.database.model.StopPoint;
import com.example.busgo.database.model.Trip;
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

    private TextView tabActive, tabCompleted, tabCancelled;
    private TextView currentSelectedTab;

    // Content
    private RecyclerView recyclerView;
    private LinearLayout layoutEmpty;
    private BookingAdapter adapter;

    // Data
    private DatabaseHelper dbHelper;
    private BookingDAO bookingDAO;
    private SessionManager sessionManager;
    private List<Booking> allBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_history);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);

        initViews();
        initDatabase();
        loadBookings();
        setupTabs();

        BottomNavHelper.setup(this, BottomNavHelper.TAB_BOOKINGS);
    }

    private void initViews() {
        // Tabs
        tabActive = findViewById(R.id.tabActive);
        tabCompleted = findViewById(R.id.tabCompleted);
        tabCancelled = findViewById(R.id.tabCancelled);
        currentSelectedTab = tabActive;

        // Content
        recyclerView = findViewById(R.id.recyclerView);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sessionManager = SessionManager.getInstance(this);
    }

    private void initDatabase() {
        dbHelper = DatabaseHelper.getInstance(this);
        bookingDAO = new BookingDAO(dbHelper);
    }

    private void loadBookings() {
        int userId = sessionManager.getLoggedInUserId();
        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        allBookings = bookingDAO.getBookingsByUserId(userId);
        if (allBookings == null) allBookings = new ArrayList<>();

        adapter = new BookingAdapter(this, allBookings);
        recyclerView.setAdapter(adapter);
    }

    // ==================== FILTER TABS ====================

    private void setupTabs() {
        tabActive.setOnClickListener(v -> {
            selectTab(tabActive);
            filterBookings("confirmed");
        });

        tabCompleted.setOnClickListener(v -> {
            selectTab(tabCompleted);
            filterBookings("completed");
        });

        tabCancelled.setOnClickListener(v -> {
            selectTab(tabCancelled);
            filterBookings("cancelled");
        });
    }

    /**
     * Đổi trạng thái tab: selected/unselected
     */
    private void selectTab(TextView tab) {
        // Bỏ chọn tab cũ
        currentSelectedTab.setBackgroundResource(R.drawable.bg_chip_unselected);
        currentSelectedTab.setTextColor(getResources().getColor(R.color.text_primary));

        // Chọn tab mới
        tab.setBackgroundResource(R.drawable.bg_chip_selected);
        tab.setTextColor(getResources().getColor(R.color.white));

        currentSelectedTab = tab;
    }

    /**
     * Lọc booking theo trạng thái
     */
    private void filterBookings(String status) {
        List<Booking> filtered = new ArrayList<>();
        for (Booking b : allBookings) {
            if (status.equals(b.getBookingStatus())) {
                filtered.add(b);
            }
        }

        adapter.updateData(filtered);


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
        if (currentSelectedTab == tabCancelled) {
            filterBookings("cancelled");
        } else if (currentSelectedTab == tabCompleted) {
            filterBookings("completed");
        } else {
            filterBookings("confirmed");
        }
    }

    @Override
    public void onBackPressed() {
        // Quay về trang chủ
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
