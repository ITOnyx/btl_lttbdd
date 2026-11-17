package com.example.app.util;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault());

    public interface OnDateSelectedListener {
        void onDateSelected(String dateStr);
    }

    public interface OnDateTimeSelectedListener {
        void onDateTimeSelected(String dateTimeStr);
    }

    public static synchronized String getCurrentDate() {
        return dateFormat.format(new Date());
    }

    public static synchronized String getCurrentDateTime() {
        return dateTimeFormat.format(new Date());
    }

    public static synchronized String formatDate(Date date) {
        if (date == null) return "";
        return dateFormat.format(date);
    }

    public static synchronized String formatDateTime(Date date) {
        if (date == null) return "";
        return dateTimeFormat.format(date);
    }

    public static synchronized String formatUtcDate(Long utcMillis) {
        if (utcMillis == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date(utcMillis));
    }

    public static synchronized Long dateStringToUtcMillis(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(dateStr.trim());
            return date != null ? date.getTime() : null;
        } catch (ParseException e) {
            return null;
        }
    }

    public static synchronized Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return dateFormat.parse(dateStr.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    public static synchronized Date parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return null;
        try {
            return dateTimeFormat.parse(dateTimeStr.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Kiem tra han nop co hop le khong (hanNop >= ngayBatDau)
     */
    public static boolean isValidDateRange(String startDateStr, String endDateStr) {
        Date start = parseDate(startDateStr);
        Date end = parseDate(endDateStr);
        if (start == null || end == null) {
            return true;
        }
        return !end.before(start);
    }

    /**
     * Kiem tra thoi gian cuoc hen da qua chua
     */
    public static boolean isPastDateTime(String dateTimeStr) {
        Date date = parseDateTime(dateTimeStr);
        if (date == null) return false;
        return date.before(new Date());
    }

    /**
     * Hien thi DatePickerDialog de chon ngay (yyyy-MM-dd)
     */
    public static void showDatePicker(Context context, String initialDate, OnDateSelectedListener listener) {
        Calendar calendar = Calendar.getInstance();
        Date parsed = parseDate(initialDate);
        if (parsed != null) {
            calendar.setTime(parsed);
        }

        DatePickerDialog dialog = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);
                    synchronized (DateUtils.class) {
                        listener.onDateSelected(dateFormat.format(selected.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    /**
     * Hien thi DatePicker va TimePicker lien tiep de chon ngay gio (yyyy-MM-dd HH:mm)
     */
    public static void showDateTimePicker(Context context, String initialDateTime, OnDateTimeSelectedListener listener) {
        Calendar calendar = Calendar.getInstance();
        Date parsed = parseDateTime(initialDateTime);
        if (parsed != null) {
            calendar.setTime(parsed);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    Calendar chosen = Calendar.getInstance();
                    chosen.set(Calendar.YEAR, year);
                    chosen.set(Calendar.MONTH, month);
                    chosen.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            context,
                            (timeView, hourOfDay, minute) -> {
                                chosen.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                chosen.set(Calendar.MINUTE, minute);
                                chosen.set(Calendar.SECOND, 0);
                                synchronized (DateUtils.class) {
                                    listener.onDateTimeSelected(dateTimeFormat.format(chosen.getTime()));
                                }
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
