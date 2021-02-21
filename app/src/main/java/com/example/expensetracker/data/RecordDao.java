package com.example.expensetracker.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface RecordDao {

    @Query("SELECT * FROM records ORDER BY id DESC")
    List<Record> getAllRecords();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecord(Record record);

    @Query("SELECT COUNT(*) FROM records")
    Integer getNumOfRecords();

    @Query("SELECT SUM(price) FROM records")
    Float getTotalPrice();
}
