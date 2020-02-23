package com.hongdroid.memoapp.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.hongdroid.memoapp.model.ImageFromMemo;
import com.hongdroid.memoapp.model.Memo;

@Database(entities = {Memo.class, ImageFromMemo.class}, version = 2)
@TypeConverters({DateTimeConverter.class})
public abstract class AppDataBase extends RoomDatabase {
    public abstract MemoDao memoDao();
}
