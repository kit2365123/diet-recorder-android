package com.example.expensetracker.data;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class RecordWithKeywords {
    @Embedded
    public Record record;

    @Relation(parentColumn = "record_id",
            entityColumn = "record_id")
    public List<Keyword> keywords;

}
