package com.easy.kotlintest.Helper;

import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.easy.kotlintest.Networking.Interface.AllInterFace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SaveWithProgress {

    String ur = "";
    File file;
    String temp_path = "";
    ProgressBar progressBar;
    Handler handler;

    AllInterFace allInterface;

    public SaveWithProgress(String url, File file, ProgressBar progressBar, Handler handler) {
        this.ur = url;
        this.file = file;
        this.progressBar = progressBar;
        this.handler = handler;
    }

    public void setAllInterface(AllInterFace allInterface) {
        this.allInterface = allInterface;
    }

    public void save() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File temp_file = new File(temp_path);
                if (file.exists()) {
                    file.delete();
                }
                if (temp_file.exists()) {
                    temp_file.delete();
                }

                try {
                    file.createNewFile();

                    URL url = new URL(ur);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    int contentLength = connection.getContentLength();
                    FileOutputStream out = new FileOutputStream(file);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setMax(contentLength);
                        }
                    });
                    int progress = 0;
                    int count = 0;
                    int size = 0;

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = input.read(buffer)) != -1) {
                        size = input.available();
                        out.write(buffer, 0, read);
                        count += read;

                        int finalCount = count;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(finalCount);
                                // Log.e("TAG", "run: "+finalCount );
                            }
                        }, 500);
                    }
                    out.flush();
                    out.close();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            if (allInterface != null) {
                                allInterface.isClicked(true);
                            }

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    file.delete();
                }

            }
        }).start();
    }


}
