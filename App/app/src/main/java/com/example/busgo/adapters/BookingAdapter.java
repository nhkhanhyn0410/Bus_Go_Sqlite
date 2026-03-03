package com.example.busgo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busgo.R;
import com.example.busgo.database.model.Booking;
import com.example.busgo.database.model.Trip;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private static final SimpleDateFormat SOURCE_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat TARGET_DATE = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private final List<Booking> items = new ArrayList<>();

    public void submitList(List<Booking> bookings) {
        items.clear();
        if (bookings != null) {
            items.addAll(bookings);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_ticket, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFromCity;
        private final TextView tvToCity;
        private final TextView tvDuration;
        private final TextView tvPickupPoint;
        private final TextView tvDropoffPoint;
        private final TextView tvDate;
        private final TextView tvPeople;
        private final TextView tvPrice;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFromCity = itemView.findViewById(R.id.tvFromCity);
            tvToCity = itemView.findViewById(R.id.tvToCity);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvPickupPoint = itemView.findViewById(R.id.tvPickupPoint);
            tvDropoffPoint = itemView.findViewById(R.id.tvDropoffPoint);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPeople = itemView.findViewById(R.id.tvPeople);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }

        void bind(Booking booking) {
            Trip trip = booking.getTrip();

            String fromCity = "--";
            String toCity = "--";
            String duration = "--";

            if (trip != null && trip.getRoute() != null) {
                fromCity = trip.getRoute().getDeparture();
                toCity = trip.getRoute().getDestination();
                duration = formatDuration(trip.getRoute().getDuration());
            }

            tvFromCity.setText(fromCity);
            tvToCity.setText(toCity);
            tvDuration.setText(duration);
            tvPickupPoint.setText(booking.getPickupPoint() != null ? booking.getPickupPoint().getPointName() : "--");
            tvDropoffPoint.setText(booking.getDropoffPoint() != null ? booking.getDropoffPoint().getPointName() : "--");
            tvDate.setText(formatDate(booking.getCreatedAt()));
            tvPeople.setText(booking.getNumSeats() + " người");
            tvPrice.setText(formatCurrency(booking.getTotalPrice()));
        }

        private static String formatDate(String rawDate) {
            if (rawDate == null || rawDate.isEmpty()) {
                return "--";
            }
            try {
                Date date = SOURCE_DATE.parse(rawDate);
                if (date == null) return rawDate;
                return TARGET_DATE.format(date);
            } catch (ParseException e) {
                return rawDate;
            }
        }

        private static String formatDuration(int minutes) {
            if (minutes <= 0) {
                return "--";
            }
            int hours = minutes / 60;
            int remain = minutes % 60;
            if (hours == 0) {
                return remain + " phút";
            }
            if (remain == 0) {
                return hours + " giờ";
            }
            return hours + " giờ " + remain + " phút";
        }

        private static String formatCurrency(double amount) {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            return numberFormat.format(Math.round(amount)) + " VNĐ";
        }
    }
}
