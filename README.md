# Vé Xe Nhanh - Android App

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)
![Material Design](https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=material-design&logoColor=white)

**Ứng dụng Android native hoàn chỉnh cho đặt vé xe khách trực tuyến**

[Tính năng](#-tính-năng-chính) • [Cài đặt](#-cài-đặt) • [Công nghệ](#-công-nghệ) • [Demo](#-screenshots) • [Tài liệu](#-tài-liệu)

</div>

---

## Giới thiệu

**Vé Xe Nhanh** là ứng dụng Android native được xây dựng hoàn toàn bằng **Java** với **SQLite** local database. Ứng dụng cung cấp trải nghiệm đặt vé xe khách hoàn chỉnh, từ tìm kiếm, chọn ghế, thanh toán đến quản lý vé điện tử.

### Mục tiêu

- **Học tập**: Dự án môn Phát triển ứng dụng di động - TDMU
- **Thực hành**: Android, SQLite, Material Design, Payment Integration
- **Standalone**: App hoàn toàn độc lập, không cần backend server

---

## Tính năng chính

### Authentication
- Đăng ký tài khoản với validation đầy đủ
- Đăng nhập với session management (SharedPreferences)
- Password hashing (SHA-256)

### Tìm kiếm & Đặt vé
- Tìm kiếm chuyến theo điểm đi, điểm đến, ngày
- Xem danh sách chuyến với thông tin chi tiết
- **Chọn điểm đón** (3-5 điểm/tuyến với giờ đón chính xác)
- **Chọn điểm trả** (3-5 điểm/tuyến với giờ trả chính xác)
- Chọn ghế trên sơ đồ xe (hỗ trợ 1-2 tầng)
- Nhập thông tin hành khách (tên, SĐT, email)
- Xác nhận đặt vé với tổng quan đầy đủ

### Thanh toán
- **MoMo** - Tích hợp SDK (sandbox mode)
- **ZaloPay** - Tích hợp SDK (sandbox mode)
- **VNPay** - WebView integration (sandbox mode)
- **Tiền mặt** - Thanh toán khi lên xe
- Tracking payment status (unpaid/paid/refunded)

### Vé điện tử
- **QR Code** - Generate từ mã vé (ZXing library)
- Hiển thị vé đẹp mắt sau khi đặt thành công
- Lịch sử đặt vé với filter theo trạng thái
- Chi tiết vé đầy đủ (tuyến, ghế, điểm đón/trả, thanh toán)
- Hủy vé (với điều kiện)
- Chia sẻ vé qua Intent

### Profile
- Xem và chỉnh sửa thông tin cá nhân
- Đăng xuất
- About

---

## Công nghệ

### Core Technologies
| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| **Java** | 8+ | Ngôn ngữ chính |
| **SQLite** | Built-in | Local database |
| **Android SDK** | Min API 24 (Android 7.0) | Platform |
| **Target SDK** | API 33+ | Latest features |

### Libraries & Dependencies
```gradle
dependencies {
    // Material Design
    implementation 'com.google.android.material:material:1.9.0'

    // Gson for JSON parsing
    implementation 'com.google.code.gson:gson:2.10.1'

    // QR Code Generation
    implementation 'com.google.zxing:core:3.5.0'

    // Payment SDKs (Optional - Sandbox mode)
    implementation 'com.github.momo-wallet:mobile-sdk:1.0.7'
    implementation 'vn.zalopay:zalopay-sdk:1.0.4'
}
```

### Architecture Pattern
- **DAO Pattern** - Data Access Objects cho database operations
- **Singleton Pattern** - DatabaseHelper
- **ViewHolder Pattern** - RecyclerView Adapters
- **MVC** - Activities (Controller), Models, Layouts (View)

---

## Cấu trúc dự án

```
com.tdmu.vexenhanh/
├── activities/          # 17 màn hình
│   ├── auth/              # Login, Register
│   ├── user/              # Main booking flow (11 màn)
│   └── common/            # Splash, About
│
├── adapters/           # RecyclerView Adapters (7 adapters)
│   ├── TripAdapter
│   ├── SeatGridAdapter
│   ├── PickupPointAdapter
│   ├── DropoffPointAdapter
│   └── PaymentMethodAdapter
│
├── database/           # SQLite Layer
│   ├── DatabaseHelper     # Main DB helper (Singleton)
│   ├── dao/              # 6 DAOs
│   ├── models/           # 9 Models
│   └── helpers/          # Data Helpers (sample data)
│
├── payment/            # Payment Integration
│   ├── PaymentManager
│   ├── momo/
│   ├── zalopay/
│   └── vnpay/
│
├── utils/              # Utilities
│   ├── SessionManager
│   ├── QRCodeGenerator
│   ├── ValidationUtils
│   └── DateUtils
│
└── docs/               # Logic documentation
    ├── AUTH_LOGIC.md
    ├── BOOKING_LOGIC.md
    ├── PAYMENT_LOGIC.md
    └── DATABASE_SCHEMA.md
```

---

---

## Cài đặt

### Yêu cầu
- **Android Studio**: Giraffe | 2022.3.1+
- **JDK**: 8+
- **Android Device/Emulator**: API 24+

### Các bước

#### 1. Clone project
```bash
git clone https://github.com/nhkhanhyn0410/
cd Ve_Xe_Nhanh
```

#### 2. Mở trong Android Studio
- File → Open → Chọn thư mục project
- Chờ Gradle sync

#### 3. Cấu hình (nếu cần)
Mở `app/build.gradle` và verify dependencies:
```gradle
android {
    compileSdk 33

    defaultConfig {
        applicationId "com.tdmu.vexenhanh"
        minSdk 24
        targetSdk 33
        versionCode 1
        versionName "1.0"
    }
}
```

#### 4. Build & Run
- Kết nối Android device hoặc start emulator
- Run → Run 'app' (Shift + F10)

---

## Screenshots

### Booking Flow
```
1. Login → 2. Search → 3. Trip List → 4. Trip Detail
    ↓
5. Pickup Point → 6. Dropoff Point → 7. Seat Selection
    ↓
8. Passenger Info → 9. Confirm → 10. Payment → 11. Success (QR)
```

### Key Features Demo

**Điểm đón với giờ chính xác:**
```
Chuyến: TP.HCM → Đà Lạt (06:00)

Điểm đón:
✓ Bến xe Miền Đông    06:00 (offset +0)
✓ Bến xe An Sương      06:30 (offset +30)
✓ Ngã tư An Lạc        06:45 (offset +45)
```

**QR Code Ticket:**
```
Mã vé: BK20260112001
[QR CODE IMAGE]
━━━━━━━━━━━━━━━━━━━━
Tuyến: TP.HCM → Đà Lạt
Ngày: 12/01/2026 - 06:00
Điểm đón: Bến xe Miền Đông (06:00)
Ghế: A1, A2
Hành khách: Nguyễn Văn A
Tổng: 500,000 VNĐ
Đã thanh toán (MoMo)
```

---

## Màn hình (17 màn)

### Authentication (3)
1. Splash Screen
2. Login
3. Register

### Booking Flow (11)
4. Dashboard
5. Search Trip
6. Trip List
7. Trip Detail
8. **Pickup Point Selection**
9. **Dropoff Point Selection**
10. Seat Selection
11. **Passenger Info**
12. Booking Confirm
13. **Payment Method**
14. **Payment Process**
15. **Booking Success (QR)**

### History & Profile (3)
16. Booking History
17. Booking Detail
18. Profile

---

## Điểm nổi bật

### 🚏 Smart Pickup/Dropoff
Không giống app đặt vé thông thường chỉ có điểm đi/đến cố định:

```java
// Tính giờ đón thực tế
Giờ đón = Giờ khởi hành + time_offset

VD: Chuyến 06:00
- Điểm 1 (offset 0):  06:00
- Điểm 2 (offset 30): 06:30
- Điểm 3 (offset 45): 06:45
```

### Payment Integration
3 ví điện tử + Tiền mặt, tất cả sandbox mode:

```java
// MoMo Integration
MoMoPayment payment = new MoMoPayment(this, callback);
payment.requestPayment(bookingCode, totalPrice);

// Callback handling
callback.onSuccess(transactionId) {
    updatePaymentStatus("paid");
    showSuccessScreen();
}
```

### QR Code Generation
```java
// Generate QR từ booking code
Bitmap qr = QRCodeGenerator.generateQRCode(
    bookingCode,
    300,
    300
);
imageView.setImageBitmap(qr);
```

### Transaction Safety
```java
db.beginTransaction();
try {
    bookingDAO.insert(booking);
    seatDAO.updateStatus(seatIds, true);
    tripDAO.decreaseSeats(tripId);
    db.setTransactionSuccessful();
} finally {
    db.endTransaction();
}
```

---

## Testing

### Test Cases
- Đăng ký với validation
- Đăng nhập success/fail
- Tìm kiếm chuyến
- Chọn ghế đã đặt → Error
- Đặt vé hoàn chỉnh
- Thanh toán MoMo (sandbox)
- Generate QR code
- Hủy vé
- Race condition (2 user cùng ghế)


**Nếu project hữu ích, hãy cho 1 star!**

Made with ❤️ by TDMU Students

</div>
