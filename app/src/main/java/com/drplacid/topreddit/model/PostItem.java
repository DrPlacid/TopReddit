package com.drplacid.topreddit.model;



import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PostItem {

    private Data data;

    public String getId() {
        return data.id;
    }

    public String getName() {
        return data.author;
    }

    public String getTitle() {
        return data.title;
    }

    public String getDate() {
        Date postedTime = new Date(data.created*1000L);
        Date currentDate = new Date();
        long diff = currentDate.getTime() - postedTime.getTime();
        long hours = diff/(60 * 60 * 1000);
        return String.valueOf(hours);
    }

    public String getImageUrl() {
        return data.thumbnailURL;
    }

    public int getCommentsCount() {
        return data.comments;
    }


    static class Data {

        private String id;

        private String author;

        private String title;

        @SerializedName("created_utc")
        private long created;

        @SerializedName("thumbnail")
        private String thumbnailURL;

        @SerializedName("num_comments")
        private int comments;
    }
}
