package com.example.busgo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busgo.R;
import com.example.busgo.database.model.Trip;
import com.example.busgo.until.DateUtils;
import com.example.busgo.until.PriceCalculator;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> tripList;
    private OnTripClickListener listener;

    public interface OnTripClickListener {
        void onTripClick(Trip trip);
    }

    public TripAdapter(List<Trip> tripList) {
        this.tripList = tripList;
    }

    public void setOnTripClickListener(OnTripClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip_search, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = tripList.get(position);
        holder.bind(trip);
    }

    @Override
    public int getItemCount() {
        return tripList != null ? tripList.size() : 0;
    }

    class TripViewHolder extends RecyclerView.ViewHolder {

        TextView tvCompanyName, tvBusType, tvBusNumber, tvRating;
        TextView tvDepartureTime, tvArrivalTime, tvDuration;
        TextView tvPickupPoint, tvDropoffPoint;
        TextView tvStopsCount, tvAvailableSeats, tvPrice;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvBusType = itemView.findViewById(R.id.tvBusType);
            tvBusNumber = itemView.findViewById(R.id.tvBusNumber);
            tvRating = itemView.findViewById(R.id.tvRating);

            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvArrivalTime = itemView.findViewById(R.id.tvArrivalTime);
            tvDuration = itemView.findViewById(R.id.tvDuration);

            tvPickupPoint = itemView.findViewById(R.id.tvPickupPoint);
            tvDropoffPoint = itemView.findViewById(R.id.tvDropoffPoint);

            tvStopsCount = itemView.findViewById(R.id.tvStopsCount);
            tvAvailableSeats = itemView.findViewById(R.id.tvAvailableSeats);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }

        public void bind(Trip trip) {
            String companyName = trip.getBus().getCompanyName();

            tvCompanyName.setText(companyName);

            String busType = trip.getBus().getBusType();
            String busModel = trip.getBus().getBusModel();
            if (busModel != null && !busModel.isEmpty()) {
                tvBusType.setText(busModel + " (" + busType + ")");
            } else {
                tvBusType.setText(busType);
            }
            tvBusNumber.setText(trip.getBus().getBusNumber());

            double rating = trip.getBus().getRating();
            tvRating.setText(String.format("%.1f", rating));

            tvDepartureTime.setText(DateUtils.formatTime(trip.getDepartureTime()));
            tvArrivalTime.setText(DateUtils.formatTime(trip.getArrivalTime()));

            String duration = DateUtils.calculateDuration(
                    trip.getDepartureTime(),
                    trip.getArrivalTime()
            );
            tvDuration.setText(duration);

            tvPickupPoint.setText(trip.getRoute().getDeparture());
            tvDropoffPoint.setText(trip.getRoute().getDestination());

            int stopsCount = trip.getStopsCount();
            tvStopsCount.setText(String.valueOf(stopsCount));

            int availableSeats = trip.getAvailableSeats();
            tvAvailableSeats.setText(String.valueOf(availableSeats));

            tvPrice.setText(PriceCalculator.formatPrice(trip.getBasePrice()));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTripClick(trip);
                }
            });
        }
    }
}