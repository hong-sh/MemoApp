package com.hongdroid.memoapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Memo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="idx")
    public long idx;

    @ColumnInfo(name = "memo_title")
    public String title;

    @ColumnInfo(name = "memo_contents")
    public String contents;

    @ColumnInfo(name = "update_date")
    public Long updateDate;

    public Memo(String title, String contents, Long updateDate)
    {
        this.idx = idx;
        this.title = title;
        this.contents = contents;
        this.updateDate = updateDate;
    }
}
