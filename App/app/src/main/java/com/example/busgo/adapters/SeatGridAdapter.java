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

    // Loại ô trong grid
    public static final int TYPE_SEAT = 0;
    public static final int TYPE_EMPTY = 1;
    public static final int TYPE_DRIVER = 2;
    public static final int TYPE_WC = 3;

    private Context context;
    private List<SeatItem> items;
    private LayoutInflater inflater;
    private OnSeatClickListener listener;
    private boolean isBedBus; // true = giường nằm (chữ nhật), false = ghế ngồi (vuông)

    /**
     * Wrapper cho mỗi ô trong grid
     */
    public static class SeatItem {
        public int type;
        public Seat seat; // null nếu type != TYPE_SEAT

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

    /**
     * Cập nhật dữ liệu grid (dùng khi chuyển tầng)
     */
    public void updateData(List<SeatItem> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
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

        // Điều chỉnh chiều cao theo loại xe
        holder.tvSeatNumber.post(() -> {
            int width = holder.tvSeatNumber.getWidth();
            if (width > 0) {
                ViewGroup.LayoutParams params = holder.tvSeatNumber.getLayoutParams();
                if (isBedBus) {
                    // Ghế giường nằm: hình chữ nhật
                    params.height = (int) (width * 1.5);
                } else {
                    // Ghế ngồi: hình vuông
                    params.height = width;
                }
                holder.tvSeatNumber.setLayoutParams(params);
            }
        });

        int white = ContextCompat.getColor(context, R.color.white);
        int textPrimary = ContextCompat.getColor(context, R.color.text_primary);

        switch (item.type) {
            case TYPE_EMPTY:
                // Ô trống: ẩn hoàn toàn
                holder.tvSeatNumber.setVisibility(View.INVISIBLE);
                holder.tvSeatNumber.setOnClickListener(null);
                break;

            case TYPE_DRIVER:
                // Ô tài xế
                holder.tvSeatNumber.setVisibility(View.VISIBLE);
                holder.tvSeatNumber.setText("Tài xế");
                holder.tvSeatNumber.setTextSize(10);
                holder.tvSeatNumber.setBackgroundResource(R.drawable.bg_seat_special);
                holder.tvSeatNumber.setTextColor(white);
                holder.tvSeatNumber.setEnabled(false);
                holder.tvSeatNumber.setOnClickListener(null);
                break;

            case TYPE_WC:
                // Ô WC
                holder.tvSeatNumber.setVisibility(View.VISIBLE);
                holder.tvSeatNumber.setText("WC");
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
                    // Ghế đã đặt: đỏ, không click được
                    holder.tvSeatNumber.setBackgroundResource(R.drawable.bg_seat_booked);
                    holder.tvSeatNumber.setTextColor(white);
                    holder.tvSeatNumber.setEnabled(false);
                    holder.tvSeatNumber.setOnClickListener(null);
                } else if (seat.isSelected()) {
                    // Ghế đang chọn: xanh accent
                    holder.tvSeatNumber.setBackgroundResource(R.drawable.bg_seat_selected);
                    holder.tvSeatNumber.setTextColor(white);
                    holder.tvSeatNumber.setEnabled(true);
                    holder.tvSeatNumber.setOnClickListener(v -> {
                        if (listener != null) listener.onSeatClick(seat);
                    });
                } else {
                    // Ghế trống: trắng + viền
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
