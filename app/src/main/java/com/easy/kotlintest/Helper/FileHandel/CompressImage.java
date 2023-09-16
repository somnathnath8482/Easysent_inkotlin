package com.easy.kotlintest.Helper.FileHandel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.easy.kotlintest.Networking.Helper.MethodClass;

import java.io.File;
import java.io.FileOutputStream;

public class CompressImage extends AsyncTask<Void, Void, String> {
    Onselect onselect;
    String source_path;
    private Context context;

    public CompressImage(Onselect onselect, String source_path, Context context) {
        this.onselect = onselect;
        this.source_path = source_path;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {
        File source_file = new File(source_path);


        if (source_file.exists()) {
            try {
                File imagePath = new File(context.getFilesDir(), "my_images");
                File terget_file = new File(imagePath.getAbsolutePath() + "/compressed_" + source_file.getName());

                if (!imagePath.exists()) {
                    imagePath.mkdirs();
                }
                if (terget_file.exists()) {
                    terget_file.delete();
                    terget_file.createNewFile();

                } else {
                    terget_file.createNewFile();
                }


                Bitmap bitmap = BitmapFactory.decodeFile(MethodClass.getRightAngleImage(source_path));
                FileOutputStream out = new FileOutputStream(terget_file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);

                return terget_file.getPath();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String str) {
        super.onPostExecute(str);

        if (onselect != null) {
            onselect.onSelect(str);
        }
    }
}
