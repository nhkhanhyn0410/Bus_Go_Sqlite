package com.example.busgo.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.busgo.R;
import com.example.busgo.database.DAO.StopPointDAO;
import com.example.busgo.database.DAO.TripDAO;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.StopPoint;
import com.example.busgo.database.model.Trip;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PickupDropoffActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvHeaderTitle;
    private RadioGroup containerPickup, containerDropoff;
    private Button btnConfirm;
    private LinearLayout lnBottomLayout;

    // Data
    private StopPointDAO stopPointDAO;
    private TripDAO tripDAO;
    private int tripId, routeId;
    private String departureTime, arrivalTime;
    private double basePrice;
    private String departure, destination;
    private String busLayout;
    private boolean isChangeMode;
    private int selectedPickupIndex = 0;
    private int selectedDropoffIndex = 0;
    private List<StopPoint> pickupPoints;
    private List<StopPoint> dropoffPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pickup_dropoff);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);

        initViews();

        final int originalLnBottomLayout = ((ViewGroup.MarginLayoutParams) lnBottomLayout.getLayoutParams()).bottomMargin;
        ViewCompat.setOnApplyWindowInsetsListener(lnBottomLayout, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.bottomMargin = originalLnBottomLayout + insets.bottom;
            view.setLayoutParams(params);
            return windowInsets;
        });
        getIntentData();

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        stopPointDAO = new StopPointDAO(dbHelper);
        tripDAO = new TripDAO(dbHelper);

        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> handleConfirm());

        loadData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        containerPickup = findViewById(R.id.containerPickup);
        containerDropoff = findViewById(R.id.containerDropoff);
        btnConfirm = findViewById(R.id.btnConfirm);
        lnBottomLayout = findViewById(R.id.lnBottomLayout);
    }

    private void getIntentData() {
        tripId = getIntent().getIntExtra("trip_id", -1);
        routeId = getIntent().getIntExtra("route_id", -1);
        departureTime = getIntent().getStringExtra("departure_time");
        basePrice = getIntent().getDoubleExtra("base_price", 0);
        departure = getIntent().getStringExtra("departure");
        destination = getIntent().getStringExtra("destination");

        // Kiểm tra mode: nếu "change_stops" thì chỉ trả kết quả, không navigate
        String mode = getIntent().getStringExtra("mode");
        isChangeMode = "change_stops".equals(mode);

        selectedPickupIndex = getIntent().getIntExtra("selected_pickup_index", 0);
        selectedDropoffIndex = getIntent().getIntExtra("selected_dropoff_index", 0);
    }


    private void loadData() {
        new Thread(() -> {
            // Load trip để lấy arrival_time và bus_layout
            Trip trip = tripDAO.getTripById(tripId);
            if (trip != null) {
                arrivalTime = trip.getArrivalTime();

                if (trip.getBus() != null) {
                    busLayout = trip.getBus().getSeatLayout();
                }
            }

            pickupPoints = stopPointDAO.getPickupPointsByRouteId(routeId);
            dropoffPoints = stopPointDAO.getDropoffPointsByRouteId(routeId);

            if (pickupPoints != null) {
                calculatePickupTimes(pickupPoints);
            }
            if (dropoffPoints != null && arrivalTime != null) {
                calculateDropoffTimes(dropoffPoints);
            }

            runOnUiThread(() -> {
                populateRadioGroup(containerPickup, pickupPoints, true);
                populateRadioGroup(containerDropoff, dropoffPoints, false);
            });
        }).start();
    }


    private void populateRadioGroup(RadioGroup group, List<StopPoint> points, boolean isPickup) {
        group.removeAllViews();

        int checkedIndex = isPickup ? selectedPickupIndex : selectedDropoffIndex;
        if (checkedIndex < 0 || checkedIndex >= points.size()) {
            checkedIndex = 0;
        }

        for (int i = 0; i < points.size(); i++) {
            StopPoint point = points.get(i);

            RadioButton rb = new RadioButton(this);
            rb.setSingleLine(false);

            // Format text: "Tên điểm — Giờ\nĐịa chỉ"
            String timeStr = formatTime(point, isPickup);
            String text = point.getPointName() + " — " + timeStr + "\n" + point.getAddress();
            rb.setText(text);

            // Style
            int padding = (int) (16 * getResources().getDisplayMetrics().density);
            rb.setPadding(padding, padding, padding, padding);
            rb.setTextSize(15);
            rb.setTextColor(getResources().getColor(R.color.text_primary));

            group.addView(rb);

            if (i == checkedIndex) {
                rb.setChecked(true);
            }
        }
    }

    private String formatTime(StopPoint point, boolean isPickup) {
        String actualTime = isPickup ? point.getActualPickupTime() : point.getActualDropoffTime();

        if (actualTime != null && !actualTime.isEmpty()) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(actualTime);
                if (date != null) {
                    return outputFormat.format(date);
                }
            } catch (Exception e) {
                return actualTime;
            }
        }
        return "--:--";
    }

    private void calculatePickupTimes(List<StopPoint> points) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date baseTime = sdf.parse(departureTime);
            if (baseTime == null) return;

            for (StopPoint point : points) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(baseTime);
                cal.add(Calendar.MINUTE, point.getTimeOffset());
                point.setActualPickupTime(sdf.format(cal.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateDropoffTimes(List<StopPoint> points) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date baseTime = sdf.parse(arrivalTime);
            if (baseTime == null) return;

            for (StopPoint point : points) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(baseTime);
                cal.add(Calendar.MINUTE, point.getTimeOffset());
                point.setActualDropoffTime(sdf.format(cal.getTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void handleConfirm() {
        int pickupIndex = getCheckedIndex(containerPickup);
        int dropoffIndex = getCheckedIndex(containerDropoff);
        StopPoint pickup = pickupPoints.get(pickupIndex);
        StopPoint dropoff = dropoffPoints.get(dropoffIndex);

        if (isChangeMode) {
            Intent data = new Intent();
            data.putExtra("pickup_point_id", pickup.getId());
            data.putExtra("dropoff_point_id", dropoff.getId());
            data.putExtra("pickup_point_name", pickup.getPointName());
            data.putExtra("dropoff_point_name", dropoff.getPointName());
            data.putExtra("pickup_time", pickup.getActualPickupTime());
            data.putExtra("dropoff_time", dropoff.getActualDropoffTime());
            data.putExtra("pickup_address", pickup.getAddress());
            data.putExtra("dropoff_address", dropoff.getAddress());
            data.putExtra("pickup_time_offset", pickup.getTimeOffset());
            data.putExtra("dropoff_time_offset", dropoff.getTimeOffset());
            data.putExtra("pickup_index", pickupIndex);
            data.putExtra("dropoff_index", dropoffIndex);
            setResult(RESULT_OK, data);
            finish();
            return;
        }
    }

    private int getCheckedIndex(RadioGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof RadioButton && ((RadioButton) child).isChecked()) {
                return i;
            }
        }
        return -1;
    }
}