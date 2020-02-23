package com.hongdroid.memoapp.viewmodel;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hongdroid.memoapp.dao.AppDataBase;
import com.hongdroid.memoapp.dao.MemoDao;
import com.hongdroid.memoapp.model.ImageFromMemo;
import com.hongdroid.memoapp.model.Memo;
import com.hongdroid.memoapp.model.MemoItem;

import java.util.ArrayList;
import java.util.List;

public class MemoViewModel extends ViewModel {

    private MutableLiveData<ArrayList<MemoItem>> memoItemLiveData;
    private ArrayList<MemoItem> memoItemList;
    public MemoViewModel()
    {
        memoItemLiveData = new MutableLiveData<>();
        memoItemList = new ArrayList<>();
    }

    public MutableLiveData<ArrayList<MemoItem>> getMemoItemLiveData(AppDataBase memoDB)
    {
        new GetMemoListTask(memoDB.memoDao()).execute();
        return memoItemLiveData;
    }

    public MemoItem getMemoItem(int memoItemPosition)
    {
        return memoItemLiveData.getValue().get(memoItemPosition);
    }

    public void writeMemo(AppDataBase memoDB ,MemoItem memoItem)
    {
        new AddMemoTask(memoDB.memoDao()).execute(memoItem);
    }

    public void deleteMemo(AppDataBase memoDB, MemoItem memoItem)
    {
        new DeleteMemoTask(memoDB.memoDao()).execute(memoItem);
    }

    public void updateMemo(AppDataBase memoDB, MemoItem oldMemo, MemoItem newMemo)
    {
        new UpdateMemoTask(memoDB.memoDao()).execute(oldMemo, newMemo);
    }

    // Get All Memo List Async Task For MemoListFragment
    private class GetMemoListTask extends AsyncTask<Void, Void, ArrayList<MemoItem>>
    {
        private MemoDao memoDao;

        public GetMemoListTask(MemoDao memoDao)
        {
            this.memoDao = memoDao;
        }

        @Override
        protected ArrayList<MemoItem> doInBackground(Void... voids) {


            ArrayList<Memo> memoList = (ArrayList)memoDao.getAllMemo();
            for(Memo memo : memoList)
            {
                List<ImageFromMemo> imageFromMemoList = memoDao.getImagesFromMemo(memo.idx);
                ArrayList<byte[]> imageList = new ArrayList<>();
                for(ImageFromMemo imageFromMemo : imageFromMemoList)
                {
                    imageList.add(imageFromMemo.image);
                }
                memoItemList.add(new MemoItem(memo.idx, memo.title, memo.contents, imageList, memo.updateDate));
            }
            return memoItemList;
        }

        @Override
        protected void onPostExecute(ArrayList<MemoItem> result) {
            memoItemLiveData.setValue(result);
        }
    }

    // Insert Memo Async Task For MemoWriteFragment
    private class AddMemoTask extends AsyncTask<MemoItem, Void, ArrayList<MemoItem>>
    {
        private MemoDao memoDao;

        public AddMemoTask(MemoDao memoDao)
        {
            this.memoDao = memoDao;
        }

        @Override
        protected ArrayList<MemoItem> doInBackground(MemoItem... memoItems) {

            MemoItem memoItem = memoItems[0];
            long updateDate = System.currentTimeMillis();
            Memo memo = new Memo(memoItem.getTitle(), memoItem.getContents(), updateDate);
            long memoIdx = memoDao.insertMemo(memo);

            List<ImageFromMemo> imageFromMemoList = new ArrayList<>();
            for(int i = 0; i <memoItem.getImageList().size(); i++)
            {
                byte[] image = memoItem.getImageList().get(i);
                ImageFromMemo imageFromMemo = new ImageFromMemo(memoIdx, image, i);
                imageFromMemoList.add(imageFromMemo);
            }

            memoDao.insertImageFromMemo(imageFromMemoList);

            memoItem.setIdx(memoIdx);
            memoItem.setUpdateDate(updateDate);
            memoItemList.add(0, memoItem);
            return memoItemList;
        }

        @Override
        protected void onPostExecute(ArrayList<MemoItem> result) {
            memoItemLiveData.setValue(result);
        }
    }

    // Delete Memo Async Task For MemoDetailFragment
    private class DeleteMemoTask extends AsyncTask<MemoItem, Void, ArrayList<MemoItem>>
    {
        private MemoDao memoDao;

        public DeleteMemoTask(MemoDao memoDao)
        {
            this.memoDao = memoDao;
        }

        @Override
        protected ArrayList<MemoItem> doInBackground(MemoItem... memoItems) {

            long idx =  memoItems[0].getIdx();

            memoDao.deleteMemo(idx);
            memoDao.deleteImageFromMemo(idx);

            for(MemoItem memoItem : memoItemList)
            {
                if(memoItem.getIdx() == idx)
                {
                    memoItemList.remove(memoItem);
                    break;
                }
            }

            return memoItemList;
        }

        @Override
        protected void onPostExecute(ArrayList<MemoItem> result) {

                    memoItemLiveData.setValue(result);
        }
    }

    // Edit Memo Async Task For MemoWriteFragment
    private class UpdateMemoTask extends AsyncTask<MemoItem, Void, ArrayList<MemoItem>>
    {
        private MemoDao memoDao;

        public UpdateMemoTask(MemoDao memoDao)
        {
            this.memoDao = memoDao;
        }

        @Override
        protected ArrayList<MemoItem> doInBackground(MemoItem... memoItems) {

            long idx =  memoItems[0].getIdx();

            memoDao.deleteMemo(idx);
            memoDao.deleteImageFromMemo(idx);

            for(MemoItem memoItem : memoItemList)
            {
                if(memoItem.getIdx() == idx)
                {
                    memoItemList.remove(memoItem);
                    break;
                }
            }

            MemoItem memoItem = memoItems[1];
            long updateDate = System.currentTimeMillis()/1000;
            Memo memo = new Memo(memoItem.getTitle(), memoItem.getContents(), updateDate);
            long memoIdx = memoDao.insertMemo(memo);

            List<ImageFromMemo> imageFromMemoList = new ArrayList<>();
            for(int i = 0; i <memoItem.getImageList().size(); i++)
            {
                byte[] image = memoItem.getImageList().get(i);
                ImageFromMemo imageFromMemo = new ImageFromMemo(memoIdx, image, i);
                imageFromMemoList.add(imageFromMemo);
            }

            memoDao.insertImageFromMemo(imageFromMemoList);

            memoItem.setIdx(memoIdx);
            memoItemList.add(0, memoItem);

            return memoItemList;
        }

        @Override
        protected void onPostExecute(ArrayList<MemoItem> result) {
            memoItemLiveData.setValue(result);
        }
    }

}
