package com.example.busgo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busgo.R;
import com.example.busgo.activities.user.BookingDetailActivity;
import com.example.busgo.database.DAO.StopPointDAO;
import com.example.busgo.database.DAO.TripDAO;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.Booking;
import com.example.busgo.database.model.Route;
import com.example.busgo.database.model.StopPoint;
import com.example.busgo.database.model.Trip;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private Context context;
    private List<Booking> bookings;
    private TripDAO tripDAO;
    private StopPointDAO stopPointDAO;

    public BookingAdapter(Context context, List<Booking> bookings) {
        this.context = context;
        this.bookings = bookings != null ? bookings : new ArrayList<>();
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        this.tripDAO = new TripDAO(dbHelper);
        this.stopPointDAO = new StopPointDAO(dbHelper);
    }

    /**
     * Cập nhật danh sách booking (dùng khi lọc theo tab)
     */
    public void updateData(List<Booking> newBookings) {
        this.bookings = newBookings != null ? newBookings : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking_ticket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookings.get(position);

        // Load trip và route info
        Trip trip = tripDAO.getTripById(booking.getTripId());
        if (trip != null && trip.getRoute() != null) {
            Route route = trip.getRoute();
            holder.tvDeparture.setText(route.getDeparture());
            holder.tvDestination.setText(route.getDestination());

            // Tính thời gian di chuyển
            if (route.getDuration() > 0) {
                int hours = route.getDuration() / 60;
                int mins = route.getDuration() % 60;
                String duration = mins > 0 ? "~" + hours + "h" + mins + "'" : "~" + hours + " giờ";
                holder.tvDuration.setText(duration);
            }

            // Ngày khởi hành (lấy phần ngày từ departure_time)
            String departureTime = trip.getDepartureTime();
            if (departureTime != null && departureTime.length() >= 10) {
                holder.tvDate.setText(departureTime.substring(0, 10));
            }
        }

        // Load điểm đón
        StopPoint pickup = stopPointDAO.getStopPointById(booking.getPickupPointId());
        if (pickup != null) {
            holder.tvPickupStation.setText(pickup.getPointName());
        }

        // Load điểm trả
        StopPoint dropoff = stopPointDAO.getStopPointById(booking.getDropoffPointId());
        if (dropoff != null) {
            holder.tvDropoffStation.setText(dropoff.getPointName());
        }

        // Số hành khách
        holder.tvPassengers.setText(booking.getNumSeats() + " người");

        // Giá vé
        holder.tvPrice.setText(String.format("%,d VNĐ", (long) booking.getTotalPrice()));

        // Click để xem chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingDetailActivity.class);
            intent.putExtra("booking_code", booking.getBookingCode());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvDeparture, tvDestination, tvDuration;
        TextView tvPickupStation, tvDropoffStation;
        TextView tvDate, tvPassengers, tvPrice;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvDeparture = itemView.findViewById(R.id.tvDeparture);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvPickupStation = itemView.findViewById(R.id.tvPickupStation);
            tvDropoffStation = itemView.findViewById(R.id.tvDropoffStation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPassengers = itemView.findViewById(R.id.tvPassengers);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}