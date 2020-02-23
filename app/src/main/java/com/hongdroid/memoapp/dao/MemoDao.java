package com.hongdroid.memoapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hongdroid.memoapp.model.ImageFromMemo;
import com.hongdroid.memoapp.model.Memo;

import java.util.List;

// Use Room DataBase Architecture
@Dao
public interface MemoDao {

    // Select All Memo Query
    @Query("SELECT * FROM memo ORDER BY update_date DESC")
    List<Memo> getAllMemo();

    // Select ThumbNail Image Query (Not Use)
    @Query("SELECT * FROM ImageFromMemo WHERE memo_idx = :memo_idx AND `order` = 0")
    ImageFromMemo getThumbNailImageFromMemo(long memo_idx);

    // Select Images From Memo Query
    @Query("SELECT * FROM ImageFromMemo WHERE memo_idx = :memo_idx ORDER BY 'order' ASC")
    List<ImageFromMemo> getImagesFromMemo(long memo_idx);

    // Insert Memo Query
    @Insert
    long insertMemo(Memo memo);

    // Insert Images From Memo Query
    @Insert
    long[] insertImageFromMemo(List<ImageFromMemo> imageFromMemoList);

    // Delete Memo Query
    @Query("DELETE FROM memo WHERE idx = :idx")
    void deleteMemo(long idx);

    // Delete Images From Memo Query
    @Query("DELETE FROM ImageFromMemo WHERE memo_idx = :memo_idx")
    void deleteImageFromMemo(long memo_idx);

}
