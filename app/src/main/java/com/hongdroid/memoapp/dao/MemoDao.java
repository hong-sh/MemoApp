package com.hongdroid.memoapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hongdroid.memoapp.model.ImageFromMemo;
import com.hongdroid.memoapp.model.Memo;

import java.util.List;

@Dao
public interface MemoDao {

    @Query("SELECT * FROM memo ORDER BY update_date DESC")
    List<Memo> getAllMemo();

    @Query("SELECT * FROM ImageFromMemo WHERE memo_idx = :memo_idx AND `order` = 0")
    ImageFromMemo getThumbNailImageFromMemo(long memo_idx);

    @Query("SELECT * FROM ImageFromMemo WHERE memo_idx = :memo_idx ORDER BY 'order' ASC")
    List<ImageFromMemo> getImagesFromMemo(long memo_idx);

    @Insert
    long insertMemo(Memo memo);

    @Insert
    long[] insertImageFromMemo(List<ImageFromMemo> imageFromMemoList);

    @Query("DELETE FROM memo WHERE idx = :idx")
    void deleteMemo(long idx);

    @Query("DELETE FROM ImageFromMemo WHERE memo_idx = :memo_idx")
    void deleteImageFromMemo(long memo_idx);

}
