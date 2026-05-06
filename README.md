# BusGo - Ứng dụng đặt vé xe khách Android

> Dự án môn Phát triển ứng dụng di động.

## Mục lục

- [Tổng quan](#tổng-quan)
- [Giải pháp của ứng dụng](#giải-pháp-của-ứng-dụng)
- [Tính năng chính](#tính-năng-chính)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Kiến trúc project](#kiến-trúc-project)
- [Cơ sở dữ liệu SQLite](#cơ-sở-dữ-liệu-sqlite)
- [Luồng đặt vé](#luồng-đặt-vé)
- [Danh sách màn hình](#danh-sách-màn-hình)
- [Cấu trúc thư mục](#cấu-trúc-thư-mục)
- [Hướng dẫn cài đặt và chạy](#hướng-dẫn-cài-đặt-và-chạy)
- [OTP và thanh toán thử nghiệm](#otp-và-thanh-toán-thử-nghiệm)
- [Bảo mật](#bảo-mật)
- [Hạn chế của ứng dụng](#hạn-chế-của-ứng-dụng)
- [Hướng phát triển](#hướng-phát-triển)

## Tổng quan

`BusGo` là ứng dụng Android hỗ trợ đặt vé xe khách dành cho mục đích học tập trong môn Phát triển ứng dụng di động. Ứng dụng cho phép người dùng tìm chuyến xe, xem chi tiết chuyến đi, chọn điểm đón và điểm trả, chọn ghế, thanh toán, theo dõi vé đã đặt và quản lý thông tin cá nhân.

Toàn bộ dữ liệu được lưu cục bộ bằng `SQLite`, phù hợp với mô hình đồ án Android độc lập, dễ chạy và dễ kiểm thử trên thiết bị hoặc máy ảo.

## Giải pháp của ứng dụng

| Trước đây | Với BusGo |
| --- | --- |
| Đặt vé thủ công qua quầy hoặc điện thoại, khó theo dõi ghế trống | Người dùng có thể tìm chuyến và xem số ghế còn trống ngay trên ứng dụng |
| Khó biết điểm đón, điểm trả và thông tin chuyến đi chi tiết | Ứng dụng hiển thị chi tiết chuyến, timeline điểm đón, điểm nghỉ chân và điểm trả |
| Vé dễ thất lạc, khó tra cứu lại sau khi đặt | Vé được lưu trong lịch sử đặt vé và có màn hình chi tiết vé riêng |
| Quy trình thanh toán và xác nhận còn rời rạc | Ứng dụng gom các bước chọn ghế, nhập thông tin hành khách, chọn thanh toán và xác nhận vé trong một luồng thống nhất |

## Tính năng chính

### Xác thực tài khoản

- Đăng nhập bằng email hoặc số điện thoại trong `AuthActivity`
- Đăng ký tài khoản mới với kiểm tra dữ liệu đầu vào
- Xác minh OTP qua email hoặc số điện thoại trong `VerificationActivity`
- Hoàn tất hồ sơ ban đầu trong `ConfirmInfoActivity`
- Quản lý phiên đăng nhập bằng `SessionManager` và `SharedPreferences`

### Tìm kiếm chuyến xe

- Tìm chuyến theo điểm đi, điểm đến và ngày khởi hành
- Hỗ trợ đổi chiều điểm đi và điểm đến
- Gợi ý tuyến phổ biến tại `MainActivity`
- Hiển thị danh sách chuyến theo 7 ngày liên tiếp trong `SearchTripActivity`
- Lọc chuyến theo tiện ích như `Wifi`, `WC`, `Giường`, `Sạc`

### Đặt vé

- Xem thông tin chi tiết chuyến xe trong `TripDetailActivity`
- Chọn hoặc đổi điểm đón và điểm trả trong `PickupDropoffActivity`
- Xác nhận điểm đón/trả qua `dialog_confirm_stops`
- Chọn ghế theo sơ đồ xe `2-2` hoặc `2-1` trong `SeatSelectionActivity`
- Giới hạn tối đa 6 ghế cho mỗi lần đặt
- Nhập thông tin hành khách trong `PassengerInfoActivity`

### Thanh toán

- Chọn phương thức thanh toán trong `PaymentMethodActivity`
- Hỗ trợ thanh toán `Tiền mặt` và `MoMo`
- Xử lý thanh toán và tạo booking trong `PaymentProcessingActivity`
- Có màn hình kết quả thanh toán thành công và thất bại

### Quản lý vé

- Xem lịch sử vé đã đặt trong `BookingHistoryActivity`
- Xem chi tiết vé trong `BookingDetailActivity`
- Hiển thị mã vé, ghế đã chọn, trạng thái thanh toán và các điểm nghỉ chân
- Hủy vé và trả lại ghế qua transaction trong `BookingDAO`

### Hồ sơ và thông tin ứng dụng

- Xem hồ sơ cá nhân trong `ProfileActivity`
- Cập nhật hồ sơ trong `EditProfileActivity`
- Xem các màn hình thông tin như `AboutUsActivity`, `TermsActivity`, `PrivacyPolicyActivity`, `LicenseActivity`, `UserAgreementActivity`

## Công nghệ sử dụng

| Công nghệ | Vai trò |
| --- | --- |
| `Java` | Ngôn ngữ chính cho Activity, DAO, model và logic nghiệp vụ |
| `XML Layout` | Xây dựng giao diện người dùng |
| `SQLite` | Lưu trữ dữ liệu cục bộ cho tài khoản, tuyến xe, chuyến xe, ghế và vé |
| `SharedPreferences` | Lưu trạng thái đăng nhập qua `SessionManager` |
| `BCrypt` | Băm và kiểm tra mật khẩu người dùng |
| `Firebase Phone Auth` | Xác minh OTP qua số điện thoại |
| `JavaMail` | Gửi OTP qua email bằng `EmailOtpSender` |
| `MoMo sandbox/test API` | Mô phỏng thanh toán điện tử trong môi trường demo |
| `RecyclerView / GridView` | Hiển thị danh sách chuyến, lịch sử vé và sơ đồ ghế |
| `AndroidX / Material Components` | Cung cấp nền tảng Activity, layout và thành phần giao diện theo Gradle |

## Kiến trúc project

Package chính của ứng dụng là `com.example.busgo`.

| Tầng | Package | Vai trò |
| --- | --- | --- |
| Activity / UI | `activities/auth`, `activities/common`, `activities/user` | Điều hướng màn hình, xử lý tương tác người dùng, hiển thị dữ liệu |
| Adapter | `adapters` | Kết nối dữ liệu với `RecyclerView` và `GridView` |
| DAO | `database/DAO` | Truy vấn, thêm, cập nhật và hủy dữ liệu trong SQLite |
| Model | `database/model` | Biểu diễn dữ liệu như `User`, `Trip`, `Booking`, `Seat`, `StopPoint` |
| SQLite Helper / Seeder | `database/DatabaseHelper`, `database/helpers` | Tạo bảng, quản lý version database và seed dữ liệu mẫu |
| Utility | `until` | Chứa helper dùng chung như `SessionManager`, `ValidationUtils`, `DateUtils`, `MoMoPaymentHelper`, `EmailOtpSender` |

## Cơ sở dữ liệu SQLite

`DatabaseHelper` tạo 7 bảng dữ liệu chính. Dữ liệu mẫu về tuyến xe, xe, chuyến xe và điểm dừng được seed ngay ở lần chạy đầu.

| Bảng | Chức năng |
| --- | --- |
| `users` | Lưu tài khoản người dùng, mật khẩu đã băm, họ tên, số điện thoại, email, ngày sinh, giới tính |
| `routes` | Lưu tuyến đường gồm điểm đi, điểm đến, quãng đường và thời gian di chuyển |
| `buses` | Lưu thông tin xe như biển số, loại xe, số ghế, layout ghế, hãng xe, model, tiện ích |
| `trips` | Lưu từng chuyến xe theo tuyến và xe, gồm giờ đi, giờ đến, giá vé, số ghế còn trống |
| `stop_points` | Lưu điểm đón, điểm trả và điểm nghỉ chân theo từng tuyến |
| `seats` | Lưu trạng thái ghế theo từng chuyến xe |
| `bookings` | Lưu đơn đặt vé, hành khách, danh sách ghế, điểm đón/trả, tổng tiền và trạng thái thanh toán |

## Luồng đặt vé

Luồng nghiệp vụ bám theo source code như sau:

`SplashActivity` -> `AuthActivity` -> `VerificationActivity` -> `ConfirmInfoActivity` -> `MainActivity` -> `SearchTripActivity` -> `TripDetailActivity` -> `PickupDropoffActivity` -> `dialog_confirm_stops` -> `SeatSelectionActivity` -> `PassengerInfoActivity` -> `PaymentMethodActivity` -> `PaymentProcessingActivity` -> `PaymentSuccessActivity` hoặc `PaymentFailedActivity` -> `BookingDetailActivity` hoặc `BookingHistoryActivity`

Ghi chú:

- Với đăng ký mới: sau `VerificationActivity` người dùng đi tiếp tới `ConfirmInfoActivity`
- Với đăng nhập: sau `VerificationActivity` người dùng được chuyển thẳng vào `MainActivity`

## Danh sách màn hình

| Nhóm | Activity |
| --- | --- |
| Xác thực tài khoản | `SplashActivity`, `AuthActivity`, `VerificationActivity`, `ConfirmInfoActivity` |
| Trang chủ và tìm chuyến | `MainActivity`, `SearchTripActivity`, `TripDetailActivity`, `PickupDropoffActivity` |
| Đặt vé và thanh toán | `SeatSelectionActivity`, `PassengerInfoActivity`, `PaymentMethodActivity`, `PaymentProcessingActivity`, `PaymentSuccessActivity`, `PaymentFailedActivity` |
| Quản lý vé | `BookingHistoryActivity`, `BookingDetailActivity` |
| Hồ sơ và thông tin ứng dụng | `ProfileActivity`, `EditProfileActivity`, `AboutUsActivity`, `TermsActivity`, `PrivacyPolicyActivity`, `LicenseActivity`, `UserAgreementActivity` |

## Cấu trúc thư mục

```text
Bus_Go_Sqlite/
├─ README.md
└─ App/
   ├─ build.gradle
   ├─ settings.gradle
   ├─ gradle/
   │  ├─ libs.versions.toml
   │  └─ wrapper/gradle-wrapper.properties
   └─ app/
      ├─ build.gradle
      └─ src/main/
         ├─ AndroidManifest.xml
         ├─ java/com/example/busgo/
         │  ├─ activities/
         │  │  ├─ auth/
         │  │  │  ├─ AuthActivity.java
         │  │  │  ├─ ConfirmInfoActivity.java
         │  │  │  └─ VerificationActivity.java
         │  │  ├─ common/
         │  │  │  └─ SplashActivity.java
         │  │  └─ user/
         │  │     ├─ AboutUsActivity.java
         │  │     ├─ BookingDetailActivity.java
         │  │     ├─ BookingHistoryActivity.java
         │  │     ├─ EditProfileActivity.java
         │  │     ├─ LicenseActivity.java
         │  │     ├─ MainActivity.java
         │  │     ├─ PassengerInfoActivity.java
         │  │     ├─ PaymentFailedActivity.java
         │  │     ├─ PaymentMethodActivity.java
         │  │     ├─ PaymentProcessingActivity.java
         │  │     ├─ PaymentSuccessActivity.java
         │  │     ├─ PickupDropoffActivity.java
         │  │     ├─ PrivacyPolicyActivity.java
         │  │     ├─ ProfileActivity.java
         │  │     ├─ SearchTripActivity.java
         │  │     ├─ SeatSelectionActivity.java
         │  │     ├─ TermsActivity.java
         │  │     ├─ TripDetailActivity.java
         │  │     └─ UserAgreementActivity.java
         │  ├─ adapters/
         │  │  ├─ BookingAdapter.java
         │  │  ├─ RouteAdapter.java
         │  │  ├─ SeatGridAdapter.java
         │  │  └─ TripAdapter.java
         │  ├─ database/
         │  │  ├─ DatabaseHelper.java
         │  │  ├─ DAO/
         │  │  │  ├─ BookingDAO.java
         │  │  │  ├─ SeatDAO.java
         │  │  │  ├─ StopPointDAO.java
         │  │  │  ├─ TripDAO.java
         │  │  │  └─ UserDAO.java
         │  │  ├─ helpers/
         │  │  │  ├─ BusDataHelper.java
         │  │  │  ├─ RouteDataHelper.java
         │  │  │  ├─ StopPointDataHelper.java
         │  │  │  └─ TripDataHelper.java
         │  │  └─ model/
         │  │     ├─ Booking.java
         │  │     ├─ Bus.java
         │  │     ├─ Route.java
         │  │     ├─ Seat.java
         │  │     ├─ StopPoint.java
         │  │     ├─ Trip.java
         │  │     └─ User.java
         │  └─ until/
         │     ├─ BottomNavHelper.java
         │     ├─ Constants.java
         │     ├─ DateUtils.java
         │     ├─ EmailOtpSender.java
         │     ├─ ExpandableGridView.java
         │     ├─ MoMoPaymentHelper.java
         │     ├─ PriceCalculator.java
         │     ├─ SessionManager.java
         │     └─ ValidationUtils.java
         └─ res/
            ├─ layout/
            ├─ drawable/
            ├─ anim/
            ├─ values/
            └─ font/
```

## Hướng dẫn cài đặt và chạy

### Yêu cầu môi trường

- Android Studio hỗ trợ `AGP 8.13.2`
- `JDK 11`
- Thiết bị hoặc emulator Android từ `API 26`

### Các bước thực hiện

1. Mở thư mục `App/` bằng Android Studio vì đây là Gradle root của project.
2. Chờ `Gradle Sync` hoàn tất.
3. Nếu muốn dùng OTP email, thêm cấu hình vào `App/local.properties`:

```properties
email.sender=your_email@gmail.com
email.app.password=your_app_password
```

4. Nếu muốn dùng OTP số điện thoại, thêm file `google-services.json` vào `App/app/` và cấu hình Firebase.
5. Chạy ứng dụng trên thiết bị thật hoặc emulator phù hợp.
6. `DatabaseHelper` sẽ tự tạo file SQLite và seed dữ liệu mẫu ngay lần chạy đầu.
7. Project không có tài khoản mẫu cố định; người dùng có thể tạo tài khoản mới trực tiếp trong ứng dụng.

## OTP và thanh toán thử nghiệm

- OTP test chỉ nên dùng trong môi trường demo khi email hoặc Firebase chưa được cấu hình đầy đủ
- Mã test fallback trong luồng xác minh là `123456`
- Thanh toán `MoMo` đang dùng môi trường `sandbox/test`
- Thanh toán `Tiền mặt` tạo vé với trạng thái `payment_status = unpaid`
- Deep link trả kết quả thanh toán là `busgo://momo_return`
