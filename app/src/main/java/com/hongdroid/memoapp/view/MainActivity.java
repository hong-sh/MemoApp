package com.hongdroid.memoapp.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hongdroid.memoapp.R;
import com.hongdroid.memoapp.dao.AppDataBase;
import com.hongdroid.memoapp.model.MemoItem;
import com.hongdroid.memoapp.viewmodel.MemoViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LifecycleOwner , MemoListFragment.RecyclerViewItemClickListener,
        MemoWriteFragment.WriteMemoListener, MemoDetailFragment.DetailMemoListener, FragmentManager.OnBackStackChangedListener {


    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int PERMISSION_RESULT_CODE = 101;

    private ProgressBar progressBar;
    FloatingActionButton fab;

    private MemoListFragment memoListFragment;
    private MemoWriteFragment memoWriteFragment;
    private MemoDetailFragment memoDetailFragment;

    private AppDataBase memoDB;
    private MemoViewModel memoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
        initLayout();

        memoDB = Room.databaseBuilder(getApplicationContext(), AppDataBase.class, "memo.db").build();

        memoViewModel = ViewModelProviders.of(this).get(MemoViewModel.class);
        memoViewModel.getMemoItemLiveData(memoDB).observe(this, memoItemUpdateObserver);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, memoWriteFragment, "Write")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void checkPermissions()
    {
        List<String> permissionRequestList = new ArrayList<>();
        for(String permission : permissions)
        {
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                permissionRequestList.add(permission);

        }

        if(!permissionRequestList.isEmpty())
            ActivityCompat.requestPermissions(this, permissionRequestList.toArray(new String[permissionRequestList.size()]), PERMISSION_RESULT_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResult)
    {
        if(requestCode == PERMISSION_RESULT_CODE)
        {
           if(grantResult.length < this.permissions.length)
               Toast.makeText(this, R.string.reqeust_permission, Toast.LENGTH_LONG).show();
        }
    }


    private void initLayout()
    {
        fab = findViewById(R.id.fab);
        fab.hide();
        progressBar = findViewById(R.id.progressBar);

        memoWriteFragment = MemoWriteFragment.newInstance();
        memoDetailFragment = MemoDetailFragment.newInstance();

        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    private Observer<ArrayList<MemoItem>> memoItemUpdateObserver = new Observer<ArrayList<MemoItem>>() {
        @Override
        public void onChanged(ArrayList<MemoItem> memoItemList)
        {
            progressBar.setVisibility(View.GONE);
            if(memoListFragment == null)
            {
                memoListFragment = MemoListFragment.newInstance(memoItemList);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, memoListFragment, "List")
                        .commit();
                fab.show();
                ((ImageView)findViewById(R.id.imageView_loading)).setVisibility(View.GONE);
            }
            else
            {
                memoListFragment.setMemoItemList(memoItemList);
            }

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRecyclerViewItemClickListener(int recyclerViewItemPosition) {
        memoDetailFragment.setMemoItem(memoViewModel.getMemoItem(recyclerViewItemPosition));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, memoDetailFragment, "Detail")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWriteMemoListener(MemoItem memoItem) {
        memoViewModel.writeMemo(memoDB, memoItem);
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onWriteMemoListener(MemoItem editMemoItem, MemoItem memoItem) {
        memoViewModel.updateMemo(memoDB, editMemoItem, memoItem);
        memoDetailFragment.setMemoItem(memoItem);
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onEditMemoListener(MemoItem memoItem) {
        memoWriteFragment.setEditMemoItem(memoItem);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, memoWriteFragment, "Edit")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onRemoveMemoListener(MemoItem memoItem) {
        memoViewModel.deleteMemo(memoDB, memoItem);
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onBackStackChanged() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        String tag = fragment.getTag();
        if(tag.equals("List"))
        {
            fab.show();
        } else
        {
            fab.hide();
        }
    }

}
