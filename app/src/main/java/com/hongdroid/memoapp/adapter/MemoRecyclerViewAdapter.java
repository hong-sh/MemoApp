package com.hongdroid.memoapp.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hongdroid.memoapp.R;
import com.hongdroid.memoapp.dao.DateTimeConverter;
import com.hongdroid.memoapp.model.MemoItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MemoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<MemoItem> memoItemList;
    private MemoItemClickListener memoItemClickListener;

    public MemoRecyclerViewAdapter(Context context, ArrayList<MemoItem> memoItemList, MemoItemClickListener memoItemClickListener)
    {
        this.context = context;
        this.memoItemClickListener = memoItemClickListener;
        this.memoItemList = memoItemList;
    }

    public void setMemoList(ArrayList<MemoItem> memoItemList)
    {
        this.memoItemList = memoItemList;
        notifyDataSetChanged();
    }

    public interface MemoItemClickListener{
        void onMemoItemClickListener(int memoItemPosition);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.memo_list_cardview, parent, false);
        return new MemoRecyclerViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        MemoItem memoItem = memoItemList.get(position);
        MemoRecyclerViewHolder viewHolder = (MemoRecyclerViewHolder)holder;
        byte[] thumbNailImage = memoItem.getThumbNail();
        // Set ThumbNail ImageView VISIBLE When ImageList size > 0
        if(thumbNailImage != null) {
            viewHolder.thumbNailImageView.setVisibility(View.VISIBLE);
            viewHolder.thumbNailImageView.setImageBitmap(BitmapFactory.decodeByteArray(thumbNailImage, 0, thumbNailImage.length));
        }
        // Trim Title String
        String titleStr = memoItem.getTitle();
        if(titleStr.length() > 10)
        {
            titleStr = titleStr.substring(0, 10);
            titleStr +="...";
        }
        String dateStr = new SimpleDateFormat("yy년 MM월 dd일 HH시 mm분의 기록").format(DateTimeConverter.fromTimeStamp(memoItem.getUpdateDate()));
        // Trim Contents FirstLine String
        String contentFirstLineStr = memoItem.getContents().split("\n")[0];
        if(contentFirstLineStr.length() > 25)
        {
            contentFirstLineStr = contentFirstLineStr.substring(0, 25);
            contentFirstLineStr +="...";
        }

        viewHolder.memoTitle.setText(titleStr);
        viewHolder.memoDate.setText(dateStr);
        viewHolder.memoContents.setText(contentFirstLineStr);
        viewHolder.cardView.setTag(position);
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memoItemClickListener.onMemoItemClickListener((int)view.getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return memoItemList.size();
    }

    class MemoRecyclerViewHolder extends RecyclerView.ViewHolder {

        View cardView;
        ImageView thumbNailImageView;
        TextView memoTitle;
        TextView memoDate;
        TextView memoContents;

        public MemoRecyclerViewHolder(@NonNull View itemView)
        {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView_memo);
            thumbNailImageView = itemView.findViewById(R.id.imageView_memo_thumbnail);
            thumbNailImageView.setClipToOutline(true);
            memoTitle = itemView.findViewById(R.id.textView_memo_title);
            memoTitle.setPaintFlags(memoTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            memoDate = itemView.findViewById(R.id.textView_date);
            memoContents = itemView.findViewById(R.id.textView_memo_contents);
        }
    }
}
