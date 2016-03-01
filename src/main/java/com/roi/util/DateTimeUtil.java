package com.roi.util;

import com.roi.services.CsvService;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtil {
    // Set timeZone to UTC
    private static final long millisInSecond = 1000;

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    /**
     * @param timestamp Date stored in unix timestamp
     * @return Returns formatted String representation of date
     */
    public static String getDate(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    /**
     * @param timestamp Date stored in unix timestamp
     * @param session Duration of session
     * @return Amount of time passed since the beginning of session till the
     * end of day (00:00)
     */
    public static long getDelta(long timestamp, long session) {
        // Trim date to midnight
        Date date = new Date(timestamp+session);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date trimmedDate = calendar.getTime();

        return (trimmedDate.getTime() - new Date(timestamp).getTime()) / millisInSecond ;
    }
}
