package com.easy.kotlintest.Networking;

import static com.easy.kotlintest.Networking.Helper.multipart.Utils.getParam;

import android.app.Application;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.easy.kotlintest.Networking.Helper.ApiClient;
import com.easy.kotlintest.Networking.Helper.ApiInterface;
import com.easy.kotlintest.Networking.Helper.ProgressRequestBody;
import com.easy.kotlintest.Networking.Helper.multipart.PART;
import com.easy.kotlintest.Networking.Helper.multipart.Params;
import com.easy.kotlintest.Networking.Interface.OnError;
import com.easy.kotlintest.Networking.Interface.OnSuccess;
import com.easy.kotlintest.R;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Main {
    private static final String TAG = "networkrequest";
    private final Callback<String> mcallback;
    private Handler handler;
    private Application application;
    private OnError onError;
    private OnSuccess onSuccess;
    private boolean Show_Progress = false;
    private Dialog progressDialog;
    ApiInterface apiInterface;


    public Main(Application application, Handler handler, OnError onError, OnSuccess onSuccess) {
        this.handler = handler;
        this.onError = onError;
        this.onSuccess = onSuccess;
        this.application = application;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        mcallback = new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                handler.post(() -> onSuccess.OnSucces(call.request().url().toString(), "200", response.body()));


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                handler.post(() -> {
                    onError.OnEror(call.request().url().toString(), 1 + "", t.getMessage());
                });

            }
        };
    }

    private void loader() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
      /*  progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);*/

        progressDialog = new Dialog(application.getApplicationContext());
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.progress_bar);


    }

    public void setShow_Progress(boolean show_Progress) {
        Show_Progress = show_Progress;
    }

    public void CALLGetRequest(String url, @Nullable String authHeader, HashMap<String, String> map) {
        authHeader = authHeader == null ? "" : authHeader;

        try {
            String finalAuthHeader = authHeader;
            new Thread(
                    () -> {
                        apiInterface.GETRequest(url, finalAuthHeader).enqueue(mcallback);
                    }
            ).start();
        } catch (Exception e) {
            onError.OnEror(url, 1 + "", e.getMessage());
        }

    }

    public void SendNotification(String url, String auth, String content, String data) {


        try {
            new Thread(
                    () -> {
                        apiInterface.PostReqJson(url, auth, data).enqueue(mcallback);


                    }
            ).start();
        } catch (Exception e) {
            onError.OnEror(url, 2 + "", e.getMessage());

        }


    }

    public void CallPostRequestFormdata(String url, @Nullable String authHeader, HashMap<String, Object> map) {


        authHeader = authHeader == null ? "" : authHeader;

        try {
            String finalAuthHeader = authHeader;
            new Thread(
                    () -> {
                        apiInterface.PostRequestFormData(url, finalAuthHeader, map).enqueue(mcallback);

                    }
            ).start();
        } catch (Exception e) {
            onError.OnEror(url, 1 + "", e.getMessage());

        }


    }

    public void CAllMultipartRequest(String url, @Nullable String authHeader, HashMap<String, Object> map, List<File> f, String file_key) {


        authHeader = authHeader == null ? "" : authHeader;

        try {
            String finalAuthHeader = authHeader;
            new Thread(
                    () -> {
                        apiInterface.
                                PostRequestMultipart(url, finalAuthHeader, Params.createMultiPart(new PART(file_key, f.get(0))), getParam(map))
                                .enqueue(mcallback);

                    }
            ).start();
        } catch (Exception e) {
            onError.OnEror(url, 1 + "", e.getMessage());

        }
    }

    public void CAllMultipartRequestWithprogress(String url, @Nullable String authHeader, HashMap<String, Object> map,
                                                 File f, String file_key, ProgressBar progress) {


        authHeader = authHeader == null ? "" : authHeader;
        handler.post(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.VISIBLE);
                progress.setMax(100);
                progress.setProgress(0);

            }
        });

        try {
            String finalAuthHeader = authHeader;
            new Thread(
                    () -> {

                        MimeTypeMap mimeTypesMap = MimeTypeMap.getSingleton();

                        String extention = MimeTypeMap.getFileExtensionFromUrl(f.getAbsolutePath());
                        String mimeType = mimeTypesMap.getMimeTypeFromExtension(extention);
                        ProgressRequestBody fileBody = new ProgressRequestBody(f, mimeType, new ProgressRequestBody.UploadCallbacks() {
                            @Override
                            public void onProgressUpdate(int percentage) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.setProgress(percentage);
                                    }
                                });
                            }

                            @Override
                            public void onError() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.setVisibility(View.GONE);


                                    }
                                });
                            }

                            @Override
                            public void onFinish() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.setVisibility(View.GONE);

                                    }
                                });
                            }
                        });
                        MultipartBody.Part filePart = MultipartBody.Part.createFormData(file_key, f.getName(), fileBody);


                        apiInterface.
                                PostRequestMultipart(url, finalAuthHeader, filePart, getParam(map))
                                .enqueue(mcallback);
                    }
            ).start();
        } catch (Exception e) {
            e.printStackTrace();
            onError.OnEror(url, 1 + "", e.getMessage());
            progress.setVisibility(View.GONE);

        }
    }


}
