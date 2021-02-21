package com.example.expensetracker.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = Record.class, version = 1)
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

}
