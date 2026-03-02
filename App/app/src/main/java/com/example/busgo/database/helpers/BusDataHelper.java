package com.example.busgo.database.helpers;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * BusDataHelper - Tạo dữ liệu xe khách mẫu
 *
 * 20 xe: 10 ghế ngồi + 10 giường nằm
 * Mỗi xe có tên nhà xe, model, rating, amenities
 * Amenities format: "wifi,wc,charging,air_con,tv,blanket"
 * Tiện ích được đa dạng hóa cho mỗi xe để bộ lọc filter hoạt động có ý nghĩa
 */
public class BusDataHelper {

    // {companyName, busModel}
    private static final String[][] COMPANIES = {
            {"Phương Trang", "Universe Noble"},
            {"Thành Bưởi", "Hyundai Universe"},
            {"Hoàng Long", "King Long"},
            {"Mai Linh Express", "Thaco Mobihome"},
            {"Kumho Samco", "Samco Primas"}
    };

    // Tiện ích đa dạng cho xe ghế ngồi (mỗi xe khác nhau để filter có ý nghĩa)
    private static final String[] SEAT_AMENITIES = {
            "wifi,charging,air_con",           // 1. Phương Trang - tiêu chuẩn
            "wifi,wc,charging,air_con",        // 2. Thành Bưởi - có WC
            "air_con",                          // 3. Hoàng Long - cơ bản
            "wifi,air_con",                     // 4. Mai Linh - wifi, không sạc
            "wifi,wc,charging,air_con,tv",     // 5. Kumho - cao cấp
            "charging,air_con",                 // 6. Phương Trang - cơ bản + sạc
            "wifi,charging,air_con",           // 7. Thành Bưởi - tiêu chuẩn
            "wifi,wc,air_con",                 // 8. Hoàng Long - có WC, không sạc
            "air_con,charging",                 // 9. Mai Linh - cơ bản + sạc
            "wifi,wc,charging,air_con,tv"      // 10. Kumho - cao cấp
    };

    // Tiện ích đa dạng cho xe giường nằm
    private static final String[] SLEEPER_AMENITIES = {
            "wifi,wc,charging,air_con,tv,blanket",  // 1. Phương Trang - đầy đủ
            "wifi,wc,charging,air_con,blanket",      // 2. Thành Bưởi - không TV
            "wc,charging,air_con,blanket",            // 3. Hoàng Long - không wifi
            "wifi,wc,air_con,tv,blanket",             // 4. Mai Linh - không sạc
            "wifi,wc,charging,air_con,tv,blanket",   // 5. Kumho - đầy đủ
            "wifi,wc,air_con,blanket",                // 6. Phương Trang - cơ bản
            "wifi,wc,charging,air_con,tv,blanket",   // 7. Thành Bưởi - đầy đủ
            "wc,air_con,blanket",                      // 8. Hoàng Long - tối thiểu
            "wifi,wc,charging,air_con,blanket",       // 9. Mai Linh - tiêu chuẩn
            "wifi,wc,charging,air_con,tv"             // 10. Kumho - không chăn
    };

    public static void insertSampleBuses(SQLiteDatabase db) {

        // 10 xe ghế ngồi: tiện ích đa dạng theo từng xe
        for (int i = 0; i < 10; i++) {
            String busNumber = String.format("51A-%05d", 10001 + i);
            String[] company = COMPANIES[i % COMPANIES.length];
            double rating = 4.0 + (i % 10) * 0.1;

            insertBus(db, busNumber, "Ghế ngồi", 45, "2-2",
                    company[0], company[1], rating,
                    SEAT_AMENITIES[i]);
        }

        // 10 xe giường nằm: tiện ích đa dạng theo từng xe
        for (int i = 0; i < 10; i++) {
            String busNumber = String.format("51B-%05d", 20001 + i);
            String[] company = COMPANIES[i % COMPANIES.length];
            double rating = 4.3 + (i % 7) * 0.1;

            insertBus(db, busNumber, "Giường nằm", 40, "2-1",
                    company[0], company[1], rating,
                    SLEEPER_AMENITIES[i]);
        }
    }

    private static void insertBus(SQLiteDatabase db, String busNumber,
                                  String busType, int totalSeats, String seatLayout,
                                  String companyName, String busModel,
                                  double rating, String amenities) {
        ContentValues values = new ContentValues();
        values.put("bus_number", busNumber);
        values.put("bus_type", busType);
        values.put("total_seats", totalSeats);
        values.put("seat_layout", seatLayout);
        values.put("company_name", companyName);
        values.put("bus_model", busModel);
        values.put("rating", Math.round(rating * 10.0) / 10.0);
        values.put("amenities", amenities);
        values.put("is_active", 1);

        db.insert("buses", null, values);
    }
}

