package com.drplacid.topreddit;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

    private SharedPreferences sPref;
    private OkHttpClient client;
    public MutableLiveData<APIResponse> responseMutableLiveData;

    public RedditRepository(Context context) {
        sPref = PreferenceManager.getDefaultSharedPreferences(context);
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
                getStoredJson();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                if(response.isSuccessful()) {
                    if (response.body() != null) {
                        final String mResponce = response.body().string();
                        APIResponse page = new Gson().fromJson(mResponce, APIResponse.class);
                        responseMutableLiveData.postValue(page);
                        saveSharedPreferenceResponse(mResponce);
                    }
                }
            }
        });

    }

    public MutableLiveData<APIResponse> getResponseMutableLiveData() {
        return responseMutableLiveData;
    }

    private void saveSharedPreferenceResponse(String json) {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("LAST_RESPONSE", json);
        ed.apply();
    }

    private void getStoredJson() {
        String savedJson = sPref.getString("LAST_RESPONSE", "");
        if (savedJson != null) {
            APIResponse fakeResponse = new Gson().fromJson(savedJson, APIResponse.class);
            responseMutableLiveData.postValue(fakeResponse);
        }

    }

}
