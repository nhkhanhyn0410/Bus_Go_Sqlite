package com.example.busgo.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

    // Data
    private StopPointDAO stopPointDAO;
    private TripDAO tripDAO;
    private int tripId, routeId;
    private String departureTime, arrivalTime;
    private double basePrice;
    private String departure, destination;
    private String busLayout; // Sẽ load từ Trip → Bus
    private boolean isChangeMode; // true khi mở từ nút "Thay đổi" trong TripDetail

    // Lưu danh sách điểm để lấy item theo index (RadioButton id = index)
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
        getIntentData();

        // Khởi tạo DAO
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        stopPointDAO = new StopPointDAO(dbHelper);
        tripDAO = new TripDAO(dbHelper);

        // Listeners
        btnBack.setOnClickListener(v -> finish());
        btnConfirm.setOnClickListener(v -> handleConfirm());

        // Load dữ liệu trên background thread
        loadData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        containerPickup = findViewById(R.id.containerPickup);
        containerDropoff = findViewById(R.id.containerDropoff);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

    /**
     * Lấy dữ liệu từ Intent (truyền từ TripDetailActivity)
     */
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

        if (tripId == -1 || routeId == -1 || departureTime == null) {
            Toast.makeText(this, "Lỗi: Thiếu thông tin chuyến đi", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Load trip (để lấy arrival_time + bus_layout) và stop points trên background thread
     */
    private void loadData() {
        new Thread(() -> {
            // Load trip để lấy arrival_time và bus_layout
            Trip trip = tripDAO.getTripById(tripId);
            if (trip != null) {
                arrivalTime = trip.getArrivalTime();
                // Lấy bus_layout từ Bus object (fix bug: trước đây không truyền qua intent)
                if (trip.getBus() != null) {
                    busLayout = trip.getBus().getSeatLayout();
                }
            }

            // Load điểm lên xe
            pickupPoints = stopPointDAO.getPickupPointsByRouteId(routeId);
            // Load điểm xuống xe
            dropoffPoints = stopPointDAO.getDropoffPointsByRouteId(routeId);

            // Tính giờ thực tế
            if (pickupPoints != null) {
                calculatePickupTimes(pickupPoints);
            }
            if (dropoffPoints != null && arrivalTime != null) {
                calculateDropoffTimes(dropoffPoints);
            }

            // Cập nhật UI trên main thread
            runOnUiThread(() -> {
                // Hiển thị điểm lên xe
                if (pickupPoints != null && !pickupPoints.isEmpty()) {
                    populateRadioGroup(containerPickup, pickupPoints, true);
                } else {
                    Toast.makeText(this, "Không có điểm lên xe cho tuyến này",
                            Toast.LENGTH_SHORT).show();
                }

                // Hiển thị điểm xuống xe
                if (dropoffPoints != null && !dropoffPoints.isEmpty()) {
                    populateRadioGroup(containerDropoff, dropoffPoints, false);
                } else {
                    Toast.makeText(this, "Không có điểm xuống xe cho tuyến này",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    /**
     * Tạo RadioButton cho mỗi StopPoint và thêm vào RadioGroup
     * Text hiển thị 2 dòng: "Tên điểm — Giờ\nĐịa chỉ"
     * Mặc định check item đầu tiên
     */
    private void populateRadioGroup(RadioGroup group, List<StopPoint> points, boolean isPickup) {
        group.removeAllViews();

        for (int i = 0; i < points.size(); i++) {
            StopPoint point = points.get(i);

            RadioButton rb = new RadioButton(this);
            rb.setId(i); // Map index → StopPoint
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

            // Mặc định chọn item đầu tiên
            if (i == 0) {
                rb.setChecked(true);
            }
        }
    }

    /**
     * Format thời gian hiển thị (HH:mm)
     */
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

    /**
     * Tính giờ đón thực tế: departure_time + time_offset (phút)
     */
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

    /**
     * Tính giờ trả thực tế: arrival_time + time_offset (phút)
     */
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

    /**
     * Xử lý nút "Đồng ý" → validate và chuyển sang SeatSelectionActivity hoặc trả kết quả
     */
    private void handleConfirm() {
        // Lấy StopPoint từ checkedRadioButtonId (id = index trong list)
        int pickupIndex = containerPickup.getCheckedRadioButtonId();
        int dropoffIndex = containerDropoff.getCheckedRadioButtonId();

        // Kiểm tra đã chọn điểm lên xe
        if (pickupPoints == null || pickupIndex < 0 || pickupIndex >= pickupPoints.size()) {
            Toast.makeText(this, "Vui lòng chọn điểm lên xe", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra đã chọn điểm xuống xe
        if (dropoffPoints == null || dropoffIndex < 0 || dropoffIndex >= dropoffPoints.size()) {
            Toast.makeText(this, "Vui lòng chọn điểm xuống xe", Toast.LENGTH_SHORT).show();
            return;
        }

        StopPoint pickup = pickupPoints.get(pickupIndex);
        StopPoint dropoff = dropoffPoints.get(dropoffIndex);

        // Mode "change_stops": trả kết quả về TripDetailActivity
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
            setResult(RESULT_OK, data);
            finish();
            return;
        }

        // Mode bình thường: chuyển sang SeatSelectionActivity
        Intent intent = new Intent(this, SeatSelectionActivity.class);
        intent.putExtra("trip_id", tripId);
        intent.putExtra("route_id", routeId);
        intent.putExtra("pickup_point_id", pickup.getId());
        intent.putExtra("pickup_point_name", pickup.getPointName());
        intent.putExtra("pickup_address", pickup.getAddress());
        intent.putExtra("pickup_time", pickup.getActualPickupTime());
        intent.putExtra("dropoff_point_id", dropoff.getId());
        intent.putExtra("dropoff_point_name", dropoff.getPointName());
        intent.putExtra("dropoff_address", dropoff.getAddress());
        intent.putExtra("dropoff_time", dropoff.getActualDropoffTime());
        intent.putExtra("base_price", basePrice);
        intent.putExtra("departure", departure);
        intent.putExtra("destination", destination);
        intent.putExtra("bus_layout", busLayout);
        startActivity(intent);
    }
}