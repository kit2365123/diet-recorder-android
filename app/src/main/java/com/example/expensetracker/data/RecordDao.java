package com.example.expensetracker.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.Date;
import java.util.List;

@Dao
public interface RecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecord(Record record);

    @Query("SELECT COUNT(*) FROM records")
    Integer getNumOfRecords();

    @Query("SELECT record_id FROM records WHERE create_time = :date")
    Integer getRecordIdByDate(Date date);

    @Query("SELECT SUM(price) FROM records")
    float getTotalPrice();

    @Query("SELECT COUNT(*) FROM records WHERE create_time BETWEEN :from AND :to")
    Integer getRecordCountByDate(Date from, Date to);

    @Query("SELECT * FROM records WHERE create_time BETWEEN :from AND :to ORDER BY create_time DESC")
    List<Record> getRecordByDate(Date from, Date to);

    @Query("SELECT SUM(price) FROM records WHERE create_time BETWEEN :from AND :to")
    float getTotalPriceByDate(Date from, Date to);

    @Query("SELECT MIN(create_time) FROM records")
    Date getMinDate();

    @Query("SELECT MAX(create_time) FROM records")
    Date getMaxDate();

    @Query("SELECT * FROM records WHERE record_id = :id ORDER BY create_time DESC")
    Record getRecordById(int id);

    @Query("SELECT COUNT(*) FROM records WHERE item_type = :type AND create_time BETWEEN :from AND :to")
    Integer getRecordTypeCountByDate(Date from, Date to, String type);

    @Query("SELECT item_type FROM records GROUP BY item_type ORDER BY count(*) DESC LIMIT 1")
    String getFavoriteType();

}

