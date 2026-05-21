package com.example.busgo.until;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateUtils {
    private static final SimpleDateFormat DATABASE_FORMAT =
            new SimpleDateFormat(Constants.DATE_FORMAT_DATABASE, Locale.getDefault());

    private static final SimpleDateFormat SQL_FORMAT =
            new SimpleDateFormat(Constants.DATE_FORMAT_SQL, Locale.getDefault());

    private static final SimpleDateFormat DATE_DISPLAY_FORMAT =
            new SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.getDefault());

    private static final SimpleDateFormat TIME_DISPLAY_FORMAT =
            new SimpleDateFormat(Constants.TIME_FORMAT_DISPLAY, Locale.getDefault());

//"2026-01-12 06:00:00" -> "12/01/2026"
    public static String formatDate(String databaseDateTime) {
        try {
            Date date = DATABASE_FORMAT.parse(databaseDateTime);
            return DATE_DISPLAY_FORMAT.format(date);
        } catch (ParseException e) {
            return databaseDateTime;
        }
    }
//"2026-01-12 06:00:00" -> "06:00"
    public static String formatTime(String databaseDateTime) {
        try {
            Date date = DATABASE_FORMAT.parse(databaseDateTime);
            return TIME_DISPLAY_FORMAT.format(date);
        } catch (ParseException e) {
            return databaseDateTime;
        }
    }

    //"2026-01-12" -> Date | null
    public static Date parseSqlDate(String sqlDate) {
        try {
            return sqlDate == null ? null : SQL_FORMAT.parse(sqlDate);
        } catch (ParseException e) {
            return null;
        }
    }

    //Date -> "2026-01-12"
    public static String formatSqlDate(Date date) {
        return SQL_FORMAT.format(date);
    }

    //"2026-01-12" -> "12/01/2026"
    public static String sqlDateToDisplay(String sqlDate) {
        Date d = parseSqlDate(sqlDate);
        return d == null ? sqlDate : DATE_DISPLAY_FORMAT.format(d);
    }

    //@return yyyy-MM-dd
    public static String getCurrentDate() {
        return SQL_FORMAT.format(new Date());
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

