package com.drplacid.topreddit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.drplacid.topreddit.model.Response;

import java.util.Objects;
import java.util.Stack;


public class RedditViewModel extends AndroidViewModel {

    private int limit;

    private RedditRepository repository;
    private MutableLiveData<Response> postItemsLiveData;

    private Stack<String> breakpoints = new Stack<>();
    private String before = "null";
    private String after = "null";

    public RedditViewModel(@NonNull Application application) {
        super(application);
        repository = new RedditRepository();
    }

    public void init(int limit) {
        this.limit = limit;
        repository.get(limit, before, after);
        postItemsLiveData = repository.getResponseMutableLiveData();
    }

    public void goToNextPage() {
        if (postItemsLiveData != null) {
            after = Objects.requireNonNull(postItemsLiveData.getValue()).getAfter();
            breakpoints.push(after);
        }
        repository.get(limit, "null", after);
    }

    public void goToPreviousPage() {
        if (!breakpoints.empty()) {
            before = breakpoints.pop();
        } else {
            before = "null";
        }
        repository.get(limit, before, "null");
    }

    public MutableLiveData<Response> getPostItemsLiveData() {
        return postItemsLiveData;
    }
}


