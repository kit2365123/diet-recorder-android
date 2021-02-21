package com.example.expensetracker.data;

import android.annotation.SuppressLint;
import android.provider.SyncStateContract;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampConverter {

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date==null? null : date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp==null? null : new Date(timestamp);
    }
}
