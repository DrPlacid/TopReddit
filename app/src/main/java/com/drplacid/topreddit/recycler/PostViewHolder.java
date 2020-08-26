package com.drplacid.topreddit.recycler;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drplacid.topreddit.util.ImageLoader;
import com.drplacid.topreddit.R;
import com.drplacid.topreddit.model.PostItem;

public class PostViewHolder extends RecyclerView.ViewHolder {

    private TextView header;
    private TextView comments;
    private TextView title;
    private ImageView thumbnail;

    private PostItem item;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);

        header = itemView.findViewById(R.id.textHeader);
        comments = itemView.findViewById(R.id.comments);
        title = itemView.findViewById(R.id.textTitle);
        thumbnail = itemView.findViewById(R.id.image);

        IPostListener listener = (IPostListener) itemView.getContext();

        thumbnail.setOnClickListener(view -> listener.loadFullSizeImage(item.getFullSizeImageUrl()));
    }

    public void setData(PostItem item) {
        this.item = item;

        String headerText = "Posted by u/" + item.getName() + " " + item.getDate() + " hours ago";
        String titleText = item.getTitle();
        String commentsText = item.getCommentsCount() + " comments";
        String thumbnailURL = item.getThumbnailUrl();

        header.setText(headerText);
        comments.setText(commentsText);
        title.setText(titleText);

        if (!thumbnailURL.equals("null")) {
            ImageLoader.getInstance().loadThumbnail(thumbnailURL, this);
        }
    }

    public void setThumbnail(Bitmap img) {
        thumbnail.setImageBitmap(img);
    }


}
