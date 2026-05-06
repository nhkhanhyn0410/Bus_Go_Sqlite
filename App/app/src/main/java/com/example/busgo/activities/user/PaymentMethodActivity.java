package com.example.busgo.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.busgo.R;

import java.util.ArrayList;
import java.util.Locale;

public class PaymentMethodActivity extends AppCompatActivity {


    private static final double PLATFORM_FEE = 10000;


    private ImageView btnBack;
    private TextView tvTicketInfo, tvTicketPrice;
    private TextView tvPlatformFee;
    private TextView tvTotalPrice;
    private LinearLayout rowMomo, rowCash, lnBottomLayout;
    private Button btnPay;


    private String selectedMethod = "cash";


    private int tripId, pickupPointId, dropoffPointId;
    private String pickupTime, dropoffTime;
    private double totalPrice;
    private ArrayList<String> seatNumbers;
    private String passengerName, passengerPhone, passengerEmail, note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        initViews();
        getDataFromIntent();
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
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        tripId = intent.getIntExtra("trip_id", -1);
        pickupPointId = intent.getIntExtra("pickup_point_id", -1);
        dropoffPointId = intent.getIntExtra("dropoff_point_id", -1);
        pickupTime = intent.getStringExtra("pickup_time");
        dropoffTime = intent.getStringExtra("dropoff_time");
        totalPrice = intent.getDoubleExtra("total_price", 0);
        seatNumbers = intent.getStringArrayListExtra("seat_numbers");
        passengerName = intent.getStringExtra("passenger_name");
        passengerPhone = intent.getStringExtra("passenger_phone");
        passengerEmail = intent.getStringExtra("passenger_email");
    }

    private void displayPaymentDetails() {
        int numSeats = (seatNumbers != null) ? seatNumbers.size() : 0;

        String ticketLabel = numSeats + "x vé";
        tvTicketInfo.setText(ticketLabel);

        tvTicketPrice.setText(formatPrice(totalPrice));

        tvPlatformFee.setText(formatPrice(PLATFORM_FEE));

        double grandTotal = totalPrice + PLATFORM_FEE;
        tvTotalPrice.setText(formatPrice(grandTotal));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        selectPaymentMethod("cash");

        rowCash.setOnClickListener(v -> selectPaymentMethod("cash"));
        rowMomo.setOnClickListener(v -> selectPaymentMethod("momo"));

        btnPay.setOnClickListener(v -> processPayment());
    }

    private void selectPaymentMethod(String method) {
        selectedMethod = method;
        rowMomo.setBackgroundResource(R.drawable.bg_pay_card);
        rowCash.setBackgroundResource(R.drawable.bg_pay_card);

        if ("momo".equals(method)) {
            rowMomo.setBackgroundResource(R.drawable.bg_pay_card_menthod_selected);
        } else {
            rowCash.setBackgroundResource(R.drawable.bg_pay_card_menthod_selected);
        }
    }

    private void processPayment() {
        Intent intent = new Intent(this, PaymentProcessingActivity.class);
        intent.putExtra("payment_method", selectedMethod);
        intent.putExtra("trip_id", tripId);
        intent.putExtra("pickup_point_id", pickupPointId);
        intent.putExtra("dropoff_point_id", dropoffPointId);
        intent.putExtra("pickup_time", pickupTime);
        intent.putExtra("dropoff_time", dropoffTime);
        intent.putExtra("total_price", totalPrice);
        intent.putStringArrayListExtra("seat_numbers", seatNumbers);
        intent.putExtra("passenger_name", passengerName);
        intent.putExtra("passenger_phone", passengerPhone);
        intent.putExtra("passenger_email", passengerEmail);
        startActivity(intent);
    }

    private String formatPrice(double price) {
        return String.format(Locale.getDefault(), "%,.0fVNĐ", price).replace(",", ".");
    }
}
