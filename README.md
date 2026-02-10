# BusGo - Ứng Dụng Đặt Vé Xe Khách

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)
![Material Design](https://img.shields.io/badge/Material%20Design%203-757575?style=for-the-badge&logo=material-design&logoColor=white)

**Ứng dụng Android đặt vé xe khách trực tuyến**

</div>

---

## Giới Thiệu

**BusGo** là ứng dụng Android được xây dựng bằng **Java** với **SQLite** cục bộ. Ứng dụng cung cấp trải nghiệm đặt vé xe khách hoàn chỉnh: tìm kiếm chuyến xe, chọn điểm đón/trả, chọn ghế trên sơ đồ xe, thanh toán và quản lý vé điện tử.

- **Học tập**: Dự án môn Phát triển ứng dụng di động - TDMU
- **Độc lập**: Hoạt động hoàn toàn không cần máy chủ

---

## Tính Năng Chính

### Xác Thực
- Đăng ký tài khoản với kiểm tra dữ liệu đầy đủ
- Đăng nhập bằng số điện thoại hoặc email
- Xác thực OTP 6 số (giả lập)
- Quản lý phiên đăng nhập (SharedPreferences)
- Mã hóa mật khẩu SHA-256

### Tìm Kiếm Chuyến Xe
- Tìm theo điểm đi, điểm đến, ngày khởi hành
- Tab xem chuyến trong 5 ngày liên tiếp
- Bộ lọc: WiFi, WC, giường nằm, sạc điện
- Xem chi tiết chuyến (tuyến đường, xe khách, giá, ghế trống)

### Đặt Vé
- Chọn điểm đón với giờ thực tế (giờ khởi hành + độ lệch)
- Chọn điểm trả với giờ thực tế (giờ đến + độ lệch)
- Sơ đồ ghế tương tác: layout 2-2 (ghế ngồi, 45 ghế) và 2-1 (giường nằm, 40 giường)
- Nhập thông tin hành khách (tự điền từ tài khoản đăng nhập)
- Xác nhận đặt vé với tổng quan đầy đủ

### Thanh Toán
- **Tiền mặt** — thanh toán khi lên xe
- **MoMo** — tích hợp SDK (chế độ sandbox)
- Theo dõi trạng thái thanh toán (chưa trả / đã trả / hoàn tiền)

### Quản Lý Vé
- Mã QR tự động tạo cho mỗi vé (ZXing)
- Lịch sử đặt vé
- Xem chi tiết vé (tuyến, ghế, điểm đón/trả, thanh toán)
- Hủy vé với giao dịch an toàn (Transaction)

### Hồ Sơ
- Xem thông tin cá nhân
- Đăng xuất

---

## Kiến Trúc

### Mô Hình 3 Lớp

```
Tầng Trình Bày    →  Activities (21) + Adapters (9) + XML Layouts
Tầng Nghiệp Vụ    →  DAOs (5) + Utils (8) + Payment (2)
Tầng Dữ Liệu      →  Models (11) + DatabaseHelper + DataHelpers (4) → SQLite
```

### Design Patterns

| Pattern | Áp dụng |
|---------|---------|
| Singleton | DatabaseHelper, SessionManager |
| DAO | UserDAO, TripDAO, SeatDAO, StopPointDAO, BookingDAO |
| ViewHolder | Tất cả Adapters |
| Transaction | Tạo/hủy vé (3 bảng cùng lúc) |
| Callback | Xử lý thanh toán MoMo |

---

## Cơ Sở Dữ Liệu (7 Bảng)

```
routes ──┐                    users ──┐
         ├──► trips ──► seats         ├──► bookings
buses  ──┘            └──► bookings ◄─┘
routes ──► stop_points ──────► bookings (điểm đón/trả)
```

| Bảng | Mô tả |
|------|-------|
| `users` | Tài khoản người dùng |
| `routes` | Tuyến đường (điểm đi, điểm đến, khoảng cách, thời gian) |
| `buses` | Xe khách (biển số, loại xe, số ghế, bố trí ghế) |
| `trips` | Chuyến đi (liên kết tuyến + xe, giờ đi/đến, giá, ghế trống) |
| `stop_points` | Điểm dừng thống nhất: đón (pickup), trả (dropoff), nghỉ (rest_stop) |
| `bookings` | Đơn đặt vé (hành khách, ghế, điểm đón/trả, thanh toán) |
| `seats` | Trạng thái ghế theo chuyến |

---

## Luồng Đặt Vé

```
Trang chủ → Tìm kiếm → Danh sách chuyến → Chi tiết chuyến
  → Chọn điểm đón → Chọn điểm trả → Chọn ghế
    → Nhập thông tin hành khách → Xác nhận đặt vé
      → Chọn thanh toán → Kết quả đặt vé (Mã QR + Mã vé)
```

Khi tạo/hủy vé, hệ thống sử dụng **Transaction** đảm bảo tính toàn vẹn:
1. Tạo/cập nhật đơn trong bảng `bookings`
2. Đánh dấu/giải phóng ghế trong bảng `seats`
3. Giảm/tăng bộ đếm ghế trống trong bảng `trips`

---

## Cấu Trúc Thư Mục

```
app/src/main/java/com/tdmu/vexenhanh/
│
├── activities/
│   ├── auth/           ← Đăng nhập, Đăng ký, OTP, Hoàn tất đăng ký
│   ├── common/         ← Splash, Kiểm tra cơ sở dữ liệu
│   └── user/           ← 15 màn hình chức năng chính
│
├── adapters/           ← 9 Adapter (Trip, Seat, Booking, Pickup, Dropoff, Payment...)
│
├── database/
│   ├── DatabaseHelper  ← Singleton, tạo 7 bảng, dữ liệu mẫu
│   ├── dao/            ← UserDAO, TripDAO, SeatDAO, StopPointDAO, BookingDAO
│   ├── helpers/        ← RouteDataHelper, BusDataHelper, StopPointDataHelper, TripDataHelper
│   └── models/         ← User, Route, Bus, Trip, Seat, StopPoint, Booking...
│
├── payment/            ← MoMoConfig, MoMoPayment (sandbox)
│
└── utils/              ← Constants, SessionManager, DateUtils, ValidationUtils,
                          PriceCalculator, QRCodeGenerator, BottomNavigationHelper
```

---

## Các Màn Hình (21 màn)

### Xác Thực (4 màn + 1 Splash)
| Màn hình | Chức năng |
|----------|-----------|
| SplashActivity | Khởi động 2 giây, kiểm tra phiên |
| LoginActivity | Đăng nhập bằng SĐT/email |
| RegisterActivity | Đăng ký tài khoản mới |
| VerificationActivity | Xác thực OTP 6 số |
| ConfirmInfoActivity | Hoàn tất thông tin (họ tên, ngày sinh) |

### Luồng Đặt Vé (12 màn)
| Màn hình | Chức năng |
|----------|-----------|
| MainActivity | Trang chủ, tìm kiếm nhanh, tuyến phổ biến |
| SearchTripActivity | Tìm kiếm nâng cao |
| TripListActivity | Danh sách chuyến + bộ lọc + tab ngày |
| TripDetailActivity | Chi tiết chuyến đi |
| PickupPointActivity | Chọn điểm đón |
| DropoffPointActivity | Chọn điểm trả |
| SeatSelectionActivity | Sơ đồ ghế tương tác |
| PassengerInfoActivity | Nhập thông tin hành khách |
| BookingConfirmActivity | Xác nhận đặt vé |
| PaymentMethodActivity | Chọn phương thức thanh toán |
| PaymentProcessActivity | Xử lý thanh toán MoMo |
| BookingSuccessActivity | Kết quả + mã QR |

### Quản Lý Vé & Hồ Sơ (3 màn)
| Màn hình | Chức năng |
|----------|-----------|
| BookingHistoryActivity | Lịch sử vé |
| BookingDetailActivity | Chi tiết vé + hủy vé |
| ProfileActivity | Hồ sơ cá nhân + đăng xuất |

---

## Cài Đặt Và Chạy

### Yêu Cầu
- Android Studio Flamingo trở lên
- JDK 8+
- Android SDK API 33
- Thiết bị hoặc máy ảo Android API 24+

### Các Bước

1. Tải dự án về máy
2. Mở bằng Android Studio → File → Open → chọn thư mục dự án
3. Đợi Gradle sync hoàn tất
4. Chạy trên thiết bị hoặc máy ảo (Run → Run 'app')

> Ứng dụng tự tạo cơ sở dữ liệu và điền dữ liệu mẫu khi cài đặt lần đầu (15 tuyến, 20 xe, chuyến đi 7 ngày tới).

### Tài Khoản Thử Nghiệm
- Tạo tài khoản mới qua màn hình đăng ký
- Mã OTP giả lập: `123456`

---

## Thư Viện

| Thư viện | Phiên bản | Mục đích |
|----------|-----------|----------|
| Material Design | 1.9.0 | Giao diện Material Design 3 |
| RecyclerView | 1.3.1 | Hiển thị danh sách |
| CardView | 1.0.0 | Thẻ giao diện với bóng đổ |
| Gson | 2.10.1 | Xử lý JSON (danh sách ghế) |
| ZXing | 3.5.0 | Tạo mã QR |
| Lottie | 6.0.0 | Hiệu ứng hoạt hình |


<div align="center">

Dự án môn học Phát triển ứng dụng di động - TDMU

</div>
