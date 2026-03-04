package com.example.busgo.until;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateUtils {
    private static final SimpleDateFormat DATABASE_FORMAT =
            new SimpleDateFormat(Constants.DATE_FORMAT_DATABASE, Locale.getDefault());

    private static final SimpleDateFormat DATE_DISPLAY_FORMAT =
            new SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.getDefault());

    private static final SimpleDateFormat TIME_DISPLAY_FORMAT =
            new SimpleDateFormat(Constants.TIME_FORMAT_DISPLAY, Locale.getDefault());

    private static final SimpleDateFormat DATETIME_DISPLAY_FORMAT =
            new SimpleDateFormat(Constants.DATETIME_FORMAT_DISPLAY, Locale.getDefault());

    /**
     * Format ngày giờ database thành chuỗi hiển thị
     * VD: "2026-01-12 06:00:00" -> "12/01/2026 06:00"
     */
    public static String formatDateTime(String databaseDateTime) {
        try {
            Date date = DATABASE_FORMAT.parse(databaseDateTime);
            return DATETIME_DISPLAY_FORMAT.format(date);
        } catch (ParseException e) {
            return databaseDateTime;
        }
    }

    /**
     * Format ngày database thành chuỗi hiển thị
     * VD: "2026-01-12 06:00:00" -> "12/01/2026"
     */
    public static String formatDate(String databaseDateTime) {
        try {
            Date date = DATABASE_FORMAT.parse(databaseDateTime);
            return DATE_DISPLAY_FORMAT.format(date);
        } catch (ParseException e) {
            return databaseDateTime;
        }
    }

    /**
     * Format giờ database thành chuỗi hiển thị
     * VD: "2026-01-12 06:00:00" -> "06:00"
     */
    public static String formatTime(String databaseDateTime) {
        try {
            Date date = DATABASE_FORMAT.parse(databaseDateTime);
            return TIME_DISPLAY_FORMAT.format(date);
        } catch (ParseException e) {
            return databaseDateTime;
        }
    }

    public static String calculateActualTime(String baseTime, int offsetMinutes) {
        try {
            Date date = DATABASE_FORMAT.parse(baseTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, offsetMinutes);

            return DATABASE_FORMAT.format(calendar.getTime());
        } catch (ParseException e) {
            return baseTime;
        }
    }

    //@return yyyy-MM-dd
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    //@return yyyy-MM-dd
    public static String getCurrentDateTime() {
        return DATABASE_FORMAT.format(new Date());
    }


     //Kiểm tra ngày có hợp lệ (>= ngày hiện tại)
    public static boolean isValidDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date inputDate = sdf.parse(date);
            Date today = sdf.parse(getCurrentDate());

            return !inputDate.before(today);
        } catch (ParseException e) {
            return false;
        }
    }

     //@return Chuỗi dạng "6 giờ 30 phút"
    public static String calculateDuration(String startTime, String endTime) {
        try {
            Date start = DATABASE_FORMAT.parse(startTime);
            Date end = DATABASE_FORMAT.parse(endTime);

            long diff = end.getTime() - start.getTime();
            long hours = diff / (1000 * 60 * 60);
            long minutes = (diff % (1000 * 60 * 60)) / (1000 * 60);

            return hours + " giờ " + minutes + " phút";
        } catch (ParseException e) {
            return "";
        }
    }
}

