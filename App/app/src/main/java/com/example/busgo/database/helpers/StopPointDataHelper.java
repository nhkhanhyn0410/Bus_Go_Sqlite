package com.example.busgo.database.helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * StopPointDataHelper - Tạo dữ liệu điểm dừng mẫu (gộp đón, trả, dừng chân)
 *
 * Bảng stop_points chứa 3 loại:
 *  - pickup: Điểm đón khách (time_offset so với departure_time)
 *  - dropoff: Điểm trả khách (time_offset so với arrival_time)
 *  - rest_stop: Điểm dừng chân giữa đường (time_offset so với departure_time)
 */
public class StopPointDataHelper {

    public static void insertSampleStopPoints(SQLiteDatabase db) {
        // Lấy danh sách routes
        Cursor cursor = db.query("routes",
                new String[]{"id", "departure", "destination", "distance", "duration"},
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            int routeId = cursor.getInt(0);
            String departure = cursor.getString(1);
            String destination = cursor.getString(2);
            int distance = cursor.getInt(3);
            int duration = cursor.getInt(4);

            // 1. Tạo điểm đón theo thành phố khởi hành
            insertPickupPointsByCity(db, routeId, departure);

            // 2. Tạo điểm trả theo thành phố đích
            insertDropoffPointsByCity(db, routeId, destination);

            // 3. Tạo điểm dừng chân (chỉ tuyến dài > 150km)
            if (distance > 150) {
                insertRestStopsByRoute(db, routeId, departure, destination, distance, duration);
            }
        }

        cursor.close();
    }

    // ======================= ĐIỂM ĐÓN (PICKUP) =======================

    private static void insertPickupPointsByCity(SQLiteDatabase db, int routeId, String cityName) {
        switch (cityName) {
            case "TP.HCM":
                insertPickup(db, routeId, "Bến xe Miền Đông", "292 Đinh Bộ Lĩnh, Bình Thạnh", 0);
                insertPickup(db, routeId, "Bến xe An Sương", "Quốc Tộ 22, Hóc Môn", 30);
                insertPickup(db, routeId, "Ngã tư An Lạc", "Ngã tư An Lạc, Bình Tân", 45);
                insertPickup(db, routeId, "Bến xe Miền Tây", "395 Kinh Dương Vương, Bình Tân", 50);
                break;

            case "Hà Nội":
                insertPickup(db, routeId, "Bến xe Giáp Bát", "Đường Giải Phóng, Hoàng Mai", 0);
                insertPickup(db, routeId, "Bến xe Mỹ Đình", "Phạm Hùng, Nam Từ Liêm", 30);
                insertPickup(db, routeId, "Bến xe Nước Ngầm", "Giải Phóng, Hoàng Mai", 40);
                break;

            case "Đà Nẵng":
                insertPickup(db, routeId, "Bến xe Đà Nẵng", "Điện Biên Phủ, Thanh Khê", 0);
                insertPickup(db, routeId, "Ngã ba Huế", "QL1A, Hòa Phước", 20);
                insertPickup(db, routeId, "Cầu Rồng", "Bạch Đằng, Hải Châu", 25);
                break;

            case "Nha Trang":
                insertPickup(db, routeId, "Bến xe Nha Trang", "23 Tháng 10, Phước Long", 0);
                insertPickup(db, routeId, "Trung tâm TP", "Trần Phú, Lộc Thọ", 15);
                break;

            case "Cần Thơ":
                insertPickup(db, routeId, "Bến xe Cần Thơ", "Nguyễn Văn Linh, Ninh Kiều", 0);
                insertPickup(db, routeId, "Cầu Cần Thơ", "QL1A, Cái Răng", 20);
                break;

            case "Biên Hòa":
                insertPickup(db, routeId, "Bến xe Biên Hòa", "Phạm Văn Thuận, Tân Tiến", 0);
                insertPickup(db, routeId, "Ngã tư Vũng Tàu", "QL51, Long Bình Tân", 15);
                break;

            default:
                insertPickup(db, routeId, "Bến xe " + cityName, "Trung tâm " + cityName, 0);
                insertPickup(db, routeId, "Điểm đón 2", "Địa chỉ 2", 20);
                break;
        }
    }

    // ======================= ĐIỂM TRẢ (DROPOFF) =======================

