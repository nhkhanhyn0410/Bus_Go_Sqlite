package com.example.busgo.activities.user;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busgo.R;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.until.BottomNavHelper;
import com.example.busgo.until.SessionManager;
import com.example.busgo.database.DAO.TripDAO;
import com.example.busgo.activities.user.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Search form views
    private LinearLayout layoutDate;
    private AutoCompleteTextView tvDeparture, tvDestination;
    private TextView tvDate;
    private ImageView btnSwapLocations;
    private TextView btnSearchTrip;

    // Database
    private TripDAO tripDAO;

    // RecyclerViews
    private RecyclerView rvPopularTrips, rvRecentSearches, rvOtherTrips;

    // Adapters
//    private PopularRouteAdapter popularRouteAdapter;
//    private RecentSearchAdapter recentSearchAdapter;
//    private PopularRouteAdapter otherTripsAdapter;

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

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);

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
        layoutDate = findViewById(R.id.layoutDate);
        tvDeparture = findViewById(R.id.tvDeparture);
        tvDestination = findViewById(R.id.tvDestination);
        tvDate = findViewById(R.id.tvDate);
        btnSwapLocations = findViewById(R.id.btnSwapLocations);
        btnSearchTrip = findViewById(R.id.btnSearchTrip);

        // Database
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        tripDAO = new TripDAO(dbHelper);

        // RecyclerViews
        rvPopularTrips = findViewById(R.id.rvPopularTrips);
        rvRecentSearches = findViewById(R.id.rvRecentSearches);
        rvOtherTrips = findViewById(R.id.rvOtherTrips);
    }

    private void setupClickListeners() {
        // Autocomplete cho điểm đi: load từ DB
        setupDepartureAutocomplete();

        // Khi chọn điểm đi xong → cập nhật gợi ý điểm đến
        tvDeparture.setOnItemClickListener((parent, view, position, id) -> {
            selectedDeparture = (String) parent.getItemAtPosition(position);
            setupDestinationAutocomplete(selectedDeparture);
            tvDestination.setText("");
            selectedDestination = "";
            tvDestination.requestFocus();
        });

        // Khi chọn điểm đến
        tvDestination.setOnItemClickListener((parent, view, position, id) -> {
            selectedDestination = (String) parent.getItemAtPosition(position);
        });

        // Date picker
        layoutDate.setOnClickListener(v -> showDatePicker());

        // Swap locations
        btnSwapLocations.setOnClickListener(v -> swapLocations());

        // Search button
        btnSearchTrip.setOnClickListener(v -> performSearch());
    }


    private void setupDepartureAutocomplete() {
        List<String> departures = tripDAO.getAllDepartures();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, departures);
        tvDeparture.setAdapter(adapter);
    }

    private void setupDestinationAutocomplete(String departure) {
        List<String> destinations = tripDAO.getDestinationsByDeparture(departure);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, destinations);
        tvDestination.setAdapter(adapter);
    }

    private void showDatePicker() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    selectedDate = sqlFormat.format(selectedCalendar.getTime());


                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    tvDate.setText(displayFormat.format(selectedCalendar.getTime()));
                    tvDate.setTextColor(getResources().getColor(R.color.text_primary));
                }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }


    private void swapLocations() {
        String temp = selectedDeparture;
        selectedDeparture = selectedDestination;
        selectedDestination = temp;

        tvDeparture.setText(selectedDeparture);
        tvDestination.setText(selectedDestination);

        if (!selectedDeparture.isEmpty()) {
            setupDestinationAutocomplete(selectedDeparture);
        }
    }


    private void performSearch() {
        // Lấy text từ ô nhập (có thể user gõ tay không chọn từ gợi ý)
        selectedDeparture = tvDeparture.getText().toString().trim();
        selectedDestination = tvDestination.getText().toString().trim();

        // Validate
        if (selectedDeparture.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập điểm bắt đầu", Toast.LENGTH_SHORT).show();
            tvDeparture.requestFocus();
            return;
        }

        if (selectedDestination.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập điểm kết thúc", Toast.LENGTH_SHORT).show();
            tvDestination.requestFocus();
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

        // Navigate to TripListActivity
        Intent intent = new Intent(MainActivity.this, SearchTripActivity.class);
        intent.putExtra("departure", selectedDeparture);
        intent.putExtra("destination", selectedDestination);
        intent.putExtra("date", selectedDate);
        startActivity(intent);
    }

}