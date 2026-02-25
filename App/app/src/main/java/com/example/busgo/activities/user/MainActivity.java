package com.example.busgo.activities.user;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busgo.R;
import com.example.busgo.adapters.PopularRouteAdapter;
import com.example.busgo.adapters.RecentSearchAdapter;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.until.BottomNavHelper;
import com.example.busgo.until.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Search form views
    private LinearLayout layoutDeparture, layoutDestination, layoutDate;
    private TextView tvDeparture, tvDestination, tvDate;
    private ImageView btnSwapLocations;
    private TextView btnSearchTrip;

    // RecyclerViews
    private RecyclerView rvPopularTrips, rvRecentSearches, rvOtherTrips;

    // Adapters
    private PopularRouteAdapter popularRouteAdapter;
    private RecentSearchAdapter recentSearchAdapter;
    private PopularRouteAdapter otherTripsAdapter;

    // Data
    private String selectedDeparture = "";
    private String selectedDestination = "";
    private String selectedDate = "";
    private Calendar selectedCalendar = Calendar.getInstance();

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sessionManager = SessionManager.getInstance(this);

        // Initialize views
        initViews();

        // Setup RecyclerViews


        // Set click listeners
        setupClickListeners();

        // Setup bottom navigation bar (dùng chung BottomNavHelper)
        BottomNavHelper.setup(this, BottomNavHelper.TAB_HOME);

    }

    private void initViews() {
        // Search form
        layoutDeparture = findViewById(R.id.layoutDeparture);
        layoutDestination = findViewById(R.id.layoutDestination);
        layoutDate = findViewById(R.id.layoutDate);
        tvDeparture = findViewById(R.id.tvDeparture);
        tvDestination = findViewById(R.id.tvDestination);
        tvDate = findViewById(R.id.tvDate);
        btnSwapLocations = findViewById(R.id.btnSwapLocations);
        btnSearchTrip = findViewById(R.id.btnSearchTrip);

        // RecyclerViews
        rvPopularTrips = findViewById(R.id.rvPopularTrips);
        rvRecentSearches = findViewById(R.id.rvRecentSearches);
        rvOtherTrips = findViewById(R.id.rvOtherTrips);
    }




    /**
     * Setup click listeners
     */
    private void setupClickListeners() {
        // Search form clicks
        layoutDeparture.setOnClickListener(v -> showDepartureDialog());
        layoutDestination.setOnClickListener(v -> showDestinationDialog());
        layoutDate.setOnClickListener(v -> showDatePicker());

        // Swap locations
        btnSwapLocations.setOnClickListener(v -> swapLocations());

        // Search button
        btnSearchTrip.setOnClickListener(v -> performSearch());
    }

    /**
     * Hiển thị dialog chọn điểm đi
     */
    private void showDepartureDialog() {
        String[] cities = {"TP.HCM", "Hà Nội", "Đà Nẵng", "Cần Thơ", "Nha Trang", "Đà Lạt", "Vũng Tàu", "Huế"};

        new android.app.AlertDialog.Builder(this)
                .setTitle("Chọn điểm bắt đầu")
                .setItems(cities, (dialog, which) -> {
                    selectedDeparture = cities[which];
                    tvDeparture.setText(selectedDeparture);
                    tvDeparture.setTextColor(getResources().getColor(R.color.text_primary));
                })
                .show();
    }

    /**
     * Hiển thị dialog chọn điểm đến
     */
    private void showDestinationDialog() {
        String[] cities = {"TP.HCM", "Hà Nội", "Đà Nẵng", "Cần Thơ", "Nha Trang", "Đà Lạt", "Vũng Tàu", "Huế"};

        new android.app.AlertDialog.Builder(this)
                .setTitle("Chọn điểm kết thúc")
                .setItems(cities, (dialog, which) -> {
                    selectedDestination = cities[which];
                    tvDestination.setText(selectedDestination);
                    tvDestination.setTextColor(getResources().getColor(R.color.text_primary));
                })
                .show();
    }

    /**
     * Hiển thị DatePicker chọn ngày
     */
    private void showDatePicker() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    selectedDate = sdf.format(selectedCalendar.getTime());
                    tvDate.setText(selectedDate);
                    tvDate.setTextColor(getResources().getColor(R.color.text_primary));
                }, year, month, day);

        // Không cho chọn ngày trong quá khứ
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    /**
     * Hoán đổi điểm đi và điểm đến
     */
    private void swapLocations() {
        String temp = selectedDeparture;
        selectedDeparture = selectedDestination;
        selectedDestination = temp;

        if (!selectedDeparture.isEmpty()) {
            tvDeparture.setText(selectedDeparture);
            tvDeparture.setTextColor(getResources().getColor(R.color.text_primary));
        } else {
            tvDeparture.setText("");
            tvDeparture.setHint("Điểm bắt đầu");
        }

        if (!selectedDestination.isEmpty()) {
            tvDestination.setText(selectedDestination);
            tvDestination.setTextColor(getResources().getColor(R.color.text_primary));
        } else {
            tvDestination.setText("");
            tvDestination.setHint("Điểm kết thúc");
        }
    }

    /**
     * Thực hiện tìm kiếm chuyến xe
     */
    private void performSearch() {
        // Validate
        if (selectedDeparture.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn điểm bắt đầu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDestination.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn điểm kết thúc", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDeparture.equals(selectedDestination)) {
            Toast.makeText(this, "Điểm đi và điểm đến không được trùng nhau", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày di chuyển", Toast.LENGTH_SHORT).show();
            return;
        }

        // Navigate to SearchTripActivity hoặc TripListActivity
        Intent intent = new Intent(MainActivity.this, TripListActivity.class);
        intent.putExtra("departure", selectedDeparture);
        intent.putExtra("destination", selectedDestination);
        intent.putExtra("date", selectedDate);
        startActivity(intent);
    }

    /**
     * Xử lý khi click vào popular route
     */


    /**
     * Xử lý khi click vào recent search
     */



}