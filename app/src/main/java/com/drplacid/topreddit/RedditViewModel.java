package com.drplacid.topreddit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.drplacid.topreddit.model.Response;


public class RedditViewModel extends AndroidViewModel {

    private RedditRepository repository;
    private MutableLiveData<Response> postItemsLiveData;

    private String beforeOld;
    private String before = "null";
    private String after = "null";

    public RedditViewModel(@NonNull Application application) {
        super(application);
        repository = new RedditRepository();
    }

    public void init() {
        repository.get(10, before, after);
        postItemsLiveData = repository.getResponseMutableLiveData();
    }

    public void goToNextPage() {
        beforeOld = before;
        repository.get(10, before, after);
    }

    public void goToPreviousPage() {
        repository.get(10, beforeOld, before);
    }

    public void setBeforeAfter() {
        if (postItemsLiveData != null) {
            before = postItemsLiveData.getValue().getBefore();
            after = postItemsLiveData.getValue().getAfter();
        }
    }

    public MutableLiveData<Response> getPostItemsLiveData() {
        return postItemsLiveData;
    }
}


