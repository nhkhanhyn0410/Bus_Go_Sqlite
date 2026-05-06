package com.example.busgo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.busgo.R;
import com.example.busgo.database.model.Seat;

import java.util.ArrayList;
import java.util.List;

public class SeatGridAdapter extends BaseAdapter {
    public static final int TYPE_SEAT = 0;
    public static final int TYPE_EMPTY = 1;
    public static final int TYPE_DRIVER = 2;

    private Context context;
    private List<SeatItem> items;
    private LayoutInflater inflater;
    private OnSeatClickListener listener;
    private boolean isBedBus;


    public static class SeatItem {
        public int type;
        public Seat seat;

        public SeatItem(int type) {
            this.type = type;
            this.seat = null;
        }

        public SeatItem(int type, Seat seat) {
            this.type = type;
            this.seat = seat;
        }
    }

    public interface OnSeatClickListener {
        void onSeatClick(Seat seat);
    }

    public SeatGridAdapter(Context context, List<SeatItem> items, boolean isBedBus) {
        this.context = context;
        this.items = items != null ? items : new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
        this.isBedBus = isBedBus;
    }

    public void setOnSeatClickListener(OnSeatClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_seat, parent, false);
            holder = new ViewHolder();
            holder.container = (FrameLayout) convertView;
            holder.tvSeatNumber = convertView.findViewById(R.id.tvSeatNumber);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SeatItem item = items.get(position);

        int white = ContextCompat.getColor(context, R.color.white);
        int textPrimary = ContextCompat.getColor(context, R.color.text_primary);

        switch (item.type) {
            case TYPE_EMPTY:
                holder.tvSeatNumber.setVisibility(View.INVISIBLE);
                holder.tvSeatNumber.setOnClickListener(null);
                break;

            case TYPE_DRIVER:
                holder.tvSeatNumber.setVisibility(View.VISIBLE);
                holder.tvSeatNumber.setText("Tài xế");
                holder.tvSeatNumber.setTextSize(10);
                holder.tvSeatNumber.setBackgroundResource(R.drawable.bg_seat_special);
                holder.tvSeatNumber.setTextColor(white);
                holder.tvSeatNumber.setEnabled(false);
                holder.tvSeatNumber.setOnClickListener(null);
                break;

            case TYPE_SEAT:
                holder.tvSeatNumber.setVisibility(View.VISIBLE);
                holder.tvSeatNumber.setTextSize(12);
                Seat seat = item.seat;
                holder.tvSeatNumber.setText(seat.getSeatNumber());

                if (seat.isBooked()) {
                    holder.tvSeatNumber.setBackgroundResource(R.drawable.bg_seat_booked);
                    holder.tvSeatNumber.setTextColor(white);
                    holder.tvSeatNumber.setEnabled(false);
                    holder.tvSeatNumber.setOnClickListener(null);
                } else if (seat.isSelected()) {
                    holder.tvSeatNumber.setBackgroundResource(R.drawable.bg_seat_selected);
                    holder.tvSeatNumber.setTextColor(white);
                    holder.tvSeatNumber.setEnabled(true);
                    holder.tvSeatNumber.setOnClickListener(v -> {
                        if (listener != null) listener.onSeatClick(seat);
                    });
                } else {
                    holder.tvSeatNumber.setBackgroundResource(R.drawable.bg_seat_available);
                    holder.tvSeatNumber.setTextColor(textPrimary);
                    holder.tvSeatNumber.setEnabled(true);
                    holder.tvSeatNumber.setOnClickListener(v -> {
                        if (listener != null) listener.onSeatClick(seat);
                    });
                }
                break;
        }

        return convertView;
    }

    static class ViewHolder {
        FrameLayout container;
        TextView tvSeatNumber;
    }
}
