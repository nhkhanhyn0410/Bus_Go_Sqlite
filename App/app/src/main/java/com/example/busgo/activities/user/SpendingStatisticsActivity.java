package com.example.busgo.activities.user;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.busgo.R;
import com.example.busgo.database.DAO.BookingDAO;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.User;
import com.example.busgo.until.SessionManager;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SpendingStatisticsActivity extends AppCompatActivity {

    private TextView tvTotalSpending, tvMonthlySpending, tvCurrentMonth;
    private ImageView ivBack;
    private RelativeLayout layoutSelectMonth;
    private BookingDAO bookingDAO;
    private SessionManager sessionManager;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spending_statistics);

        selectedDate = Calendar.getInstance();
        initViews();
        setupData();
    }

    private void initViews() {
        tvTotalSpending = findViewById(R.id.tvTotalSpending);
        tvMonthlySpending = findViewById(R.id.tvMonthlySpending);
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        ivBack = findViewById(R.id.ivBack);
        layoutSelectMonth = findViewById(R.id.layoutSelectMonth);

        ivBack.setOnClickListener(v -> finish());
        
        layoutSelectMonth.setOnClickListener(v -> showMonthYearPicker());

        bookingDAO = new BookingDAO(DatabaseHelper.getInstance(this));
        sessionManager = SessionManager.getInstance(this);
    }

    private void showMonthYearPicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    updateMonthlyData();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.setTitle("Chọn tháng thống kê");
        datePickerDialog.show();
    }

    private void setupData() {
        User user = sessionManager.getLoggedInUser();
        if (user == null) return;
        double total = bookingDAO.getTotalSpending(user.getId());
        tvTotalSpending.setText(formatCurrency(total));

        updateMonthlyData();
    }

    private void updateMonthlyData() {
        User user = sessionManager.getLoggedInUser();
        if (user == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String yearMonth = sdf.format(selectedDate.getTime());
        
        SimpleDateFormat displaySdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        tvCurrentMonth.setText("Tháng " + displaySdf.format(selectedDate.getTime()));

        double monthly = bookingDAO.getMonthlySpending(user.getId(), yearMonth);
        tvMonthlySpending.setText(formatCurrency(monthly));
    }

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }
}
