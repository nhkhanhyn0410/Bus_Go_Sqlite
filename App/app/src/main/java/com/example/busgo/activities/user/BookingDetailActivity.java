package com.example.busgo.activities.user;

import android.graphics.Bitmap;
import android.os.Bundle;
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

    private String bookingCode;
    private Booking booking;
    private Trip trip;

    private DatabaseHelper dbHelper;
    private BookingDAO bookingDAO;
    private TripDAO tripDAO;
    private StopPointDAO stopPointDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_detail);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);

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

        trip = tripDAO.getTripById(booking.getTripId());

        displayTicketInfo();
        displayRestStops();
        updateCancelButton();
    }

    private void displayTicketInfo() {
        String shortCode = bookingCode;
        if (bookingCode.length() > 8) {
            shortCode = bookingCode.substring(bookingCode.length() - 8);
        }
        tvBookingCode.setText(shortCode);

        if (trip != null) {
            Bus bus = trip.getBus();
            Route route = trip.getRoute();


            if (bus != null) {
                tvCompanyName.setText(bus.getCompanyName() != null ? bus.getCompanyName() : "Nhà xe");
                tvBusType.setText(bus.getBusType() != null ? bus.getBusType() : "");
                tvBusPlate.setText(bus.getBusNumber() != null ? bus.getBusNumber() : "");
            }


            if (route != null) {
                tvDepartureStation.setText(route.getDeparture());
                tvArrivalStation.setText(route.getDestination());
                tvDepartureCity.setText(route.getDeparture());
                tvArrivalCity.setText(route.getDestination());


                if (route.getDuration() > 0) {
                    int hours = route.getDuration() / 60;
                    int mins = route.getDuration() % 60;
                    tvDuration.setText(hours + "h " + String.format("%02d", mins) + "m");
                }
            }


            tvDepartureTime.setText(formatDateTimeShort(trip.getDepartureTime()));
            tvArrivalTime.setText(formatDateTimeShort(trip.getArrivalTime()));
        }


        tvPassengerInfo.setText(booking.getNumSeats() + "x Người lớn");


        String seatStr = booking.getSeatNumbers();
        if (seatStr != null) {
            tvSeats.setText(seatStr.replace(",", ", "));
        }


        tvTotalPrice.setText(String.format(Locale.getDefault(), "%,.0f VND", booking.getTotalPrice()).replace(",", "."));


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

    private void displayRestStops() {
        if (trip == null) return;

        List<StopPoint> restStops = stopPointDAO.getRestStopsByRouteId(trip.getRouteId());


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


    private View createRestStopItem(StopPoint stop) {
        View item = LayoutInflater.from(this)
                .inflate(R.layout.item_rest_stop, containerRestStops, false);

        ((TextView) item.findViewById(R.id.tvName)).setText(stop.getPointName());

        TextView tvDuration = item.findViewById(R.id.tvDuration);
        int stopDuration = stop.getStopDuration();
        if (stopDuration > 0) {
            tvDuration.setText(stopDuration + " Phút");
            tvDuration.setVisibility(View.VISIBLE);
        }

        return item;
    }


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

            loadBookingDetail();
        } else {
            Toast.makeText(this, "Không thể hủy vé", Toast.LENGTH_SHORT).show();
        }
    }

}