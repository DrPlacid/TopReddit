package com.drplacid.topreddit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton next = findViewById(R.id.next);
        FloatingActionButton prev = findViewById(R.id.prev);

        RedditViewModel viewModel = ViewModelProviders.of(MainActivity.this).get(RedditViewModel.class);

        viewModel.init();

        RecyclerView posts = findViewById(R.id.posts);
        posts.setLayoutManager(new LinearLayoutManager(this));
        posts.setHasFixedSize(true);

        PostAdapter adapter = new PostAdapter();
        posts.setAdapter(adapter);

        viewModel.getPostItemsLiveData().observe(this, response ->  {
            adapter.submitList(response.getPosts());
            viewModel.setBeforeAfter();
        });

        next.setOnClickListener(view -> viewModel.goToNextPage());
        prev.setOnClickListener(view -> viewModel.goToPreviousPage());
    }
}