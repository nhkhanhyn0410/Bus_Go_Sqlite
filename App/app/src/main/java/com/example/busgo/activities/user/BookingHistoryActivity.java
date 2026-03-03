package com.example.busgo.activities.user;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.busgo.database.model.Route;
import com.example.busgo.database.model.StopPoint;
import com.example.busgo.database.model.Trip;
import com.example.busgo.R;
import com.example.busgo.adapters.BookingAdapter;
import com.example.busgo.database.DAO.BookingDAO;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.Booking;
import com.example.busgo.until.BottomNavHelper;
import com.example.busgo.until.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {

    private static final String TAB_ACTIVE = "active";
    private static final String TAB_COMPLETED = "completed";
    private static final String TAB_CANCELLED = "cancelled";

    private TextView tabActive;
    private TextView tabCompleted;
    private TextView tabCancelled;
    private TextView tvEmpty;

    private final List<Booking> allBookings = new ArrayList<>();
    private BookingAdapter bookingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_history);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);

        setupViews();
        loadBookingData();
        selectTab(TAB_COMPLETED);

        BottomNavHelper.setup(this, BottomNavHelper.TAB_BOOKINGS);
    }

    private void setupViews() {
        tabActive = findViewById(R.id.tabActive);
        tabCompleted = findViewById(R.id.tabCompleted);
        tabCancelled = findViewById(R.id.tabCancelled);
        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rvBookings = findViewById(R.id.rvBookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        bookingAdapter = new BookingAdapter();
        rvBookings.setAdapter(bookingAdapter);

        tabActive.setOnClickListener(v -> selectTab(TAB_ACTIVE));
        tabCompleted.setOnClickListener(v -> selectTab(TAB_COMPLETED));
        tabCancelled.setOnClickListener(v -> selectTab(TAB_CANCELLED));
    }

    private void loadBookingData() {
        int userId = SessionManager.getInstance(this).getLoggedInUserId();
        if (userId <= 0) {
            allBookings.clear();
            allBookings.addAll(createSampleBookings());
            return;
        }

        BookingDAO bookingDAO = new BookingDAO(new DatabaseHelper(this));
        allBookings.clear();
        allBookings.addAll(bookingDAO.getBookingsByUserId(userId));
        if (allBookings.isEmpty()) {
            allBookings.addAll(createSampleBookings());
        }
    }

    private List<Booking> createSampleBookings() {
        List<Booking> samples = new ArrayList<>();
        samples.add(createSampleBooking(
                "pending",
                "TP.HCM",
                "Vũng Tàu",
                150,
                "Bến xe Miền Đông",
                "Bến xe Vũng Tàu",
                "2026-01-18 09:00:00",
                2,
                160000
        ));
        samples.add(createSampleBooking(
                "completed",
                "TP.HCM",
                "Đà Lạt",
                360,
                "Bến xe Miền Đông",
                "Bến xe Liên Tỉnh Đà Lạt",
                "2026-01-10 20:30:00",
                1,
                280000
        ));
        samples.add(createSampleBooking(
                "completed",
                "Đà Lạt",
                "Vũng Tàu",
                360,
                "Bến xe Đà Lạt",
                "Bến xe Vũng Tàu",
                "2026-12-10 20:40:00",
                2,
                300000
        ));
        samples.add(createSampleBooking(
                "completed",
                "TP HCM",
                "Gia Lai",
                360,
                "Bến xe Miền Đông",
                "Bến xe Đức Long",
                "2026-11-04 07:40:00",
                3,
                400000
        ));
        samples.add(createSampleBooking(
                "cancelled",
                "TP.HCM",
                "Nghệ An",
                900,
                "Bến xe Miền Đông",
                "Bến xe Vinh",
                "2026-01-05 07:00:00",
                3,
                610000
        ));
        samples.add(createSampleBooking(
                "cancelled",
                "TP.HCM",
                "Thanh Hóa",
                900,
                "Bến xe Miền Đông",
                "Bến xe Thanh Hóa",
                "2026-01-05 07:00:00",
                3,
                500000
        ));
        return samples;
    }

    private Booking createSampleBooking(
            String status,
            String fromCity,
            String toCity,
            int duration,
            String pickupPoint,
            String dropoffPoint,
            String createdAt,
            int seats,
            double price
    ) {
        Route route = new Route();
        route.setDeparture(fromCity);
        route.setDestination(toCity);
        route.setDuration(duration);

        Trip trip = new Trip();
        trip.setRoute(route);

        StopPoint pickup = new StopPoint();
        pickup.setPointName(pickupPoint);

        StopPoint dropoff = new StopPoint();
        dropoff.setPointName(dropoffPoint);

        Booking booking = new Booking();
        booking.setBookingStatus(status);
        booking.setTrip(trip);
        booking.setPickupPoint(pickup);
        booking.setDropoffPoint(dropoff);
        booking.setCreatedAt(createdAt);
        booking.setNumSeats(seats);
        booking.setTotalPrice(price);
        return booking;
    }

    private void selectTab(@NonNull String tab) {
        setTabState(tabActive, TAB_ACTIVE.equals(tab));
        setTabState(tabCompleted, TAB_COMPLETED.equals(tab));
        setTabState(tabCancelled, TAB_CANCELLED.equals(tab));

        List<Booking> filtered = filterByTab(tab);
        bookingAdapter.submitList(filtered);
        tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void setTabState(TextView tab, boolean selected) {
        tab.setBackgroundResource(selected ? R.drawable.bg_chip_booking_selected : R.drawable.bg_chip_booking_unselected);
        tab.setTextColor(getColor(selected ? R.color.white : R.color.text_primary));
    }

    private List<Booking> filterByTab(String tab) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : allBookings) {
            String status = booking.getBookingStatus();
            if (TAB_ACTIVE.equals(tab) && isActiveStatus(status)) {
                result.add(booking);
            } else if (TAB_COMPLETED.equals(tab) && isCompletedStatus(status)) {
                result.add(booking);
            } else if (TAB_CANCELLED.equals(tab) && "cancelled".equalsIgnoreCase(status)) {
                result.add(booking);
            }
        }
        return result;
    }

    private boolean isActiveStatus(String status) {
        return "pending".equalsIgnoreCase(status) || "processing".equalsIgnoreCase(status);
    }

    private boolean isCompletedStatus(String status) {
        return "confirmed".equalsIgnoreCase(status) || "completed".equalsIgnoreCase(status);
    }
}
