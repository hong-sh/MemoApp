package com.hongdroid.memoapp.view;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hongdroid.memoapp.R;
import com.hongdroid.memoapp.adapter.ImageRecyclerViewAdapter;
import com.hongdroid.memoapp.model.MemoItem;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MemoWriteFragment extends Fragment{

    private static final int INTENT_FLAG_CAMERA = 1;
    private static final int INTENT_FLAG_GALLERY = 2;

    private WriteMemoListener writeMemoListener;

    private EditText editTextTitle;
    private EditText editTextContents;
    private RecyclerView recyclerViewImage;
    private ImageRecyclerViewAdapter imageRecyclerViewAdapter;
    private View viewImageAdd;
    private View viewCropView;
    private CropImageView cropImageView;

    private String currentPhotoPath;
    private Uri imageSource;

    private MemoItem editMemoItem;
    private String tmpTitleStr;
    private String tmpContentsStr;

    public MemoWriteFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MemoWriteFragment newInstance() {
        return new MemoWriteFragment();
    }

    public void setEditMemoItem(MemoItem memoItem)
    {
        editMemoItem = memoItem;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.memo_write_fragment, container, false);
        initLayout(rootView);
        return rootView;
    }

    private void initLayout(View rootView)
    {
        editTextTitle = rootView.findViewById(R.id.editText_title);
        recyclerViewImage = rootView.findViewById(R.id.recyclerView_image);
        editTextContents = rootView.findViewById(R.id.editText_contents);
        viewImageAdd = rootView.findViewById(R.id.layout_imageAdd);
        viewCropView = rootView.findViewById(R.id.layout_cropView);
        cropImageView = rootView.findViewById(R.id.cropImageView);

        ((ImageView)rootView.findViewById(R.id.button_cancel)).setOnClickListener(menuButtonClickListener);
        ((ImageView)rootView.findViewById(R.id.button_add_image)).setOnClickListener(menuButtonClickListener);
        ((ImageView)rootView.findViewById(R.id.button_complete)).setOnClickListener(menuButtonClickListener);
        ((TextView)rootView.findViewById(R.id.textView_hidden_title)).setOnClickListener(menuButtonClickListener);

        ((ImageView)rootView.findViewById(R.id.button_camera)).setOnClickListener(imageAddButtonClickListener);
        ((ImageView)rootView.findViewById(R.id.button_gallery)).setOnClickListener(imageAddButtonClickListener);
        ((ImageView)rootView.findViewById(R.id.button_link)).setOnClickListener(imageAddButtonClickListener);
        ((ImageView)rootView.findViewById(R.id.button_close)).setOnClickListener(imageAddButtonClickListener);
        ((ImageView)rootView.findViewById(R.id.button_save)).setOnClickListener(imageAddButtonClickListener);

        recyclerViewImage.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        imageRecyclerViewAdapter = new ImageRecyclerViewAdapter(getContext(), new ArrayList<byte[]>(), ImageRecyclerViewAdapter.MODE_EDIT);
        recyclerViewImage.setAdapter(imageRecyclerViewAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(editMemoItem != null)
        {
            ArrayList<byte[]> imageList = new ArrayList<>();
            editTextTitle.setText(editMemoItem.getTitle());
            editTextContents.setText(editMemoItem.getContents());
            for(byte[] image : editMemoItem.getImageList())
            {
                imageList.add(image);
            }
            imageRecyclerViewAdapter.setImageList(imageList);
        }
        else
        {
            editTextTitle.setText(tmpTitleStr);
            editTextContents.setText(tmpContentsStr);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        editMemoItem = null;
    }

    @Override
    public void onActivityResult(int reqeustCode, int resultCode, Intent data)
    {
        switch (reqeustCode)
        {
            case INTENT_FLAG_CAMERA:
                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                    imageSource = FileProvider.getUriForFile(getContext(), "com.hongdroid.memoapp.provider", new File(currentPhotoPath));
                else {
                    if(currentPhotoPath == null)
                        break;
                    imageSource = Uri.fromFile(new File(currentPhotoPath));
                }
                callCropView(imageSource);
                break;
            case INTENT_FLAG_GALLERY:
                if(data != null)
                    imageSource = data.getData();
                callCropView(imageSource);
                break;
        }
    }

    private void callCropView(Uri imageSource)
    {
        viewImageAdd.setVisibility(View.GONE);
        viewCropView.setVisibility(View.VISIBLE);
        // Use Library 'com.isseiaoki:simplecropview:1.1.8'
        cropImageView.setCustomRatio(17, 10);
        cropImageView.load(imageSource)
                .useThumbnail(true)
                .execute(new LoadCallback(){

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onSuccess() {
                    }
                });

    }

    View.OnClickListener menuButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.button_cancel:
                    getActivity().getSupportFragmentManager().popBackStack();
                    break;
                case R.id.button_add_image:
                    viewImageAdd.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    break;
                case R.id.button_complete:
                    String title = editTextTitle.getText().toString();
                    String contents = editTextContents.getText().toString();
                    if(title.length() <=0) {
                        Toast.makeText(getContext(), R.string.request_title_input, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    ArrayList<byte[]> imageList = imageRecyclerViewAdapter.getImageList();
                    MemoItem memoItem = new MemoItem(title, contents, imageList);
                    if(editMemoItem != null)
                        writeMemoListener.onWriteMemoListener(editMemoItem, memoItem);
                    else
                        writeMemoListener.onWriteMemoListener(memoItem);
                    break;
            }
        }
    };

    View.OnClickListener imageAddButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            tmpTitleStr = editTextTitle.getText().toString();
            tmpContentsStr = editTextContents.getText().toString();
            switch (view.getId())
            {
                case R.id.button_camera:
                    getImageFromCamera();
                    break;
                case R.id.button_gallery:
                    getImageFromGallery();
                    break;
                case R.id.button_link:
                    getImageFromUrl();
                    break;
                case R.id.button_close:
                    viewImageAdd.setVisibility(View.GONE);
                    break;
                case R.id.button_save:
                    cropImageView.cropAsync(imageSource, new CropCallback() {
                        @Override
                        public void onSuccess(Bitmap cropped) {
                            imageRecyclerViewAdapter.addImage(cropped);
                            viewCropView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
                    viewImageAdd.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private void getImageFromCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getContext().getPackageManager()) != null)
        {
            File imageFile = null;
            try {
                imageFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(imageFile != null) {

                Uri imageFromCameraUri = null;
                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                    imageFromCameraUri = FileProvider.getUriForFile(getContext(), "com.hongdroid.memoapp.provider", imageFile);
                else
                    imageFromCameraUri = Uri.fromFile(imageFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFromCameraUri);
                startActivityForResult(intent, INTENT_FLAG_CAMERA);

            }
        }

    }

    private void getImageFromGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, INTENT_FLAG_GALLERY);
    }

    private void getImageFromUrl()
    {
        final EditText editTextURL = new EditText(getContext());
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_MinWidth);
        dialogBuilder
                .setTitle("URL 입력")
                .setMessage("이미지 URL을 입력하세요.")
                .setIcon(R.drawable.ic_link)
                .setCancelable(true)
                .setView(editTextURL)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new GetImageFromURLTask().execute(editTextURL.getText().toString());

                    }
                });
        dialogBuilder.create().show();
    }

    private class GetImageFromURLTask extends AsyncTask<String, Void, File>
    {
        @Override
        protected File doInBackground(String... params) {
           try
            {
                URL url = new URL(params[0]);
                URLConnection conn = url.openConnection();
                int contentLength = conn.getContentLength();
                byte[] byteBuffer = new byte[contentLength];
                InputStream inputStream = conn.getInputStream();

                File imageFile = createImageFile();
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);

                int read;
                while(true)
                {
                    read = inputStream.read(byteBuffer);
                    if(read <= 0)
                        break;
                    fileOutputStream.write(byteBuffer, 0, read);
                }

                return imageFile;
            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(File result) {
            if(result != null)
            {
                Uri imageFromURLUri = FileProvider.getUriForFile(getContext(), "com.hongdroid.memoapp.provider", result);
                callCropView(imageFromURLUri);
            } else
            {
                Toast.makeText(getContext(), R.string.picture_url_error_str , Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TmpImage_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    public interface WriteMemoListener {
        // TODO: Update argument type and name
        void onWriteMemoListener(MemoItem memoItem);
        void onWriteMemoListener(MemoItem editMemoItem, MemoItem memoItem);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WriteMemoListener) {
            writeMemoListener = (WriteMemoListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        writeMemoListener = null;
    }


}
