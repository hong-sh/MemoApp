package com.hongdroid.memoapp.model;

import java.util.ArrayList;

public class MemoItem {
    private long idx;
    private String title;
    private String contents;
    private ArrayList<byte[]> imageList;
    private long updateDate;

    public MemoItem(long idx, String title, String contents, ArrayList<byte[]> imageList, long updateDate) {
        this.idx = idx;
        this.title = title;
        this.contents = contents;
        this.imageList = imageList;
        this.updateDate = updateDate;
    }

    public MemoItem(String title, String contents, ArrayList<byte[]> imageList) {
        this.title = title;
        this.contents = contents;
        this.imageList = imageList;
    }

    public long getIdx() {
        return idx;
    }

    public void setIdx(long idx) {
        this.idx = idx;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public ArrayList<byte[]> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<byte[]> imageList) {
        this.imageList = imageList;
    }

    public long getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate(long updateDate)
    {
        this.updateDate = updateDate;
    }

    public byte[] getThumbNail()
    {
        if(imageList.size() > 0)
            return imageList.get(0);

        return null;
    }
}
