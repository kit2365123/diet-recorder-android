package com.example.expensetracker.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface KeywordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertKeyword(Keyword keyword);

    @Query("SELECT word FROM keywords")
    List<String> getAllWord();

    @Query("SELECT DISTINCT record_id FROM keywords WHERE LOWER(word) LIKE '%' || :word || '%'")
    List<Integer> getRecordIdByWord(String word);
}
