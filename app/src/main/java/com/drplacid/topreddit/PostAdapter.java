package com.drplacid.topreddit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.drplacid.topreddit.model.PostItem;

public class PostAdapter extends ListAdapter<PostItem, PostViewHolder> {


    protected PostAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<PostItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<PostItem>() {

        @Override
        public boolean areItemsTheSame(@NonNull PostItem oldItem, @NonNull PostItem newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull PostItem oldItem, @NonNull PostItem newItem) {
            return false;
        }
    };

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_post_item, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.setData(getItem(position));
    }
}
