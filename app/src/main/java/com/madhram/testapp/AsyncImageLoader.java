package com.madhram.testapp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class AsyncImageLoader extends AsyncTask<ImageView, Void, Bitmap> {

    @SuppressLint("StaticFieldLeak")
    private ImageView imageView = null;

    @Override
    protected Bitmap doInBackground(ImageView... imageViews) {
        imageView = imageViews[0];
        return downloadImage((String) imageView.getTag());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
    }

    private Bitmap downloadImage(String urlString){
        Bitmap bitmap = null;
        try {
            InputStream in = new URL(urlString).openConnection().getInputStream();
            bitmap = BitmapFactory.decodeStream(in);
            if (in != null) in.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return bitmap;
    }
}