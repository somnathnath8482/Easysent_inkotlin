package com.easy.kotlintest.Networking.Helper;

import static com.easy.kotlintest.Networking.Helper.Constants.BASE_URL;
import static com.easy.kotlintest.Networking.Helper.Constants.CATCH_DIR;
import static com.easy.kotlintest.Networking.Helper.Constants.CATCH_DIR2;
import static com.easy.kotlintest.Networking.Helper.Constants.LOGOUT;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.easy.kotlintest.Helper.PrefFile.PrefUtill;
import com.easy.kotlintest.Networking.Network;
import com.easy.kotlintest.R;
import com.easy.kotlintest.Response.Error.Error;
import com.easy.kotlintest.activity.LoginActivity;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

import io.github.muddz.styleabletoast.StyleableToast;

public class MethodClass {



    public static JSONObject Json_rpc_format(HashMap<String, String> params) {
        HashMap<String, Object> main_param = new HashMap<String, Object>();
        main_param.put("params", new JSONObject(params));
        main_param.put("jsonrpc", "2.0");
        Log.e("request", new JSONObject(main_param).toString());
        return new JSONObject(main_param);
    }

    public static HashMap<String, Object> Json_rpc_format_hashmap(HashMap<String, String> params) {
        HashMap<String, Object> main_param = new HashMap<String, Object>();
        main_param.put("jsonrpc", "2.0");
        main_param.put("params", params);
        Log.e("request", main_param.toString());
        return main_param;
    }

    public static JSONObject Json_rpc_format_obj(HashMap<String, Object> params) {
        HashMap<String, Object> main_param = new HashMap<String, Object>();
        main_param.put("params", new JSONObject(params));
        main_param.put("jsonrpc", "2.0");
        Log.e("request", new JSONObject(main_param).toString());
        return new JSONObject(main_param);
    }

    public static boolean checkEditText(EditText editText) {
        if (editText != null && editText.getText().toString().trim().length() > 0) {

            return true;

        } else {
            return false;
        }
    }

    public static boolean hasError(Activity activity, Error error) {

        if (error != null) {

                try {
                    // activity.onBackPressed();
                    LottieAlertDialog lottieAlertDialog = new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_ERROR)
                            .setPositiveButtonColor(Color.parseColor("#048B3A"))
                            .setNegativeButtonColor(Color.parseColor("#DA1606"))
                            .setNoneButtonColor(Color.parseColor("#038DFC"))
                            .setPositiveTextColor(Color.WHITE)
                            .setNegativeTextColor(Color.WHITE)
                            .setNoneTextColor(Color.WHITE)
                            .setTitle("Error")
                            .setDescription(error.getMessage())
                            .setNoneText("OK")
                            .setNoneListener(Dialog::dismiss)
                            .build();

                    lottieAlertDialog.setCancelable(false);
                    lottieAlertDialog.setCanceledOnTouchOutside(false);
                    lottieAlertDialog.show();
                } catch (Exception e) {
                    StyleableToast.makeText(activity.getApplicationContext(), "" + error.getMessage(), R.style.mytoast).show();
                }


            return false;
        }


        return true;
    }
    public static int getResId(String resName, Class<?> c) {

        try {

            char[] initial = resName.toLowerCase(Locale.ROOT).toCharArray();
            String initial_char = "img_" + initial[0];

            Field idField = c.getDeclaredField(initial_char);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String CheckEmpty(EditText editText) throws Exception {
        if (editText.getText().toString().trim().equals("")) {
            if (editText.isFocusable()) {
                editText.setError("Please Enter " + editText.getHint());
                editText.requestFocus();
            } else {
                Toast.makeText(editText.getContext(), "Please Enter" + editText.getHint(), Toast.LENGTH_SHORT).show();
            }

            throw new Exception(editText.getId() + "");
        } else {
            return editText.getText().toString().trim();
        }
    }
    public static boolean isValidEmailId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public static void logout(Activity activity, String id) {

        new LottieAlertDialog.Builder(activity, DialogTypes.TYPE_QUESTION)

                .setTitle("Logout")
                .setDescription("Are you sure you want to log out")
                .setPositiveText("Yes")
                .setNegativeText("No")
                .setPositiveTextColor(activity.getResources().getColor(R.color.white))
                .setNegativeTextColor(activity.getResources().getColor(R.color.white))
                .setPositiveListener(new ClickListener() {
                    @Override
                    public void onClick(@NonNull LottieAlertDialog lottieAlertDialog) {

                        Network network = new Network();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("id", id);


                        network.post(BASE_URL + LOGOUT ,"",map,(url, code, res) -> {

                        });
                        lottieAlertDialog.dismiss();
                        //todo
                      /*  String type = "STATUS";
                        Data data = new Data(type, PreferenceFile.getUser().getUser().getId(), "off");
                        Message message1 = new Message(data, "/topics/EASY_ALL");

                        Sender se = new Sender(false, message1);
                        MethodClass.SendNotificationOnTopic(se, activity.getApplication(), handler);*/
                        PrefUtill.Companion.clearSessionManager();
                        activity.finishAffinity();
                        activity.startActivity(new Intent(activity, LoginActivity.class));


                    }
                }).setNegativeListener(new ClickListener() {
                    @Override
                    public void onClick(@NonNull LottieAlertDialog lottieAlertDialog) {
                        lottieAlertDialog.dismiss();
                    }
                }).build().show();

    }

    public static void CashImage(String s, String s1, Drawable d) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(CATCH_DIR + "/" + s1);

                if (file.exists()) {
                    file.deleteOnExit();
                }

                try {
                    file.createNewFile();
                   /* Bitmap bm = ((BitmapDrawable) d).getBitmap();
                    FileOutputStream outStream = new FileOutputStream(file);
                    bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);

                    outStream.flush();
                    outStream.close();*/

                    new GetFile(file, s).execute();

                } catch (Exception e) {
                    e.printStackTrace();
                    file.delete();
                }

            }
        });
        th.run();
    }

    public static void CashImage2(String s, String s1) {

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(CATCH_DIR2 + "/" + s1);
                new GetFile(file, s).execute();
            }
        });
        th.start();

    }
    public static class GetFile extends AsyncTask<Void, Void, Void> {

        File file;
        String ur;

        public GetFile(File file, String ur) {
            this.file = file;
            this.ur = ur;
        }


        @Override
        protected Void doInBackground(Void... voids) {


            if (file.exists()) {
                file.delete();
            }

            try {
                file.createNewFile();

                URL url = new URL(ur);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                FileOutputStream out = new FileOutputStream(file);
                copyStream(input, out);
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
                file.delete();
            }


            return null;
        }
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
                /*out.flush();
                out.close();*/
        }
        out.flush();
        out.close();
    }

}
