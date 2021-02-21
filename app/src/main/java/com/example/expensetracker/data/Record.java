package com.example.expensetracker.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "records")
public class Record implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "item_name")
    private String itemName;

    @ColumnInfo(name = "price")
    private float price;

    @ColumnInfo(name = "create_time")
    @TypeConverters({TimestampConverter.class})
    private Date createTime;

    @ColumnInfo(name = "image_byte_array")
    private String imageByteArray;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getItemName() {
        return itemName;
    }

    public void setItemName(@NonNull String itemName) {
        this.itemName = itemName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getImageByteArray() {
        return imageByteArray;
    }

    public void setImageByteArray(String imageByteArray) {
        this.imageByteArray = imageByteArray;
    }
}
