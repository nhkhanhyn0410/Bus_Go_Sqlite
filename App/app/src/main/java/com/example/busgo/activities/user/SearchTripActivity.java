package com.example.busgo.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busgo.R;
import com.example.busgo.adapters.TripAdapter;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.DAO.TripDAO;
import com.example.busgo.database.model.Bus;
import com.example.busgo.database.model.Trip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchTripActivity extends AppCompatActivity {

    private ImageView btnBack, btnRefresh, btnSwapRoute;
    private TextView tvDepartureCity, tvDestinationCity;

    private LinearLayout layoutDateTabs;
    private TextView[] tvDates = new TextView[7];
    private View[] indicatorDates = new View[7];
    private int selectedDateIndex = 0;

    private RecyclerView recyclerViewTrips;
    private LinearLayout layoutEmpty;
    private FrameLayout layoutLoading;

    private LinearLayout filterWifi, filterWC, filterBed, filterCharging, layoutFilterBar;
    private ImageView btnFilter;
    private boolean isWifiSelected = false;
    private boolean isWCSelected = false;
    private boolean isBedSelected = false;
    private boolean isChargingSelected = false;

    private TripDAO tripDAO;
    private TripAdapter tripAdapter;
    private List<Trip> tripList;
    private List<Trip> allTrips; // Lưu tất cả kết quả để filter

    private String departure, destination, date;
    private List<String> dateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_trip);
        EdgeToEdge.enable(this);


        initViews();

        getSearchParameters();

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        tripDAO = new TripDAO(dbHelper);

        setupDateTabs();

        setupRecyclerView();

        setupFilterBar();

        displayRouteInfo();

        loadTrips();

        setupClickListeners();
    }


    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnSwapRoute = findViewById(R.id.btnSwapRoute);
        tvDepartureCity = findViewById(R.id.tvDepartureCity);
        tvDestinationCity = findViewById(R.id.tvDestinationCity);

        tvDates[0] = findViewById(R.id.tvDate1);
        tvDates[1] = findViewById(R.id.tvDate2);
        tvDates[2] = findViewById(R.id.tvDate3);
        tvDates[3] = findViewById(R.id.tvDate4);
        tvDates[4] = findViewById(R.id.tvDate5);
        tvDates[5] = findViewById(R.id.tvDate6);
        tvDates[6] = findViewById(R.id.tvDate7);
        indicatorDates[0] = findViewById(R.id.indicatorDate1);
        indicatorDates[1] = findViewById(R.id.indicatorDate2);
        indicatorDates[2] = findViewById(R.id.indicatorDate3);
        indicatorDates[3] = findViewById(R.id.indicatorDate4);
        indicatorDates[4] = findViewById(R.id.indicatorDate5);
        indicatorDates[5] = findViewById(R.id.indicatorDate6);
        indicatorDates[6] = findViewById(R.id.indicatorDate7);

        recyclerViewTrips = findViewById(R.id.recyclerViewTrips);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        layoutLoading = findViewById(R.id.layoutLoading);

        filterWifi = findViewById(R.id.filterWifi);
        filterWC = findViewById(R.id.filterWC);
        filterBed = findViewById(R.id.filterBed);
        filterCharging = findViewById(R.id.filterCharging);
        btnFilter = findViewById(R.id.btnFilter);
        layoutFilterBar = findViewById(R.id.layoutFilterBar);
    }

    private void getSearchParameters() {
        departure = getIntent().getStringExtra("departure");
        destination = getIntent().getStringExtra("destination");
        date = getIntent().getStringExtra("date");

        if (departure == null) departure = "TP.Hồ Chí Minh";
        if (destination == null) destination = "Đà Lạt";
        if (date == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            date = sdf.format(new Date());
        }
    }

    private void setupDateTabs() {
        try {
            dateList.clear();

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());

            Date startDate = inputFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);

            for (int i = 0; i < 7; i++) {
                if (i > 0) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
                String dateStr = inputFormat.format(calendar.getTime());
                String dayStr = "Ngày " + dayFormat.format(calendar.getTime());
                dateList.add(dateStr);
                tvDates[i].setText(dayStr);
                final int index = i;
                tvDates[i].setOnClickListener(v -> selectDate(index));
            }
            selectDate(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectDate(int index) {
        selectedDateIndex = index;

        for (int i = 0; i < 7; i++) {
            if (i == index) {
                tvDates[i].setTextColor(ContextCompat.getColor(this, R.color.white));
                indicatorDates[i].setVisibility(View.VISIBLE);
            } else {
                tvDates[i].setTextColor(ContextCompat.getColor(this, R.color.text_on_dark_dimmed));
                indicatorDates[i].setVisibility(View.INVISIBLE);
            }
        }

        if (!dateList.isEmpty() && index < dateList.size()) {
            date = dateList.get(index);
            loadTrips();
        }
    }

    private void setupRecyclerView() {
        tripList = new ArrayList<>();
        allTrips = new ArrayList<>();
        tripAdapter = new TripAdapter(tripList);

        recyclerViewTrips.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrips.setAdapter(tripAdapter);

        tripAdapter.setOnTripClickListener(trip -> {
            Intent intent = new Intent(SearchTripActivity.this, TripDetailActivity.class);
            intent.putExtra("trip_id", trip.getId());
            startActivity(intent);
        });
    }

    private void setupFilterBar() {
        final int originalBottomMargin = ((ViewGroup.MarginLayoutParams) layoutFilterBar.getLayoutParams()).bottomMargin;

        ViewCompat.setOnApplyWindowInsetsListener(layoutFilterBar, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.bottomMargin = originalBottomMargin + insets.bottom;
            view.setLayoutParams(params);
            return windowInsets;
        });
        filterWifi.setOnClickListener(v -> {
            isWifiSelected = !isWifiSelected;
            updateFilterUI(filterWifi, isWifiSelected);
            applyFilters();
        });

        filterWC.setOnClickListener(v -> {
            isWCSelected = !isWCSelected;
            updateFilterUI(filterWC, isWCSelected);
            applyFilters();
        });

        filterBed.setOnClickListener(v -> {
            isBedSelected = !isBedSelected;
            updateFilterUI(filterBed, isBedSelected);
            applyFilters();
        });

        filterCharging.setOnClickListener(v -> {
            isChargingSelected = !isChargingSelected;
            updateFilterUI(filterCharging, isChargingSelected);
            applyFilters();
        });

        btnFilter.setOnClickListener(v -> {
            // TODO: Mở dialog bộ lọc nâng cao
        });
    }


    private void updateFilterUI(LinearLayout filterView, boolean isSelected) {
        if (isSelected) {
            filterView.setBackgroundResource(R.drawable.bg_filter_item_selected);
        } else {
            filterView.setBackground(null);
        }
    }

    private void applyFilters() {
        if (allTrips == null || allTrips.isEmpty()) return;

        List<Trip> filteredTrips = new ArrayList<>();

        for (Trip trip : allTrips) {
            Bus bus = trip.getBus();
            if (bus == null) continue;

            boolean matches = true;

            if (isWifiSelected && !bus.hasAmenity(Bus.AMENITY_WIFI)) {
                matches = false;
            }
            if (isWCSelected && !bus.hasAmenity(Bus.AMENITY_WC)) {
                matches = false;
            }
            if (isChargingSelected && !bus.hasAmenity(Bus.AMENITY_CHARGING)) {
                matches = false;
            }

            if (isBedSelected) {
                String busType = bus.getBusType();
                if (busType == null || !busType.toLowerCase().contains("giường")) {
                    matches = false;
                }
            }

            if (matches) {
                filteredTrips.add(trip);
            }
        }

        tripList.clear();
        tripList.addAll(filteredTrips);
        tripAdapter.notifyDataSetChanged();

        if (filteredTrips.isEmpty()) {
            recyclerViewTrips.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerViewTrips.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }


    private void displayRouteInfo() {
        tvDepartureCity.setText(departure);
        tvDestinationCity.setText(destination);
    }


    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnRefresh.setOnClickListener(v -> {
            isWifiSelected = false;
            isWCSelected = false;
            isBedSelected = false;
            isChargingSelected = false;

            updateFilterUI(filterWifi, false);
            updateFilterUI(filterWC, false);
            updateFilterUI(filterBed, false);
            updateFilterUI(filterCharging, false);

            loadTrips();
        });

        btnSwapRoute.setOnClickListener(v -> {
            String temp = departure;
            departure = destination;
            destination = temp;

            displayRouteInfo();
            loadTrips();
        });
    }


    private void loadTrips() {
        showLoading(true);
        new Thread(() -> {
            List<Trip> trips = tripDAO.searchTrips(departure, destination, date);

            runOnUiThread(() -> {
                showLoading(false);

                if (trips != null && !trips.isEmpty()) {
                    allTrips.clear();
                    allTrips.addAll(trips);
                    tripList.clear();
                    tripList.addAll(trips);
                    tripAdapter.notifyDataSetChanged();
                    recyclerViewTrips.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);

                    if (isWifiSelected || isWCSelected || isBedSelected || isChargingSelected) {
                        applyFilters();
                    }
                } else {

                    allTrips.clear();
                    tripList.clear();
                    tripAdapter.notifyDataSetChanged();

                    recyclerViewTrips.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }


    private void showLoading(boolean show) {
        if (show) {
            layoutLoading.setVisibility(View.VISIBLE);
            recyclerViewTrips.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
        } else {
            layoutLoading.setVisibility(View.GONE);
        }
    }
}