package com.hongdroid.memoapp.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hongdroid.memoapp.R;
import com.hongdroid.memoapp.adapter.ImageRecyclerViewAdapter;
import com.hongdroid.memoapp.dao.DateTimeConverter;
import com.hongdroid.memoapp.model.MemoItem;

import java.text.SimpleDateFormat;


public class MemoDetailFragment extends Fragment implements View.OnClickListener {
    private ImageRecyclerViewAdapter imageRecyclerViewAdapter;
    private MemoItem memoItem;
    private TextView textViewTitle;
    private DetailMemoListener detailMemoListener;

    public MemoDetailFragment() {   }

    // TODO: Rename and change types and number of parameters
    public static MemoDetailFragment newInstance() {
        MemoDetailFragment fragment = new MemoDetailFragment();
        return fragment;
    }

    public void setMemoItem(MemoItem memoItem)
    {
        this.memoItem = memoItem;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.memo_detail_fragment, container, false);
        initLayout(rootView);
        return rootView;
    }

    private void initLayout(View rootView)
    {
        textViewTitle = ((TextView)rootView.findViewById(R.id.textView_title));
        textViewTitle.setText(memoItem.getTitle());
        textViewTitle.setPaintFlags(textViewTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        ((TextView)rootView.findViewById(R.id.textView_contents)).setText(memoItem.getContents());
        String dateStr = new SimpleDateFormat("yy년 MM월 dd일 HH시 mm분의 기록.").format(DateTimeConverter.fromTimeStamp(memoItem.getUpdateDate()));
        ((TextView)rootView.findViewById(R.id.textView_date)).setText(dateStr);

        ((ImageView)rootView.findViewById(R.id.button_delete)).setOnClickListener(this);
        ((ImageView)rootView.findViewById(R.id.button_edit)).setOnClickListener(this);
        ((ImageView)rootView.findViewById(R.id.button_complete)).setOnClickListener(this);

        RecyclerView recyclerViewImage = rootView.findViewById(R.id.recyclerView_image);
        recyclerViewImage.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(getContext(), memoItem.getImageList(), ImageRecyclerViewAdapter.MODE_VIEW);
        recyclerViewImage.setAdapter(imageRecyclerViewAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailMemoListener) {
            detailMemoListener = (DetailMemoListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        detailMemoListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            // Request Delete Confirm to User using Dialog
            // And Call DeleteListener When OK
            case R.id.button_delete:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
                dialogBuilder
                        .setTitle("삭제 확인")
                        .setMessage("기록을 삭제 하시겠습니까?")
                        .setIcon(R.drawable.ic_remove)
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                detailMemoListener.onRemoveMemoListener(memoItem);

                            }
                        });
                dialogBuilder.create().show();
                break;
            case R.id.button_edit:
                // Call EditListener With EditMemoItem
                detailMemoListener.onEditMemoListener(memoItem);
                break;
            case R.id.button_complete:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
    }

    public interface DetailMemoListener {
        // TODO: Update argument type and name
        void onEditMemoListener(MemoItem memoItem);
        void onRemoveMemoListener(MemoItem memoItem);
    }

}