    private static void insertDropoffPointsByCity(SQLiteDatabase db, int routeId, String cityName) {
        switch (cityName) {
            case "Đà Lạt":
                insertDropoff(db, routeId, "Trung tâm Đà Lạt", "Hồ Xuân Hương, Phường 10", 0);
                insertDropoff(db, routeId, "Chợ Đà Lạt", "Nguyễn Thị Minh Khai, Phường 1", 15);
                insertDropoff(db, routeId, "Ga Đà Lạt", "Quang Trung, Phường 10", 20);
                insertDropoff(db, routeId, "Hồ Xuân Hương", "Trần Quốc Toản, Phường 3", 30);
                break;

            case "Nha Trang":
                insertDropoff(db, routeId, "Bến xe Nha Trang", "23 Tháng 10, Phước Long", 0);
                insertDropoff(db, routeId, "Trung tâm TP", "Trần Phú, Lộc Thọ", 15);
                insertDropoff(db, routeId, "Bãi biển Nha Trang", "Trần Phú, Vĩnh Hải", 25);
                break;

            case "Vũng Tàu":
                insertDropoff(db, routeId, "Bến xe Vũng Tàu", "Nam Kỳ Khởi Nghĩa, Phường 1", 0);
                insertDropoff(db, routeId, "Bãi Trước", "Quang Trung, Phường Thắng Tam", 15);
                insertDropoff(db, routeId, "Bãi Sau", "Thùy Vân, Phường Thắng Tam", 20);
                break;

            case "Phan Thiết":
                insertDropoff(db, routeId, "Bến xe Phan Thiết", "Lê Hồng Phong, Phú Thủy", 0);
                insertDropoff(db, routeId, "Mũi Né", "Nguyễn Đình Chiểu, Mũi Né", 20);
                break;

            case "Cần Thơ":
                insertDropoff(db, routeId, "Bến xe Cần Thơ", "Nguyễn Văn Linh, Ninh Kiều", 0);
                insertDropoff(db, routeId, "Chợ Cần Thơ", "Hai Bà Trưng, Tân An", 15);
                break;

            case "Hải Phòng":
                insertDropoff(db, routeId, "Bến xe Niệm Nghĩa", "Lê Thánh Tông, Máy Chai", 0);
                insertDropoff(db, routeId, "Trung tâm TP", "Điện Biên Phủ, Minh Khai", 15);
                break;

            case "Hạ Long":
                insertDropoff(db, routeId, "Bến xe Hạ Long", "QL18, Bãi Cháy", 0);
                insertDropoff(db, routeId, "Bãi Cháy", "Hạ Long, Bãi Cháy", 10);
                break;

            case "Sapa":
                insertDropoff(db, routeId, "Bến xe Sapa", "Điện Biên Phủ, Sa Pa", 0);
                insertDropoff(db, routeId, "Chợ Sapa", "Fansipan, Sa Pa", 10);
                break;

            case "Huế":
                insertDropoff(db, routeId, "Bến xe Hướng Nam", "An Dương Vương, Phú Hội", 0);
                insertDropoff(db, routeId, "Đại Nội Huế", "Lê Duẩn, Phú Hậu", 15);
                break;

            case "Hội An":
                insertDropoff(db, routeId, "Bến xe Hội An", "Lý Thường Kiệt, Cẩm Châu", 0);
                insertDropoff(db, routeId, "Phố Cổ Hội An", "Trần Phú, Minh An", 10);
                break;

            case "Ninh Bình":
                insertDropoff(db, routeId, "Bến xe Ninh Bình", "Lê Đại Hành, Đông Thành", 0);
                insertDropoff(db, routeId, "Tràng An", "QL1A, Ninh Hải", 20);
                break;

            case "Thanh Hóa":
                insertDropoff(db, routeId, "Bến xe Thanh Hóa", "Quang Trung, Điện Biên", 0);
                insertDropoff(db, routeId, "Trung tâm TP", "Phan Chu Trinh, Ba Đình", 15);
                break;

            case "Rạch Giá":
                insertDropoff(db, routeId, "Bến xe Rạch Giá", "Nguyễn Trung Trực, Vĩnh Lạc", 0);
                insertDropoff(db, routeId, "Cảng Rạch Giá", "Trần Phú, Vĩnh Thanh Vân", 15);
                break;

            default:
                insertDropoff(db, routeId, "Bến xe " + cityName, "Trung tâm " + cityName, 0);
                insertDropoff(db, routeId, "Điểm trả 2", "Địa chỉ 2", 15);
                break;
        }
    }

