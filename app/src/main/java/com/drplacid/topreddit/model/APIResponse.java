package com.drplacid.topreddit.model;

import java.util.List;

public class APIResponse {

    private Data data;

    static class Data {
        List<PostItem> children;

        private String before;

        private String after;
    }

    public List<PostItem> getPosts() {
        return data.children;
    }

    public String getBefore() {
        return data.before;
    }

    public String getAfter() {
        return data.after;
    }
}
