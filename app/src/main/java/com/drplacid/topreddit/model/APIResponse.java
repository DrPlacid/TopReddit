package com.drplacid.topreddit.model;

import java.util.List;

public class APIResponse {

    private ResponseData data;

    static class ResponseData {
        List<PostItem> children;

        private String before;

        private String after;
    }

    public List<PostItem> getPosts() {
        return data.children;
    }

    public String getAfter() {
        return data.after;
    }
}
