package com.example.busgo.activities.user;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.busgo.R;
import com.example.busgo.adapters.SeatGridAdapter;
import com.example.busgo.database.DAO.SeatDAO;
import com.example.busgo.database.DAO.TripDAO;
import com.example.busgo.database.DatabaseHelper;
import com.example.busgo.database.model.Bus;
import com.example.busgo.database.model.Route;
import com.example.busgo.database.model.Seat;
import com.example.busgo.database.model.Trip;
import com.example.busgo.until.DateUtils;
import com.example.busgo.adapters.SeatGridAdapter.SeatItem;
import com.example.busgo.until.ExpandableGridView;

import java.util.ArrayList;
import java.util.List;

public class SeatSelectionActivity extends AppCompatActivity {

    // Giới hạn số ghế tối đa mỗi lần đặt
    private static final int MAX_SEATS = 6;

    // Header UI
    private ImageView btnBack;
    private TextView tvCompanyName, tvDepartureTime, tvArrivalTime, tvDuration;
    private TextView tvDepartureStation, tvArrivalStation, tvDate, tvRating;
    private LinearLayout lnBottomNav;

    // Seat grids
    private LinearLayout layoutFloorLabels, layoutDualGrid;
    private ExpandableGridView gridFloor1, gridFloor2; // Xe giường: 2 grid cạnh nhau
    private ExpandableGridView gridSeats;              // Xe ngồi: 1 grid duy nhất

    // Bottom bar
    private TextView tvSelectedSeatsLabel, tvSelectedSeatsValue;
    private Button btnConfirm;

    // Intent data
    private int tripId;
    private int routeId;
    private int pickupPointId;
    private int dropoffPointId;
    private String pickupTime, pickupPointName;
    private String dropoffTime, dropoffPointName;
    private double basePrice;
    private String busLayout;

    // Data
    private Trip trip;
    private List<Seat> allSeats;
    private List<Seat> floor1Seats;
    private List<Seat> floor2Seats;
    private List<String> selectedSeats;
    private SeatGridAdapter adapterFloor1, adapterFloor2, adapterSingle;
    private boolean isBedBus;
    private int numColumns;

