package com.example.expensetracker.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "keywords")
public class Keyword implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "record_id")
    private int recordId;

    @ColumnInfo(name = "word")
    private String word;

    public int getId() {
        return id;
    }

    public int getRecordId() {
        return recordId;
    }

    public String getWord() {
        return word;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public void setWord(String word) {
        this.word = word;
    }

}
