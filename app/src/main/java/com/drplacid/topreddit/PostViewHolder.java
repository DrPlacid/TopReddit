package com.drplacid.topreddit;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drplacid.topreddit.model.PostItem;

public class PostViewHolder extends RecyclerView.ViewHolder {

    private View gap;

    private TextView author;
    private TextView postedAt;
    private TextView comments;
    private TextView title;
    private ImageView thumbnail;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        author = itemView.findViewById(R.id.textUsername);
        postedAt = itemView.findViewById(R.id.textDate);
        comments = itemView.findViewById(R.id.commentsNumber);
        title = itemView.findViewById(R.id.textTitle);
        thumbnail = itemView.findViewById(R.id.image);
        gap = itemView.findViewById(R.id.gapView);
    }

    public void setData(PostItem item) {
        String name = item.getName();
        String date = String.valueOf(item.getDate());
        String thumbnailURL = item.getImageUrl();
        String commentsCount = String.valueOf(item.getCommentsCount());
        String titleText = item.getTitle();

        author.setText(name);
        postedAt.setText(date);
        comments.setText(commentsCount);
        title.setText(titleText);

        if (!thumbnailURL.isEmpty()) {
            Bitmap img = ImageLoader.load(thumbnailURL);
            thumbnail.setImageBitmap(img);
            if (img == null) {
                gap.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT));
            }
        }

    }


}
