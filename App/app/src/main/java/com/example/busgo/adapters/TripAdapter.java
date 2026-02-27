package com.example.busgo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busgo.R;
import com.example.busgo.database.model.Trip;

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

    public void updateData(List<Trip> newTrips) {
        this.tripList = newTrips;
        notifyDataSetChanged();
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

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
