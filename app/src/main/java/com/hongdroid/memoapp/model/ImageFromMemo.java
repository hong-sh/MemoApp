package com.hongdroid.memoapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class ImageFromMemo {

    @PrimaryKey(autoGenerate = true)
    public long idx;

    @ColumnInfo(name="memo_idx")
    public long memoIdx;

    @ColumnInfo(name="image")
    public byte[] image;

    @ColumnInfo(name="order")
    public int order;

    public ImageFromMemo(long memoIdx, byte[] image, int order)
    {
        this.memoIdx = memoIdx;
        this.image = image;
        this.order = order;
    }

}
