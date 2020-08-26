package com.drplacid.topreddit;

import androidx.lifecycle.MutableLiveData;

import com.drplacid.topreddit.model.APIResponse;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RedditRepository {

    private OkHttpClient client;
    public MutableLiveData<APIResponse> responseMutableLiveData;

    public RedditRepository() {
        client = new OkHttpClient();
        responseMutableLiveData = new MutableLiveData<>();
    }

    public void get(int limit, String before, String after) {

        final HttpUrl URL = new HttpUrl.Builder()
                .scheme("https")
                .host("www.reddit.com")
                .addPathSegment("top.json")
                .addQueryParameter("limit", String.valueOf(limit))
                .addQueryParameter("before", before)
                .addQueryParameter("after", after)
                .build();

        Request request = new Request.Builder()
                .url(URL)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                if(response.isSuccessful()) {
                    if (response.body() != null) {
                        final String mResponce = response.body().string();
                        APIResponse page = new Gson().fromJson(mResponce, APIResponse.class);
                        responseMutableLiveData.postValue(page);
                    }
                }
            }
        });

    }

    public MutableLiveData<APIResponse> getResponseMutableLiveData() {
        return responseMutableLiveData;
    }
}
