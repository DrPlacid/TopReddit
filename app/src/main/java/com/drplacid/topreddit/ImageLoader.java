package com.drplacid.topreddit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class ImageLoader {

    public static Bitmap load(String url) {
        try {
            return new LoadAsyncTask().execute(url).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class LoadAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                Bitmap b = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return Bitmap.createScaledBitmap(b, 150, 150, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
