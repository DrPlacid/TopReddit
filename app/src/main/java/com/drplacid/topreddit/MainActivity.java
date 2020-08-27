package com.drplacid.topreddit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Display;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.drplacid.topreddit.recycler.IPostListener;
import com.drplacid.topreddit.recycler.PostAdapter;
import com.drplacid.topreddit.util.AnimationManager;
import com.drplacid.topreddit.util.ImageLoader;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements IPostListener {

    private static Bitmap currentImg;
    private static int currentShownItems = 10;
    private static boolean isDataLoading = true;

    private SharedPreferences sPref;
    private RedditViewModel viewModel;

    private ImageView fullSize;
    private CoordinatorLayout root;
    private FrameLayout expandableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sPref = PreferenceManager.getDefaultSharedPreferences(this);
        currentShownItems = sPref.getInt("ITEMS_SHOWN", 10);

        viewModel = ViewModelProviders.of(MainActivity.this).get(RedditViewModel.class);
        viewModel.init(currentShownItems);

        RecyclerView posts = findViewById(R.id.posts);
        expandableLayout = findViewById(R.id.expandable);
        root =  findViewById(R.id.root);

        posts.setLayoutManager(new LinearLayoutManager(this));
        posts.setHasFixedSize(true);
        PostAdapter adapter = new PostAdapter();
        posts.setAdapter(adapter);

        viewModel.getPostItemsLiveData().observe(MainActivity.this, response ->  {
            adapter.submitList(response.getPosts());
            isDataLoading = false;
        });

        initBottomPanel();

        initImageViewLayout();
    }


    private void initImageViewLayout() {
        fullSize = findViewById(R.id.fullSizeImage);

        ImageButton close = findViewById(R.id.closeImageView);
        close.setOnClickListener(view -> closeImageView());

        ImageButton save = findViewById(R.id.saveImage);
        save.setOnClickListener(view -> saveImage());
    }

    private void initBottomPanel() {
        ImageButton next = findViewById(R.id.next);
        ImageButton prev = findViewById(R.id.prev);
        RadioGroup displayed = findViewById(R.id.radioGroup);

        next.setOnClickListener(view -> {
            if(!isDataLoading) {
                viewModel.goToNextPage();
                isDataLoading = true;
            }
        });
        prev.setOnClickListener(view -> {
            if(!isDataLoading) {
                viewModel.goToPreviousPage();
                isDataLoading = true;
            }
        });

        switch (currentShownItems) {
            case 10:
                ((RadioButton) findViewById(R.id.show10)).setChecked(true);
                break;
            case 25:
                ((RadioButton) findViewById(R.id.show25)).setChecked(true);
                break;
            case 50:
                ((RadioButton) findViewById(R.id.show50)).setChecked(true);
                break;
        }

        displayed.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.show10:
                    currentShownItems = 10;
                    break;
                case R.id.show25:
                    currentShownItems = 25;
                    break;
                case  R.id.show50:
                    currentShownItems = 50;
                    break;
            }
            viewModel.init(currentShownItems);
            isDataLoading = true;
        });
    }

    @Override
    public void loadFullSizeImage(String url) {
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        currentImg = ImageLoader.getInstance().loadFullSize(url, width);
        if (currentImg != null) {
            openImageView();
        }
    }

    private void openImageView() {
        fullSize.setImageBitmap(currentImg);
        AnimationManager.fullSizeImageShown = true;
        AnimationManager.verticalExpand(expandableLayout, root);
    }

    private void closeImageView() {
        AnimationManager.verticalCollapse(expandableLayout);
        new Handler().postDelayed(() -> {
            fullSize.setImageBitmap(null);
            AnimationManager.fullSizeImageShown = false;
        }, AnimationManager.ANIMATION_TIME);
    }

    @Override
    public void onBackPressed() {
        if (AnimationManager.fullSizeImageShown) {
            closeImageView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(connectionChangeReceiver);
    }

    @Override
    protected void onStop() {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("ITEMS_SHOWN", currentShownItems);
        ed.apply();

        super.onStop();
    }


    private BroadcastReceiver connectionChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (checkOnline()) {
                viewModel.init(currentShownItems);
            } else {
                Snackbar.make(root, "Application offline", Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    };

    public boolean checkOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void saveImage() {
        MediaStore.Images.Media.insertImage(getContentResolver(), currentImg, "image_from_reddit" , "saved_image_from_reddit");
        closeImageView();
    }


}