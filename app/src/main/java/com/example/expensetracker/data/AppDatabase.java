package com.example.expensetracker.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Record.class, Keyword.class}, version = 1)
@TypeConverters(TimestampConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase appDatabase;

    public static synchronized AppDatabase getDatabase(Context context) {
        if(appDatabase == null) {
            appDatabase = Room.databaseBuilder(
                    context,
                    AppDatabase.class,
                    "records_db"
            ).build();
        }
        return appDatabase;
    }
    public abstract RecordDao recordDao();
    public abstract KeywordDao keywordDao();
}
