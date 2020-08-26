package com.drplacid.topreddit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements IPostListener {

    private static Bitmap currentImg;
    private static int currentShown = 10;

    private RedditViewModel viewModel;

    private ImageView fullSize;
    private RecyclerView posts;
    private CoordinatorLayout root;
    private FrameLayout expandableLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("CURRENTTIME", "onCreate" + SystemClock.currentThreadTimeMillis());

        posts = findViewById(R.id.posts);
        expandableLayout = findViewById(R.id.expandable);
        root =  findViewById(R.id.root);

        Log.i("CURRENTTIME", "getVM" + SystemClock.currentThreadTimeMillis());
        viewModel = ViewModelProviders.of(MainActivity.this).get(RedditViewModel.class);
        viewModel.init(currentShown);

        posts.setLayoutManager(new LinearLayoutManager(this));
        posts.setHasFixedSize(true);
        PostAdapter adapter = new PostAdapter();
        posts.setAdapter(adapter);

        viewModel.getPostItemsLiveData().observe(this, response ->  {
            adapter.submitList(response.getPosts());
            Log.i("CURRENTTIME", "submited" + SystemClock.currentThreadTimeMillis());
        });
        initBottomPanel();

        initImageViewLayout();
    }

    private void initImageViewLayout() {
        fullSize = findViewById(R.id.fullSizeImage);

        ImageButton close = findViewById(R.id.closeImageView);
        close.setOnClickListener(view -> closeImageView());

        ImageButton save = findViewById(R.id.saveImage);
        save.setOnClickListener(view -> saveImage("fghsh"));
    }

    private void initBottomPanel(){
        ImageButton next = findViewById(R.id.next);
        ImageButton prev = findViewById(R.id.prev);
        RadioGroup displayed = findViewById(R.id.radioGroup);

        next.setOnClickListener(view -> viewModel.goToNextPage());
        prev.setOnClickListener(view -> viewModel.goToPreviousPage());
        displayed.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.show10:
                    currentShown = 10;
                    break;
                case R.id.show25:
                    currentShown = 25;
                    break;
                case  R.id.show50:
                    currentShown = 50;
                    break;
            }
            viewModel.init(currentShown);
        });
    }

    @Override
    public void loadImage(String url) {
        currentImg = ImageLoader.getInstance().loadFullSize(url);
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

    private void saveImage(String image_name) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + image_name+ ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            currentImg.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}