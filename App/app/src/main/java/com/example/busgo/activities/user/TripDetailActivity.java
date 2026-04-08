package com.example.busgo.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.busgo.R;
import com.example.busgo.database.DAO.StopPointDAO;
import com.example.busgo.database.DAO.TripDAO;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.Bus;
import com.example.busgo.database.model.Route;
import com.example.busgo.database.model.StopPoint;
import com.example.busgo.database.model.Trip;
import com.example.busgo.until.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class TripDetailActivity extends AppCompatActivity {

    private static final int REQUEST_CHANGE_STOPS = 100;

    // Header
    private ImageView btnBack;
    private TextView tvCompanyName, tvDepartureTime, tvArrivalTime;
    private TextView tvDepartureStation, tvArrivalStation;
    private TextView tvDuration, tvDate, tvRating;

    // Content
    private LinearLayout timelineContainer;
    private LinearLayout amenitiesContainer;
    private TextView btnChangeStops;
    private Button btnSelectSeats;

    // Data
    private TripDAO tripDAO;
    private StopPointDAO stopPointDAO;
    private Trip trip;
    private int tripId;

    private StopPoint selectedPickup;
    private StopPoint selectedDropoff;
    private List<StopPoint> restStops;
    private int selectedPickupIndex = 0;
    private int selectedDropoffIndex = 0;
    private float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trip_detail);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);

        density = getResources().getDisplayMetrics().density;

        initViews();

        tripId = getIntent().getIntExtra("trip_id", -1);
        if (tripId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy chuyến xe", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        tripDAO = new TripDAO(dbHelper);
        stopPointDAO = new StopPointDAO(dbHelper);

        loadTripDetails();

        btnBack.setOnClickListener(v -> finish());
        btnChangeStops.setOnClickListener(v -> handleChangeStops());
        btnSelectSeats.setOnClickListener(v -> handleSelectSeats());
    }

    private void initViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);
        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvDepartureTime = findViewById(R.id.tvDepartureTime);
        tvArrivalTime = findViewById(R.id.tvArrivalTime);
        tvDepartureStation = findViewById(R.id.tvDepartureStation);
        tvArrivalStation = findViewById(R.id.tvArrivalStation);
        tvDuration = findViewById(R.id.tvDuration);
        tvDate = findViewById(R.id.tvDate);
        tvRating = findViewById(R.id.tvRating);

        // Content
        timelineContainer = findViewById(R.id.timelineContainer);
        amenitiesContainer = findViewById(R.id.amenitiesContainer);
        btnChangeStops = findViewById(R.id.btnChangeStops);
        btnSelectSeats = findViewById(R.id.btnSelectSeats);

        final int originalbtnSelectSeats = ((ViewGroup.MarginLayoutParams) btnSelectSeats.getLayoutParams()).bottomMargin;
        ViewCompat.setOnApplyWindowInsetsListener(btnSelectSeats, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.bottomMargin = originalbtnSelectSeats + insets.bottom;
            view.setLayoutParams(params);
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(btnSelectSeats);
    }


    private void loadTripDetails() {
        new Thread(() -> {
            trip = tripDAO.getTripById(tripId);

            List<StopPoint> pickupPoints = stopPointDAO.getPickupPointsByRouteId(trip.getRouteId());
            List<StopPoint> dropoffPoints = stopPointDAO.getDropoffPointsByRouteId(trip.getRouteId());
            restStops = stopPointDAO.getRestStopsByRouteId(trip.getRouteId());

            if (pickupPoints != null && !pickupPoints.isEmpty()) {
                selectedPickup = pickupPoints.get(0);
            }
            if (dropoffPoints != null && !dropoffPoints.isEmpty()) {
                selectedDropoff = dropoffPoints.get(0);
            }

            runOnUiThread(() -> {
                displayHeader();
                displayTimeline();
                displayAmenities();

                if (trip.getAvailableSeats() <= 0) {
                    btnSelectSeats.setEnabled(false);
                    btnSelectSeats.setText("Hết vé");
                }
            });
        }).start();
    }


    private void displayHeader() {
        Bus bus = trip.getBus();
        Route route = trip.getRoute();

        if (bus != null && bus.getCompanyName() != null) {
            tvCompanyName.setText(bus.getCompanyName());
        } else {
            tvCompanyName.setText("Nhà xe");
        }

        tvDepartureTime.setText(formatTimeOnly(trip.getDepartureTime()));
        tvArrivalTime.setText(formatTimeOnly(trip.getArrivalTime()));

        if (route != null && route.getDuration() > 0) {
            int hours = route.getDuration() / 60;
            int mins = route.getDuration() % 60;
            String dur = mins > 0 ? hours + "h " + mins + "'" : hours + " giờ";
            tvDuration.setText(dur);
        }

        if (route != null) {
            tvDepartureStation.setText(route.getDeparture());
            tvArrivalStation.setText(route.getDestination());
        }

        tvDate.setText(DateUtils.formatDate(trip.getDepartureTime()));

        if (bus != null) {
            tvRating.setText(String.valueOf(bus.getRating()));
        }
    }


    private String formatTimeOnly(String dateTime) {
        if (dateTime == null || dateTime.length() < 16) return "--:--";
        try {
            String time = dateTime.substring(11, 16);
            return time.replace(":", ".");
        } catch (Exception e) {
            return "--:--";
        }
    }


    private void displayTimeline() {
        timelineContainer.removeAllViews();

        List<StopPoint> timelineStops = new ArrayList<>();
        if (selectedPickup != null) timelineStops.add(selectedPickup);
        if (restStops != null) timelineStops.addAll(restStops);
        if (selectedDropoff != null) timelineStops.add(selectedDropoff);

        if (timelineStops.isEmpty()) return;

        for (int i = 0; i < timelineStops.size(); i++) {
            StopPoint stop = timelineStops.get(i);

            if (i > 0) {
                timelineContainer.addView(createConnectorLine());
            }

            View itemView = createTimelineItem(stop);
            timelineContainer.addView(itemView);
        }
    }


    private View createConnectorLine() {
        return LayoutInflater.from(this)
                .inflate(R.layout.item_timeline_connector, timelineContainer, false);
    }


    private View createTimelineItem(StopPoint stop) {
        String type = stop.getPointType();
        String actualTime = calculateActualTime(stop);
        String info = actualTime + ", " + stop.getPointName();

        View itemView;
        if ("pickup".equals(type)) {
            itemView = LayoutInflater.from(this)
                    .inflate(R.layout.item_timeline_pickup, timelineContainer, false);
        } else if ("dropoff".equals(type)) {
            itemView = LayoutInflater.from(this)
                    .inflate(R.layout.item_timeline_dropoff, timelineContainer, false);
        } else {
            itemView = LayoutInflater.from(this)
                    .inflate(R.layout.item_timeline_rest, timelineContainer, false);
        }

        TextView tvInfo = itemView.findViewById(R.id.tvInfo);
        if (tvInfo != null) {
            tvInfo.setText(info);
        }

        return itemView;
    }


    private String calculateActualTime(StopPoint stop) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm", Locale.getDefault());

            String baseTime;
            if ("dropoff".equals(stop.getPointType())) {
                baseTime = trip.getArrivalTime();
            } else {
                baseTime = trip.getDepartureTime();
            }

            if (baseTime == null) return "--:--";

            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(baseTime));
            cal.add(Calendar.MINUTE, stop.getTimeOffset());

            return timeFormat.format(cal.getTime());
        } catch (Exception e) {
            return "--:--";
        }
    }


    private void displayAmenities() {
        amenitiesContainer.removeAllViews();

        Bus bus = trip.getBus();
        if (bus == null) return;

        // Thu thập tiện ích
        List<int[]> amenities = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        if (bus.hasWifi()) {
            amenities.add(new int[]{R.drawable.ic_wifi});
            labels.add("Wifi miễn phí");
        }
        if (bus.hasTV()) {
            amenities.add(new int[]{R.drawable.ic_bus});
            labels.add("Giải trí");
        }
        if (bus.hasWC()) {
            amenities.add(new int[]{R.drawable.ic_wc});
            labels.add("WC");
        }
        if (bus.hasCharging()) {
            amenities.add(new int[]{R.drawable.ic_charging});
            labels.add("Sạc điện");
        }

        String busType = bus.getBusType();
        if (busType != null) {
            if (busType.contains("Giường")) {
                amenities.add(new int[]{R.drawable.ic_bed});
                labels.add("Giường nằm");
            } else {
                amenities.add(new int[]{R.drawable.ic_bus});
                labels.add("Ghế ngồi");
            }
        }


        LinearLayout currentRow = createAmenityRow();
        amenitiesContainer.addView(currentRow);
        int itemsInRow = 0;

        for (int i = 0; i < amenities.size(); i++) {
            if (itemsInRow >= 3) {
                currentRow = createAmenityRow();
                amenitiesContainer.addView(currentRow);
                itemsInRow = 0;
            }

            View item = createAmenityItem(amenities.get(i)[0], labels.get(i));
            currentRow.addView(item);
            itemsInRow++;
        }
    }

    private LinearLayout createAmenityRow() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return row;
    }


    private View createAmenityItem(int iconRes, String label) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        itemParams.setMarginEnd((int) (20 * density));
        itemParams.topMargin = (int) (5 * density);
        item.setLayoutParams(itemParams);

        int iconSize = (int) (24 * density);
        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
        icon.setLayoutParams(iconParams);
        icon.setImageResource(iconRes);
        icon.setColorFilter(ContextCompat.getColor(this, R.color.text_primary));

        TextView tv = new TextView(this);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tvParams.setMarginStart((int) (4 * density));
        tv.setLayoutParams(tvParams);
        tv.setText(label);
        tv.setTextSize(13);
        tv.setTextColor(ContextCompat.getColor(this, R.color.text_primary));

        item.addView(icon);
        item.addView(tv);
        return item;
    }


    private void handleChangeStops() {
        Intent intent = new Intent(this, PickupDropoffActivity.class);
        intent.putExtra("trip_id", tripId);
        intent.putExtra("route_id", trip.getRouteId());
        intent.putExtra("departure_time", trip.getDepartureTime());
        intent.putExtra("base_price", trip.getBasePrice());
        intent.putExtra("departure", trip.getRoute().getDeparture());
        intent.putExtra("destination", trip.getRoute().getDestination());
        intent.putExtra("mode", "change_stops");
        intent.putExtra("selected_pickup_index", selectedPickupIndex);
        intent.putExtra("selected_dropoff_index", selectedDropoffIndex);
        startActivityForResult(intent, REQUEST_CHANGE_STOPS);
    }


    private void handleSelectSeats() {
        if (selectedPickup == null || selectedDropoff == null) {
            Toast.makeText(this, "Chưa có thông tin điểm đón/trả", Toast.LENGTH_SHORT).show();
            return;
        }
        showConfirmDialog();
    }


    private void showConfirmDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_stops, null);

        // Hiển thị thông tin điểm lên xe
        TextView tvPickupName = dialogView.findViewById(R.id.tvPickupName);
        TextView tvPickupTime = dialogView.findViewById(R.id.tvPickupTime);
        tvPickupName.setText(selectedPickup.getPointName());
        tvPickupTime.setText(calculateActualTime(selectedPickup));

        // Hiển thị thông tin điểm xuống xe
        TextView tvDropoffName = dialogView.findViewById(R.id.tvDropoffName);
        TextView tvDropoffTime = dialogView.findViewById(R.id.tvDropoffTime);
        tvDropoffName.setText(selectedDropoff.getPointName());
        tvDropoffTime.setText(calculateActualTime(selectedDropoff));

        // Tạo dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Nút Hủy
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Nút Tiếp tục → navigate sang SeatSelectionActivity
        Button btnContinue = dialogView.findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> {
            dialog.dismiss();
            navigateToSeatSelection();
        });

        dialog.show();
    }


    private void navigateToSeatSelection() {
        String pickupTime = calculateActualTimeFull(selectedPickup);
        String dropoffTime = calculateActualTimeFull(selectedDropoff);

        Intent intent = new Intent(this, SeatSelectionActivity.class);
        intent.putExtra("trip_id", tripId);
        intent.putExtra("route_id", trip.getRouteId());
        intent.putExtra("pickup_point_id", selectedPickup.getId());
        intent.putExtra("pickup_point_name", selectedPickup.getPointName());
        intent.putExtra("pickup_address", selectedPickup.getAddress());
        intent.putExtra("pickup_time", pickupTime);
        intent.putExtra("dropoff_point_id", selectedDropoff.getId());
        intent.putExtra("dropoff_point_name", selectedDropoff.getPointName());
        intent.putExtra("dropoff_address", selectedDropoff.getAddress());
        intent.putExtra("dropoff_time", dropoffTime);
        intent.putExtra("base_price", trip.getBasePrice());
        intent.putExtra("departure", trip.getRoute().getDeparture());
        intent.putExtra("destination", trip.getRoute().getDestination());
        if (trip.getBus() != null) {
            intent.putExtra("bus_layout", trip.getBus().getSeatLayout());
        }
        startActivity(intent);
    }


    private String calculateActualTimeFull(StopPoint stop) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            String baseTime;
            if ("dropoff".equals(stop.getPointType())) {
                baseTime = trip.getArrivalTime();
            } else {
                baseTime = trip.getDepartureTime();
            }

            if (baseTime == null) return null;

            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(baseTime));
            cal.add(Calendar.MINUTE, stop.getTimeOffset());

            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHANGE_STOPS && resultCode == RESULT_OK && data != null) {
            int pickupId = data.getIntExtra("pickup_point_id", -1);
            int dropoffId = data.getIntExtra("dropoff_point_id", -1);

            if (pickupId == -1 || dropoffId == -1) return;

            StopPoint newPickup = new StopPoint();
            newPickup.setId(pickupId);
            newPickup.setPointType(StopPoint.TYPE_PICKUP);
            newPickup.setPointName(data.getStringExtra("pickup_point_name"));
            newPickup.setAddress(data.getStringExtra("pickup_address"));
            newPickup.setTimeOffset(data.getIntExtra("pickup_time_offset", 0));

            StopPoint newDropoff = new StopPoint();
            newDropoff.setId(dropoffId);
            newDropoff.setPointType(StopPoint.TYPE_DROPOFF);
            newDropoff.setPointName(data.getStringExtra("dropoff_point_name"));
            newDropoff.setAddress(data.getStringExtra("dropoff_address"));
            newDropoff.setTimeOffset(data.getIntExtra("dropoff_time_offset", 0));

            selectedPickup = newPickup;
            selectedDropoff = newDropoff;

            selectedPickupIndex = data.getIntExtra("pickup_index", 0);
            selectedDropoffIndex = data.getIntExtra("dropoff_index", 0);

            displayTimeline();
        }
    }
}