package com.hongdroid.memoapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hongdroid.memoapp.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private Context context;
    private ArrayList<byte[]> imageList;
    private int currentMode;
    public static final int MODE_VIEW = 1;
    public static final int MODE_EDIT = 2;

    public ImageRecyclerViewAdapter(Context context, ArrayList<byte[]> imageList, int mode)
    {
        this.context = context;
        this.imageList = imageList;
        this.currentMode = mode;
    }

    // Set ImageList And Update View
    public void setImageList(ArrayList<byte[]> imageList)
    {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    // Add Image From Camera, Gallery, URL And Update View
    public void addImage(Bitmap image)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        imageList.add(0, stream.toByteArray());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.memo_image_cardview, parent, false);
        return new ImageRecyclerViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        byte[] memoImage = imageList.get(position);
        ImageRecyclerViewHolder viewHolder = (ImageRecyclerViewHolder)holder;
        viewHolder.imageViewMemoImage.setImageBitmap(BitmapFactory.decodeByteArray(memoImage, 0, memoImage.length));
        // If MemoWriteFragment mode is Edit, Image Remove Button Active
        if(currentMode == MODE_EDIT) {
            viewHolder.imageViewRemoveImage.setVisibility(View.VISIBLE);
            viewHolder.imageViewRemoveImage.setTag(position);
            viewHolder.imageViewRemoveImage.setOnClickListener(this);
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public ArrayList<byte[]> getImageList()
    {
        return imageList;
    }

    @Override
    public void onClick(View view) {
        int imagePosition = (int)view.getTag();
        imageList.remove(imagePosition);
        notifyDataSetChanged();
    }

    class ImageRecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewMemoImage;
        ImageView imageViewRemoveImage;

        public ImageRecyclerViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageViewMemoImage = itemView.findViewById(R.id.imageView_memoImage);
            imageViewMemoImage.setClipToOutline(true);
            imageViewRemoveImage = itemView.findViewById(R.id.button_removeImage);
        }
    }
}
