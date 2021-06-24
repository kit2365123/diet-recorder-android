package com.example.expensetracker.function;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateFunctions {

    // count how many month
    public static int countMonth(int startYear, int startMonth, int endYear, int endMonth) {
        int monthCounter = 0;
        int m = startMonth;

        for(int i = startYear; i <= endYear; i++) {
            if(i != endYear) {
                for(int j = m; j <= 12; j++) {
                    Log.d("j", j+"_");
                    monthCounter++;
                    if(j == 12) {
                        m = 1;
                    }
                }
            } else {
                for(int j = m; j <= endMonth; j++) {
                    monthCounter++;
                }
            }
        }
        return monthCounter;
    }

    // count how many month
    public static int countMonthDay(int year, int month) {
        int iYear = year;
        int iMonth = month-1; // 1 (months begin with 0)
        int iDay = 1;

        // Create a calendar object and set year and month
        Calendar mycal = new GregorianCalendar(iYear, iMonth, iDay);

        // Get the number of days in that month
        return mycal.getActualMaximum(Calendar.DAY_OF_MONTH); // 28
    }



    public static Date getCurrentDate() {
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime;
    }

    public static Date[] getCurrentStartEndDate() {
        Date currentTime = Calendar.getInstance().getTime();
        String startDate = (String) android.text.format.DateFormat.format("dd-MM-yyyy 00:00:00", currentTime);
        String endDate = (String) android.text.format.DateFormat.format("dd-MM-yyyy 23:59:59", currentTime);
        Date sDate = parseDate(startDate);
        Date eDate = parseDate(endDate);

        return new Date[]{sDate, eDate};
    }

    public static int getDayNum() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getCurrentDate());
        Date currentTime = getCurrentDate();
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int days = Integer.parseInt((String) android.text.format.DateFormat.format("dd", currentTime));
        return 0;
    }

    public static int getCurrentYear() {
        Date currentTime = Calendar.getInstance().getTime();
        String year = (String) android.text.format.DateFormat.format("yyyy", currentTime);
        return Integer.parseInt(year);
    }

    public static int getCurrentMonth() {
        Date currentTime = Calendar.getInstance().getTime();
        String month = (String) android.text.format.DateFormat.format("MM", currentTime);
        return Integer.parseInt(month);
    }

    @SuppressLint("SimpleDateFormat")
    private static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

}
