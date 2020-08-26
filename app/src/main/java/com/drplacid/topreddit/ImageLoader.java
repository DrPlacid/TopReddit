package com.drplacid.topreddit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class ImageLoader {

    private LruCache<String, Bitmap> memoryCache;

    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final int cacheSize = maxMemory / 8;

    private static ImageLoader instance;

    public static ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    public ImageLoader() {
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void loadToPost(@NonNull String url, PostViewHolder holder) {
        if (memoryCache.get(url) == null) {
            new LoadToPostAsyncTask(holder, memoryCache).execute(url);
        } else {
            Bitmap img = memoryCache.get(url);
            holder.setThumbnail(img);
        }
    }

    public Bitmap loadFullSize(String url) {
        try {
            return new LoadFullSizeAsyncTask().execute(url).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class LoadToPostAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private PostViewHolder holder;
        LruCache<String, Bitmap> memoryCache;

        public LoadToPostAsyncTask(PostViewHolder holder, LruCache<String, Bitmap> memoryCache) {
            this.holder = holder;
            this.memoryCache = memoryCache;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            if (!strings[0].equals("null")) {
                try {
                    URL url = new URL(strings[0]);
                    Bitmap b = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    Bitmap resized = Bitmap.createScaledBitmap(b, 120, 120, false);
                    memoryCache.put(strings[0], resized);
                    return resized;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            holder.setThumbnail(bitmap);
        }
    }

    private static class LoadFullSizeAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                return BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
