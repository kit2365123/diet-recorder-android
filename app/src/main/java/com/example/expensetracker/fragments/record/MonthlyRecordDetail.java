package com.example.expensetracker.fragments.record;

import java.util.Date;

public class MonthlyRecordDetail {
    public String month;
    public String year;
    public String day;
    public Date startDate;
    public Date endDate;
    public int itemNum;
    public float totalPrice;
    public float monthLimit;

    public MonthlyRecordDetail(Date startDate, Date endDate) {
        //this.month = month;
        this.startDate = startDate;
        this.endDate = endDate;
        //this.totalPrice = totalPrice;
    }

}