    // DAOs
    private SeatDAO seatDAO;
    private TripDAO tripDAO;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seat_selection);



        initViews();
        getDataFromIntent();
        initDatabase();
        loadTripData();
        loadSeats();
        displayHeader();
        setupSeatGrid();
        setupListeners();
    }

    private void initViews() {
        // Header
        btnBack = findViewById(R.id.btnBack);
        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvDepartureTime = findViewById(R.id.tvDepartureTime);
        tvArrivalTime = findViewById(R.id.tvArrivalTime);
        tvDuration = findViewById(R.id.tvDuration);
        tvDepartureStation = findViewById(R.id.tvDepartureStation);
        tvArrivalStation = findViewById(R.id.tvArrivalStation);
        tvDate = findViewById(R.id.tvDate);
        tvRating = findViewById(R.id.tvRating);

        // Floor grids
        layoutFloorLabels = findViewById(R.id.layoutFloorLabels);
        layoutDualGrid = findViewById(R.id.layoutDualGrid);
        gridFloor1 = findViewById(R.id.gridFloor1);
        gridFloor2 = findViewById(R.id.gridFloor2);
        gridSeats = findViewById(R.id.gridSeats);

        // Bottom bar
        lnBottomNav = findViewById(R.id.lnBottomNav);
        tvSelectedSeatsLabel = findViewById(R.id.tvSelectedSeatsLabel);
        tvSelectedSeatsValue = findViewById(R.id.tvSelectedSeatsValue);
        btnConfirm = findViewById(R.id.btnConfirm);

        final int originalbtnlnBottomNav = ((ViewGroup.MarginLayoutParams) lnBottomNav.getLayoutParams()).bottomMargin;
        ViewCompat.setOnApplyWindowInsetsListener(lnBottomNav, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.bottomMargin = originalbtnlnBottomNav + insets.bottom;
            view.setLayoutParams(params);
            return windowInsets;
        });

        selectedSeats = new ArrayList<>();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        tripId = intent.getIntExtra("trip_id", -1);
        routeId = intent.getIntExtra("route_id", -1);
        pickupPointId = intent.getIntExtra("pickup_point_id", -1);
        dropoffPointId = intent.getIntExtra("dropoff_point_id", -1);
        pickupTime = intent.getStringExtra("pickup_time");
        pickupPointName = intent.getStringExtra("pickup_point_name");
        dropoffTime = intent.getStringExtra("dropoff_time");
        dropoffPointName = intent.getStringExtra("dropoff_point_name");
        basePrice = intent.getDoubleExtra("base_price", 0);
        busLayout = intent.getStringExtra("bus_layout");

        // Xác định loại xe
        isBedBus = busLayout != null && busLayout.equals("2-1");
        numColumns = isBedBus ? 3 : 4;

        if (tripId == -1) {
            Toast.makeText(this, "Lỗi: Không có thông tin chuyến", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initDatabase() {
        dbHelper = DatabaseHelper.getInstance(this);
        seatDAO = new SeatDAO(dbHelper);
        tripDAO = new TripDAO(dbHelper);
    }

    /**
     * Load Trip với đầy đủ thông tin Route + Bus cho header
     */
    private void loadTripData() {
        trip = tripDAO.getTripById(tripId);

        // Nếu chưa có busLayout từ intent, lấy từ Trip
        if (busLayout == null && trip != null && trip.getBus() != null) {
            busLayout = trip.getBus().getSeatLayout();
            isBedBus = "2-1".equals(busLayout);
            numColumns = isBedBus ? 3 : 4;
        }
    }

    private void loadSeats() {
        allSeats = seatDAO.getSeatsByTripId(tripId);
        if (allSeats == null || allSeats.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin ghế", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Chia tầng cho xe giường nằm
        if (isBedBus) {
            int mid = allSeats.size() / 2;
            floor1Seats = new ArrayList<>(allSeats.subList(0, mid));
            floor2Seats = new ArrayList<>(allSeats.subList(mid, allSeats.size()));
        }
    }

    // ==================== HEADER ====================

    private void displayHeader() {
        if (trip == null) return;

        Bus bus = trip.getBus();
        Route route = trip.getRoute();

        // Tên nhà xe
        if (bus != null && bus.getCompanyName() != null) {
            tvCompanyName.setText(bus.getCompanyName());
        } else {
            tvCompanyName.setText("Nhà xe");
        }

        // Giờ đi / đến
        tvDepartureTime.setText(formatTimeOnly(trip.getDepartureTime()));
        tvArrivalTime.setText(formatTimeOnly(trip.getArrivalTime()));

        // Thời gian di chuyển
        if (route != null && route.getDuration() > 0) {
            int hours = route.getDuration() / 60;
            int mins = route.getDuration() % 60;
            String dur = mins > 0 ? hours + "h " + mins + "'" : hours + " giờ";
            tvDuration.setText(dur);
        }

        // Bến xe
        if (route != null) {
            tvDepartureStation.setText(route.getDeparture());
            tvArrivalStation.setText(route.getDestination());
        }

        // Ngày khởi hành
        tvDate.setText(DateUtils.formatDate(trip.getDepartureTime()));

        // Rating
        if (bus != null) {
            tvRating.setText(String.valueOf(bus.getRating()));
        }
    }

    /**
     * Trích xuất giờ từ datetime "yyyy-MM-dd HH:mm" → "HH.mm"
     */
    private String formatTimeOnly(String dateTime) {
        if (dateTime == null || dateTime.length() < 16) return "--:--";
        try {
            String time = dateTime.substring(11, 16);
            return time.replace(":", ".");
        } catch (Exception e) {
            return "--:--";
        }
    }

    // ==================== SEAT GRID ====================

    /**
     * Thiết lập grid ghế:
     * - Xe giường (2-1): 2 grid cạnh nhau (Tầng 1 trái, Tầng 2 phải)
     * - Xe ngồi (2-2): 1 grid duy nhất
     */
    private void setupSeatGrid() {
        if (isBedBus) {
            // Hiện layout 2 tầng cạnh nhau
            layoutFloorLabels.setVisibility(View.VISIBLE);
            layoutDualGrid.setVisibility(View.VISIBLE);
            gridSeats.setVisibility(View.GONE);

            // Tầng 1
            List<SeatItem> grid1 = buildSeatGrid(floor1Seats);
            adapterFloor1 = new SeatGridAdapter(this, grid1, true);
            adapterFloor1.setOnSeatClickListener(this::handleSeatClick);
            gridFloor1.setAdapter(adapterFloor1);

            // Tầng 2
            List<SeatItem> grid2 = buildSeatGrid(floor2Seats);
            adapterFloor2 = new SeatGridAdapter(this, grid2, true);
            adapterFloor2.setOnSeatClickListener(this::handleSeatClick);
            gridFloor2.setAdapter(adapterFloor2);
        } else {
            // Hiện 1 grid duy nhất
            layoutFloorLabels.setVisibility(View.GONE);
            layoutDualGrid.setVisibility(View.GONE);
            gridSeats.setVisibility(View.VISIBLE);
            gridSeats.setNumColumns(numColumns);

            List<SeatItem> grid = buildSeatGrid(allSeats);
            adapterSingle = new SeatGridAdapter(this, grid, false);
            adapterSingle.setOnSeatClickListener(this::handleSeatClick);
            gridSeats.setAdapter(adapterSingle);
        }
    }

    /**
     * Xây dựng danh sách SeatItem cho grid
     * Hàng đầu: Tài xế + ô trống + mũi tên (back)
     * Hàng ghế: các ghế từ DB
     * Hàng cuối (xe giường): ghế + WC + ghế
     */
    private List<SeatItem> buildSeatGrid(List<Seat> seats) {
        List<SeatItem> grid = new ArrayList<>();

        // Hàng đầu: Tài xế + trống + trống (xe ngồi 4 cột) hoặc Tài xế + trống + trống (3 cột)
        grid.add(new SeatItem(SeatGridAdapter.TYPE_DRIVER));
        for (int i = 1; i < numColumns; i++) {
            grid.add(new SeatItem(SeatGridAdapter.TYPE_EMPTY));
        }

        if (isBedBus) {
            // Xe giường: ghế sắp xếp thành grid 3 cột
            // Mỗi tầng có (totalSeats) ghế, chia thành 3 cột
            // Cột 1: vị trí 0, 1, 2... (bên trái)
            // Cột 2: vị trí ở giữa
            // Cột 3: bên phải
            int seatsPerColumn = (int) Math.ceil(seats.size() / 2.0);

            // Sắp xếp ghế theo hàng: [col1_row1, col2_row1, col3_row1, col1_row2, ...]
            // Dữ liệu từ DB đã sắp xếp theo seat_number
            // Ví dụ: A1, A2, A3, ... A17 → phải xếp thành 3 cột x nhiều hàng
            int leftCount = seatsPerColumn;         // Cột trái
            int rightCount = seats.size() - leftCount; // Cột phải (có thể ít hơn)

            // Tính số hàng dựa trên tổng ghế: ghế cuối hàng chứa WC ở giữa
            // Layout: cột trái (leftCount ghế) | cột giữa (rightCount-1 ghế + WC) | cột phải (rightCount ghế)
            // Thực tế đơn giản: dùng trực tiếp thứ tự từ DB
            for (Seat seat : seats) {
                grid.add(new SeatItem(SeatGridAdapter.TYPE_SEAT, seat));
            }

            // Thêm WC ở cuối nếu cần
            int remainder = grid.size() % numColumns;
            if (remainder == 1) {
                // Thêm WC ở giữa + 1 empty
                grid.add(new SeatItem(SeatGridAdapter.TYPE_WC));
                grid.add(new SeatItem(SeatGridAdapter.TYPE_EMPTY));
            } else if (remainder == 2) {
                // Thêm WC ở cuối
                grid.add(new SeatItem(SeatGridAdapter.TYPE_WC));
            } else if (remainder == 0) {
                // Hàng mới cho WC: empty + WC + empty
                grid.add(new SeatItem(SeatGridAdapter.TYPE_EMPTY));
                grid.add(new SeatItem(SeatGridAdapter.TYPE_WC));
                grid.add(new SeatItem(SeatGridAdapter.TYPE_EMPTY));
            }
        } else {
            // Xe ngồi: thêm ghế theo thứ tự
            for (Seat seat : seats) {
                grid.add(new SeatItem(SeatGridAdapter.TYPE_SEAT, seat));
            }
        }

        return grid;
    }

    // ==================== SEAT CLICK ====================

    /**
     * Xử lý click ghế: toggle chọn/bỏ chọn
     * Giới hạn tối đa MAX_SEATS ghế
     */
    private void handleSeatClick(Seat seat) {
        if (seat.isBooked()) {
            Toast.makeText(this, "Ghế đã được đặt", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedSeats.contains(seat.getSeatNumber())) {
            // Bỏ chọn
            selectedSeats.remove(seat.getSeatNumber());
            seat.setSelected(false);
        } else {
            // Kiểm tra giới hạn
            if (selectedSeats.size() >= MAX_SEATS) {
                Toast.makeText(this,
                        "Chỉ được chọn tối đa " + MAX_SEATS + " ghế mỗi lần đặt",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            selectedSeats.add(seat.getSeatNumber());
            seat.setSelected(true);
        }

        // Cập nhật UI
        updateBottomBar();
        notifyAllAdapters();
    }

    /**
     * Cập nhật tất cả adapter (cả 2 tầng hoặc single)
     */
    private void notifyAllAdapters() {
        if (isBedBus) {
            if (adapterFloor1 != null) adapterFloor1.notifyDataSetChanged();
            if (adapterFloor2 != null) adapterFloor2.notifyDataSetChanged();
        } else {
            if (adapterSingle != null) adapterSingle.notifyDataSetChanged();
        }
    }

    /**
     * Cập nhật bottom bar: ghế đã chọn
     */
    private void updateBottomBar() {
        int numSeats = selectedSeats.size();

        if (numSeats == 0) {
            tvSelectedSeatsLabel.setText("Ghế đã chọn:");
            tvSelectedSeatsValue.setText("Chưa chọn");
            btnConfirm.setEnabled(false);
        } else {
            tvSelectedSeatsLabel.setText("Ghế đã chọn:");
            tvSelectedSeatsValue.setText(String.join(", ", selectedSeats));
            btnConfirm.setEnabled(true);
            btnConfirm.setBackgroundTintList(
                    ColorStateList.valueOf(
                            ContextCompat.getColor(this, R.color.accent)
                    )
            );
        }
    }

    // ==================== LISTENERS ====================

    private void setupListeners() {
        // Nút Back
        btnBack.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {

            Intent intent = new Intent(SeatSelectionActivity.this, PassengerInfoActivity.class);
            intent.putExtra("trip_id", tripId);
            intent.putExtra("route_id", routeId);
            intent.putExtra("pickup_point_id", pickupPointId);
            intent.putExtra("dropoff_point_id", dropoffPointId);
            intent.putExtra("pickup_time", pickupTime);
            intent.putExtra("pickup_point_name", pickupPointName);
            intent.putExtra("dropoff_time", dropoffTime);
            intent.putExtra("dropoff_point_name", dropoffPointName);
            intent.putExtra("base_price", basePrice);
            intent.putStringArrayListExtra("seat_numbers", new ArrayList<>(selectedSeats));
            intent.putExtra("total_price", selectedSeats.size() * basePrice);
            startActivity(intent);
        });
    }


}