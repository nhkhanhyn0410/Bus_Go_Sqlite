package com.example.busgo.activities.user;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
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
import com.example.busgo.database.DAO.BookingDAO;
import com.example.busgo.database.DAO.StopPointDAO;
import com.example.busgo.database.DAO.TripDAO;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.Booking;
import com.example.busgo.database.model.Bus;
import com.example.busgo.database.model.Route;
import com.example.busgo.database.model.StopPoint;
import com.example.busgo.database.model.Trip;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BookingDetailActivity extends AppCompatActivity {

    // UI
    private ImageView btnBack, ivQRCode;
    private TextView tvCompanyName, tvBusType, tvBusPlate;
    private TextView tvDepartureStation, tvDepartureTime, tvDepartureCity;
    private TextView tvArrivalStation, tvArrivalTime, tvArrivalCity;
    private TextView tvDuration;
    private TextView tvPassengerInfo, tvSeats, tvBookingCode;
    private TextView tvTotalPrice, tvStopCount, tvStatus;
    private TextView tvRestStopsTitle;
    private LinearLayout containerRestStops;
    private Button btnCancelBooking;

    // Data
    private String bookingCode;
    private Booking booking;
    private Trip trip;

    // Database
    private DatabaseHelper dbHelper;
    private BookingDAO bookingDAO;
    private TripDAO tripDAO;
    private StopPointDAO stopPointDAO;

    private float density;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_detail);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);


        density = getResources().getDisplayMetrics().density;

        initViews();
        getDataFromIntent();
        initDatabase();
        loadBookingDetail();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        ivQRCode = findViewById(R.id.ivQRCode);
        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvBusType = findViewById(R.id.tvBusType);
        tvBusPlate = findViewById(R.id.tvBusPlate);
        tvDepartureStation = findViewById(R.id.tvDepartureStation);
        tvDepartureTime = findViewById(R.id.tvDepartureTime);
        tvDepartureCity = findViewById(R.id.tvDepartureCity);
        tvArrivalStation = findViewById(R.id.tvArrivalStation);
        tvArrivalTime = findViewById(R.id.tvArrivalTime);
        tvArrivalCity = findViewById(R.id.tvArrivalCity);
        tvDuration = findViewById(R.id.tvDuration);
        tvPassengerInfo = findViewById(R.id.tvPassengerInfo);
        tvSeats = findViewById(R.id.tvSeats);
        tvBookingCode = findViewById(R.id.tvBookingCode);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvStopCount = findViewById(R.id.tvStopCount);
        tvStatus = findViewById(R.id.tvStatus);
        tvRestStopsTitle = findViewById(R.id.tvRestStopsTitle);
        containerRestStops = findViewById(R.id.containerRestStops);
        btnCancelBooking = findViewById(R.id.btnCancelBooking);

        final int originalbtnCancelBooking = ((ViewGroup.MarginLayoutParams) btnCancelBooking.getLayoutParams()).bottomMargin;
        ViewCompat.setOnApplyWindowInsetsListener(btnCancelBooking, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.bottomMargin = originalbtnCancelBooking + insets.bottom;
            view.setLayoutParams(params);
            return windowInsets;
        });
    }

    private void getDataFromIntent() {
        bookingCode = getIntent().getStringExtra("booking_code");
        if (bookingCode == null || bookingCode.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không có mã vé", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initDatabase() {
        dbHelper = DatabaseHelper.getInstance(this);
        bookingDAO = new BookingDAO(dbHelper);
        tripDAO = new TripDAO(dbHelper);
        stopPointDAO = new StopPointDAO(dbHelper);
    }

    private void loadBookingDetail() {
        booking = bookingDAO.getBookingByCode(bookingCode);
        if (booking == null) {
            Toast.makeText(this, "Không tìm thấy vé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load trip info
        trip = tripDAO.getTripById(booking.getTripId());

        displayTicketInfo();
        displayRestStops();
        updateCancelButton();
    }

    /**
     * Hiển thị thông tin vé trên card
     */
    private void displayTicketInfo() {
        // Mã vé (rút gọn 8 ký tự cuối)
        String shortCode = bookingCode;
        if (bookingCode.length() > 8) {
            shortCode = bookingCode.substring(bookingCode.length() - 8);
        }
        tvBookingCode.setText(shortCode);

        if (trip != null) {
            Bus bus = trip.getBus();
            Route route = trip.getRoute();

            // Nhà xe
            if (bus != null) {
                tvCompanyName.setText(bus.getCompanyName() != null ? bus.getCompanyName() : "Nhà xe");
                tvBusType.setText(bus.getBusType() != null ? bus.getBusType() : "");
                tvBusPlate.setText(bus.getBusNumber() != null ? bus.getBusNumber() : "");
            }

            // Tuyến đường
            if (route != null) {
                tvDepartureStation.setText(route.getDeparture());
                tvArrivalStation.setText(route.getDestination());
                tvDepartureCity.setText(route.getDeparture());
                tvArrivalCity.setText(route.getDestination());

                // Thời gian di chuyển
                if (route.getDuration() > 0) {
                    int hours = route.getDuration() / 60;
                    int mins = route.getDuration() % 60;
                    tvDuration.setText(hours + "h " + String.format("%02d", mins) + "m");
                }
            }

            // Giờ đi/đến (format: "13.00, 13/01")
            tvDepartureTime.setText(formatDateTimeShort(trip.getDepartureTime()));
            tvArrivalTime.setText(formatDateTimeShort(trip.getArrivalTime()));
        }

        // Hành khách
        tvPassengerInfo.setText(booking.getNumSeats() + "x Người lớn");

        // Ghế
        String seatStr = booking.getSeatNumbers();
        if (seatStr != null) {
            tvSeats.setText(seatStr.replace(",", ", "));
        }

        // Giá vé
        tvTotalPrice.setText(String.format(Locale.getDefault(), "%,.0f VND", booking.getTotalPrice()).replace(",", "."));

        // Trạng thái
        String statusText;
        if ("cancelled".equals(booking.getBookingStatus())) {
            statusText = "Trạng thái: Đã hủy";
        } else if ("paid".equals(booking.getPaymentStatus())) {
            statusText = "Trạng thái: Đã thanh toán";
        } else {
            statusText = "Trạng thái: Chưa thanh toán";
        }
        tvStatus.setText(statusText);
    }

    /**
     * Hiển thị danh sách điểm nghỉ chân
     */
    private void displayRestStops() {
        if (trip == null) return;

        List<StopPoint> restStops = stopPointDAO.getRestStopsByRouteId(trip.getRouteId());

        // Đếm tổng điểm dừng (pickup + dropoff + rest)
        List<StopPoint> pickups = stopPointDAO.getPickupPointsByRouteId(trip.getRouteId());
        List<StopPoint> dropoffs = stopPointDAO.getDropoffPointsByRouteId(trip.getRouteId());
        int totalStops = trip.getStopsCount();
        tvStopCount.setText(totalStops + " Điểm");

        if (restStops == null || restStops.isEmpty()) {
            tvRestStopsTitle.setVisibility(View.GONE);
            containerRestStops.setVisibility(View.GONE);
            return;
        }

        containerRestStops.removeAllViews();
        for (StopPoint stop : restStops) {
            View itemView = createRestStopItem(stop);
            containerRestStops.addView(itemView);
        }
    }

    /**
     * Tạo 1 card điểm nghỉ chân: icon bolt + tên + icon clock + thời gian dừng
     */
    private View createRestStopItem(StopPoint stop) {
        // Card container
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card_white);
        int pad = (int) (12 * density);
        card.setPadding(pad, pad, pad, pad);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = (int) (8 * density);
        card.setLayoutParams(cardParams);

        // Icon bolt
        ImageView iconBolt = new ImageView(this);
        int iconSize = (int) (24 * density);
        iconBolt.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
        iconBolt.setImageResource(R.drawable.ic_bolt);
        card.addView(iconBolt);

        // Tên điểm dừng
        TextView tvName = new TextView(this);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        nameParams.setMarginStart((int) (10 * density));
        tvName.setLayoutParams(nameParams);
        tvName.setText(stop.getPointName());
        tvName.setTextSize(14);
        tvName.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        card.addView(tvName);

        // Thời gian dừng
        int stopDuration = stop.getStopDuration();
        if (stopDuration > 0) {
            TextView tvDur = new TextView(this);
            tvDur.setText(stopDuration + " Phút");
            tvDur.setTextSize(10);
            tvDur.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            card.addView(tvDur);
        }

        return card;
    }

    /**
     * Format datetime "yyyy-MM-dd HH:mm" → "HH.mm, dd/MM"
     */
    private String formatDateTimeShort(String dateTime) {
        if (dateTime == null || dateTime.length() < 16) return "--";
        try {
            SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            SimpleDateFormat sdfOut = new SimpleDateFormat("HH.mm, dd/MM", Locale.getDefault());
            return sdfOut.format(sdfIn.parse(dateTime));
        } catch (Exception e) {
            return "--";
        }
    }


    private void updateCancelButton() {
        if ("cancelled".equals(booking.getBookingStatus())) {
            btnCancelBooking.setEnabled(false);
            btnCancelBooking.setText("Đã hủy");
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancelBooking.setOnClickListener(v -> showCancelDialog());
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy vé")
                .setMessage("Bạn có chắc muốn hủy vé này không?")
                .setPositiveButton("Hủy vé", (dialog, which) -> cancelBooking())
                .setNegativeButton("Không", null)
                .show();
    }

    private void cancelBooking() {
        boolean success = bookingDAO.cancelBooking(booking.getBookingCode());
        if (success) {
            Toast.makeText(this, "Đã hủy vé thành công", Toast.LENGTH_SHORT).show();
            // Reload lại để cập nhật UI
            loadBookingDetail();
        } else {
            Toast.makeText(this, "Không thể hủy vé", Toast.LENGTH_SHORT).show();
        }
    }

}