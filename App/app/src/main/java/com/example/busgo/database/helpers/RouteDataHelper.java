package com.example.busgo.database.helpers;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import com.example.busgo.database.DatabaseHelper;
public class RouteDataHelper {
    public static void insertSampleRoutes(SQLiteDatabase db) {

        String[][] routes = {
                {"TP.HCM", "Đà Lạt", "300", "360"},
                {"TP.HCM", "Nha Trang", "450", "540"},
                {"TP.HCM", "Vũng Tàu", "125", "150"},
                {"TP.HCM", "Phan Thiết", "200", "240"},
                {"TP.HCM", "Cần Thơ", "170", "210"},

                {"Hà Nội", "Hải Phòng", "120", "150"},
                {"Hà Nội", "Hạ Long", "165", "210"},
                {"Hà Nội", "Sapa", "350", "420"},
                {"Hà Nội", "Ninh Bình", "95", "120"},
                {"Hà Nội", "Thanh Hóa", "160", "195"},

                {"Đà Nẵng", "Huế", "100", "135"},
                {"Đà Nẵng", "Hội An", "30", "45"},
                {"Nha Trang", "Đà Lạt", "135", "180"},
                {"Cần Thơ", "Rạch Giá", "120", "150"},
                {"Biên Hòa", "Vũng Tàu", "90", "120"}
        };

        for (String[] route : routes) {
            ContentValues values = new ContentValues();
            values.put("departure", route[0]);
            values.put("destination", route[1]);
            values.put("distance", Integer.parseInt(route[2]));
            values.put("duration", Integer.parseInt(route[3]));
            values.put("is_active", 1);

            db.insert("routes", null, values);
        }
    }
}