    // ======================= ĐIỂM DỪNG CHÂN (REST_STOP) =======================

    private static void insertRestStopsByRoute(SQLiteDatabase db, int routeId,
                                               String departure, String destination,
                                               int distance, int duration) {

        // ==================== Tuyến từ TP.HCM ====================

        // TP.HCM → Đà Lạt (300km): 1 điểm dừng
        if (departure.equals("TP.HCM") && destination.equals("Đà Lạt")) {
            insertRestStop(db, routeId, "Trạm dừng Định Quán",
                    "QL20, Định Quán, Đồng Nai", 120, 20);
        }

        // TP.HCM → Nha Trang (450km): 2 điểm dừng
        if (departure.equals("TP.HCM") && destination.equals("Nha Trang")) {
            insertRestStop(db, routeId, "Trạm dừng Phan Thiết",
                    "QL1A, Phan Thiết, Bình Thuận", 150, 15);
            insertRestStop(db, routeId, "Trạm dừng Phan Rang",
                    "QL1A, Phan Rang - Tháp Chàm", 330, 20);
        }

        // TP.HCM → Phan Thiết (200km): 1 điểm dừng
        if (departure.equals("TP.HCM") && destination.equals("Phan Thiết")) {
            insertRestStop(db, routeId, "Trạm dừng Long Khánh",
                    "QL1A, Long Khánh, Đồng Nai", 90, 15);
        }

        // TP.HCM → Cần Thơ (170km): 1 điểm dừng
        if (departure.equals("TP.HCM") && destination.equals("Cần Thơ")) {
            insertRestStop(db, routeId, "Trạm dừng Trung Lương",
                    "QL1A, Tân An, Long An", 60, 15);
        }

        // ==================== Tuyến từ Hà Nội ====================

        // Hà Nội → Hạ Long (165km): 1 điểm dừng
        if (departure.equals("Hà Nội") && destination.equals("Hạ Long")) {
            insertRestStop(db, routeId, "Trạm dừng Hải Dương",
                    "QL5, Hải Dương", 60, 15);
        }

        // Hà Nội → Sapa (350km): 2 điểm dừng
        if (departure.equals("Hà Nội") && destination.equals("Sapa")) {
            insertRestStop(db, routeId, "Trạm dừng Yên Bái",
                    "QL32, Yên Bái", 180, 20);
            insertRestStop(db, routeId, "Trạm dừng Lào Cai",
                    "QL4D, Lào Cai", 330, 15);
        }

        // Hà Nội → Thanh Hóa (160km): 1 điểm dừng
        if (departure.equals("Hà Nội") && destination.equals("Thanh Hóa")) {
            insertRestStop(db, routeId, "Trạm dừng Ninh Bình",
                    "QL1A, Ninh Bình", 90, 15);
        }
    }

    // ======================= HELPER METHODS =======================

    private static void insertPickup(SQLiteDatabase db, int routeId,
                                     String name, String address, int timeOffset) {
        insertPoint(db, routeId, "pickup", name, address, timeOffset, 0);
    }

    private static void insertDropoff(SQLiteDatabase db, int routeId,
                                      String name, String address, int timeOffset) {
        insertPoint(db, routeId, "dropoff", name, address, timeOffset, 0);
    }

    private static void insertRestStop(SQLiteDatabase db, int routeId,
                                       String name, String address,
                                       int timeOffset, int stopDuration) {
        insertPoint(db, routeId, "rest_stop", name, address, timeOffset, stopDuration);
    }

    /**
     * Insert một điểm vào bảng stop_points (method chung)
     */
    private static void insertPoint(SQLiteDatabase db, int routeId, String pointType,
                                    String stopName, String address,
                                    int timeOffset, int stopDuration) {
        ContentValues values = new ContentValues();
        values.put("route_id", routeId);
        values.put("point_type", pointType);
        values.put("stop_name", stopName);
        values.put("address", address);
        values.put("time_offset", timeOffset);
        values.put("stop_duration", stopDuration);
        values.put("is_active", 1);

        db.insert("stop_points", null, values);
    }
}

