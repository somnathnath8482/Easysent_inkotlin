package com.easy.kotlintest.Helper;



import static android.graphics.BitmapFactory.decodeFile;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

public class ImageGetter extends AsyncTask<File, Void, Bitmap> {
    private ImageView iv;
    public ImageGetter(ImageView v) {
        iv = v;
    }
    @Override
    protected Bitmap doInBackground(File... params) {
        Bitmap bitmap = decodeFile(params[0].getAbsolutePath());
        if (bitmap.getHeight() > 4096 || bitmap.getWidth() > 4096) {
            int width = (int) (bitmap.getWidth() * 0.9);
            int height = (int) (bitmap.getHeight() * 0.9);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

            return resizedBitmap;
        } else {
            return bitmap;
        }
    }
    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);

        try {
            if (result!=null)
            Glide.with(iv.getContext())
                    .load(result)
                    .into(iv);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}