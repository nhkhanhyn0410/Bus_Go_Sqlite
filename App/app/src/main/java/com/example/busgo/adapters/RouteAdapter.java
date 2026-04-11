package com.example.busgo.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busgo.R;
import com.example.busgo.database.model.Route;
import com.example.busgo.until.PriceCalculator;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder> {

    private Context context;
    private List<Route> routes;
    private OnRouteClickListener listener;

    public interface OnRouteClickListener {
        void onRouteClick(Route route);
    }

    public RouteAdapter(Context context, List<Route> routes) {
        this.context = context;
        this.routes = routes;
    }

    public void setOnRouteClickListener(OnRouteClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Route> newRoutes) {
        this.routes = newRoutes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_trip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Route route = routes.get(position);

                // Set departure và destination
        holder.tvDeparture.setText(route.getDeparture());
        holder.tvDestination.setText(route.getDestination());

        // Set giá gốc (gạch ngang)
        double originalPrice = route.getDistance() * 1000 * 1.2; // Example original price calculation
        holder.tvOriginalPrice.setText(PriceCalculator.formatPrice(originalPrice));
        holder.tvOriginalPrice.setPaintFlags(holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // Set giá hiện tại
        double currentPrice = route.getDistance() * 1000; // Example current price calculation
        holder.tvCurrentPrice.setText(PriceCalculator.formatPrice(currentPrice));

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRouteClick(route);
            }
        });
    }

    @Override
    public int getItemCount() {
        return routes != null ? routes.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeparture;
        TextView tvDestination;
        TextView tvOriginalPrice;
        TextView tvCurrentPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeparture = itemView.findViewById(R.id.tvDeparture);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvCurrentPrice = itemView.findViewById(R.id.tvCurrentPrice);
        }
    }
}

