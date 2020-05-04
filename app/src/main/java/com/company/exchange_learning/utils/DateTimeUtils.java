package com.company.exchange_learning.utils;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class DateTimeUtils {
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    public static LocalDateTime getDateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d H:m:s");
        return LocalDateTime.parse(date.trim(), formatter);
    }

    public static String getStringFromDate(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("yyyy-M-d H:m:s").format(dateTime);
    }
}
