package com.example.busgo.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.busgo.R;
import com.example.busgo.database.DAO.BookingDAO;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.Booking;
import com.example.busgo.until.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PaymentMethodActivity extends AppCompatActivity {

    // Phí nền tảng cố định
    private static final double PLATFORM_FEE = 10000;

    // UI
    private ImageView btnBack;
    private TextView tvTicketInfo, tvTicketPrice;
    private TextView tvPlatformFee;
    private TextView tvTotalPrice;
    private LinearLayout rowMomo, rowCash, lnBottomLayout;
    private Button btnPay;

    // Phương thức thanh toán đang chọn: "cash" hoặc "momo"
    private String selectedMethod = "cash";

    // Data từ màn hình trước
    private int tripId, routeId, pickupPointId, dropoffPointId;
    private String pickupTime, dropoffTime;
    private double basePrice, totalPrice;
    private ArrayList<String> seatNumbers;
    private String passengerName, passengerPhone, passengerEmail, note;

    // Database
    private DatabaseHelper dbHelper;
    private BookingDAO bookingDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        initViews();
        getDataFromIntent();
        initDatabase();
        displayPaymentDetails();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTicketInfo = findViewById(R.id.tvTicketInfo);
        tvTicketPrice = findViewById(R.id.tvTicketPrice);
        tvPlatformFee = findViewById(R.id.tvPlatformFee);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        rowMomo = findViewById(R.id.rowMomo);
        rowCash = findViewById(R.id.rowCash);
        btnPay = findViewById(R.id.btnPay);

        lnBottomLayout = findViewById(R.id.lnBottomLayout);
        final int originalbtnlnBottomLayout = ((ViewGroup.MarginLayoutParams) lnBottomLayout.getLayoutParams()).bottomMargin;
        ViewCompat.setOnApplyWindowInsetsListener(lnBottomLayout, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.bottomMargin = originalbtnlnBottomLayout + insets.bottom;
            view.setLayoutParams(params);
            return windowInsets;
        });

        sessionManager = SessionManager.getInstance(this);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        tripId = intent.getIntExtra("trip_id", -1);
        routeId = intent.getIntExtra("route_id", -1);
        pickupPointId = intent.getIntExtra("pickup_point_id", -1);
        dropoffPointId = intent.getIntExtra("dropoff_point_id", -1);
        pickupTime = intent.getStringExtra("pickup_time");
        dropoffTime = intent.getStringExtra("dropoff_time");
        basePrice = intent.getDoubleExtra("base_price", 0);
        totalPrice = intent.getDoubleExtra("total_price", 0);
        seatNumbers = intent.getStringArrayListExtra("seat_numbers");
        passengerName = intent.getStringExtra("passenger_name");
        passengerPhone = intent.getStringExtra("passenger_phone");
        passengerEmail = intent.getStringExtra("passenger_email");
        note = intent.getStringExtra("note");
    }

    private void initDatabase() {
        dbHelper = DatabaseHelper.getInstance(this);
        bookingDAO = new BookingDAO(dbHelper);
    }

    /**
     * Hiển thị chi tiết thanh toán: vé, phí, tổng
     */
    private void displayPaymentDetails() {
        int numSeats = (seatNumbers != null) ? seatNumbers.size() : 0;

        // Thông tin vé: "2x Ghế ngồi" hoặc "1x Giường nằm"
        String ticketLabel = numSeats + "x vé";
        tvTicketInfo.setText(ticketLabel);

        // Giá vé
        tvTicketPrice.setText(formatPrice(totalPrice));

        // Phí nền tảng
        tvPlatformFee.setText(formatPrice(PLATFORM_FEE));

        // Tổng cộng (giá vé + phí nền tảng)
        double grandTotal = totalPrice + PLATFORM_FEE;
        tvTotalPrice.setText(formatPrice(grandTotal));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Mặc định chọn tiền mặt
        selectPaymentMethod("cash");

        rowCash.setOnClickListener(v -> selectPaymentMethod("cash"));

        rowMomo.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng MoMo đang được phát triển", Toast.LENGTH_SHORT).show();
        });

        btnPay.setOnClickListener(v -> processPayment());
    }

    /**
     * Chọn phương thức thanh toán: đổi viền selected/unselected
     */
    private void selectPaymentMethod(String method) {
        selectedMethod = method;

        // Reset tất cả về unselected
        rowMomo.setBackgroundResource(R.drawable.bg_pay_card);
        rowCash.setBackgroundResource(R.drawable.bg_pay_card);

        // Highlight phương thức được chọn
        if ("momo".equals(method)) {
            rowMomo.setBackgroundResource(R.drawable.bg_pay_card_menthod_selected);
        } else {
            rowCash.setBackgroundResource(R.drawable.bg_pay_card_menthod_selected);
        }
    }

    /**
     * Xử lý thanh toán tiền mặt: tạo booking → chuyển sang BookingSuccessActivity
     */
    private void processPayment() {
        int userId = sessionManager.getLoggedInUserId();
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        String bookingCode = generateBookingCode();

        // Tạo booking object
        Booking booking = new Booking();
        booking.setBookingCode(bookingCode);
        booking.setUserId(userId);
        booking.setTripId(tripId);
        booking.setSeatNumbers(String.join(",", seatNumbers));
        booking.setNumSeats(seatNumbers.size());
        booking.setPickupPointId(pickupPointId);
        booking.setDropoffPointId(dropoffPointId);
        booking.setPickupTime(pickupTime);
        booking.setDropoffTime(dropoffTime);
        booking.setPassengerName(passengerName);
        booking.setPassengerPhone(passengerPhone);
        booking.setPassengerEmail(passengerEmail);
        booking.setNote(note);
        booking.setTotalPrice(totalPrice + PLATFORM_FEE);
        booking.setBookingStatus("confirmed");
        booking.setPaymentStatus("unpaid");
        booking.setPaymentMethod("cash");

        // BookingDAO.createBooking() đã tự quản lý transaction
        // (insert booking → update seats → decrease available_seats)
        long bookingId = bookingDAO.createBooking(booking, seatNumbers);

        if (bookingId == -1) {
            Toast.makeText(this, "Lỗi: Không thể tạo vé. Vui lòng thử lại", Toast.LENGTH_LONG).show();
            return;
        }

        // Chuyển sang màn hình thành công
        Intent intent = new Intent(this, BookingSuccessActivity.class);
        intent.putExtra("booking_code", bookingCode);
        intent.putExtra("payment_status", "unpaid");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Format giá tiền: 600000 → "600.000VNĐ"
     */
    private String formatPrice(double price) {
        return String.format(Locale.getDefault(), "%,.0fVNĐ", price).replace(",", ".");
    }

    /**
     * Tạo mã booking: BK + timestamp + 4 số random
     */
    private String generateBookingCode() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        int random = (int) (Math.random() * 9000) + 1000;
        return "BK" + timestamp + random;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}