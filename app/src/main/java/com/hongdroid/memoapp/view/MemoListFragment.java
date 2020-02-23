package com.hongdroid.memoapp.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hongdroid.memoapp.R;
import com.hongdroid.memoapp.adapter.MemoRecyclerViewAdapter;
import com.hongdroid.memoapp.model.MemoItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MemoListFragment extends Fragment implements MemoRecyclerViewAdapter.MemoItemClickListener{

    private RecyclerView RecyclerViewMemo;
    private MemoRecyclerViewAdapter memoRecyclerViewAdapter;
    private ArrayList<MemoItem> memoItemList;

    public MemoListFragment() {
        this.memoItemList = new ArrayList<>();
    }

    public MemoListFragment(ArrayList<MemoItem> memoItemList)
    {
        this.memoItemList = memoItemList;
    }

    public static MemoListFragment newInstance(ArrayList<MemoItem> memoItemList) {
        return new MemoListFragment(memoItemList);
    }

    // Set MemoItemList to Adapter And Update View Through NotifyDataChanged
    public void setMemoItemList(ArrayList<MemoItem> memoItemList)
    {
        this.memoItemList = memoItemList;
        memoRecyclerViewAdapter.setMemoList(memoItemList);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.memo_list_fragment, container, false);

        String dateStr = new SimpleDateFormat("yy년 MM월 dd일.").format(new Date());
        ((TextView)rootView.findViewById(R.id.textView_date)).setText(dateStr);

        RecyclerViewMemo = rootView.findViewById(R.id.recyclerView_memo);
        RecyclerViewMemo.setLayoutManager(new LinearLayoutManager(getContext()));
        memoRecyclerViewAdapter = new MemoRecyclerViewAdapter(getContext(), memoItemList, this);
        RecyclerViewMemo.setAdapter(memoRecyclerViewAdapter);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public interface RecyclerViewItemClickListener {
        void onRecyclerViewItemClickListener(int recyclerViewItemPosition);
    }

    @Override
    public void onMemoItemClickListener(int memoItemPosition) {
        ((RecyclerViewItemClickListener)getActivity()).onRecyclerViewItemClickListener(memoItemPosition);
    }
}
