package com.easy.kotlintest.Networking.Helper;

import static com.easy.kotlintest.Networking.Helper.Constants.BASE_URL;
import static com.easy.kotlintest.Networking.Helper.Constants.CATCH_DIR_CASH;
import static com.easy.kotlintest.Networking.Helper.Constants.CATCH_DIR_Memory;
import static com.easy.kotlintest.Networking.Helper.Constants.LOGOUT;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.easy.kotlintest.Helper.PrefFile.PrefUtill;
import com.easy.kotlintest.Networking.Interface.AllInterFace;
import com.easy.kotlintest.Networking.Interface.OnMenuItemClick;
import com.easy.kotlintest.Networking.Network;
import com.easy.kotlintest.R;
import com.easy.kotlintest.Response.Error.Error;
import com.easy.kotlintest.Room.Messages.Message_View_Model;
import com.easy.kotlintest.activity.LoginActivity;
import com.easy.kotlintest.adapter.ViewPager.AttachmentViewpager;
import com.easy.kotlintest.databinding.FragmentViewAttachmentBinding;
import com.labters.lottiealertdialoglibrary.ClickListener;
import com.labters.lottiealertdialoglibrary.DialogTypes;
import com.labters.lottiealertdialoglibrary.LottieAlertDialog;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import io.github.muddz.styleabletoast.StyleableToast;

public class MethodClass {

    public static String changeDateFormat2(String date_str) {
        String date_output = "";
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        input.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");
        output.setTimeZone(TimeZone.getDefault());
        try {
            Date oneWayTripDate = input.parse(date_str);
            date_output = output.format(oneWayTripDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date_output;
    }

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
            return R.drawable.img_e;
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


                        network.post(BASE_URL + LOGOUT, "", map, (url, code, res) -> {

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

    public static void CashImageInMemory(String file_name, @Nullable Bitmap bm) {

        Thread th = new Thread(() -> {

            File file = new File(CATCH_DIR_Memory + "/" + file_name);

            if (file.exists()) {
                file.delete();
            }

            try {
                file.createNewFile();

                FileOutputStream outStream = new FileOutputStream(file);
                getResizedBitmap(bm, 50).compress(Bitmap.CompressFormat.PNG, 5, outStream);

                outStream.flush();
                outStream.close();


            } catch (Exception e) {
                e.printStackTrace();
                file.delete();
            }

        });
        th.run();
    }


public static void CashImageInMemoryOriginalQuality(String file_name, @Nullable Bitmap bm) {

        Thread th = new Thread(() -> {

            File file = new File(CATCH_DIR_Memory + "/" + file_name);

            if (file.exists()) {
                file.delete();
            }

            try {
                file.createNewFile();

                FileOutputStream outStream = new FileOutputStream(file);
              bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);

                outStream.flush();
                outStream.close();


            } catch (Exception e) {
                e.printStackTrace();
                file.delete();
            }

        });
        th.run();
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static void CashImageInCatch(String file_name, Bitmap bm) {
        Thread th = new Thread(() -> {

            File file = new File(CATCH_DIR_CASH + "/" + file_name);

            if (file.exists()) {
                file.delete();
            }

            try {
                file.createNewFile();

                FileOutputStream outStream = new FileOutputStream(file);
                getResizedBitmap(bm, 50).compress(Bitmap.CompressFormat.PNG, 5, outStream);

                outStream.flush();
                outStream.close();


            } catch (Exception e) {
                e.printStackTrace();
                file.delete();
            }

        });
        th.run();
    }
    public static void CashImageInCatchOriginalQuality(String file_name, Bitmap bm) {
        Thread th = new Thread(() -> {

            File file = new File(CATCH_DIR_CASH + "/" + file_name);

            if (file.exists()) {
                file.delete();
            }

            try {
                file.createNewFile();

                FileOutputStream outStream = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 50, outStream);

                outStream.flush();
                outStream.close();


            } catch (Exception e) {
                e.printStackTrace();
                file.delete();
            }

        });
        th.run();
    }

    public static void CashImageFromUrl(String url, String s1) {

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(CATCH_DIR_Memory + "/" + s1);
                new GetFile(file, url).execute();
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

    public static String getRightAngleImage(String photoPath) {

        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree;

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 0;
                    break;
                default:
                    degree = 90;
            }

            return rotateImage(degree, photoPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoPath;
    }

    public static void chengeBackground(LinearLayout mainLay) {
        mainLay.setBackgroundColor(mainLay.getContext().getColor(R.color.seleted_bg));
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mainLay.setBackgroundColor(mainLay.getContext().getColor(R.color.transparent));
            }
        }, 1200);


    }

    public static void showFullScreen(Activity context, Handler handler, String thread, String chat_id) {
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_view_attachment, null);
        @NonNull FragmentViewAttachmentBinding binding = FragmentViewAttachmentBinding.bind(dialogView);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_DialogWhenLarge_DarkActionBar);
        // AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();


        Message_View_Model message_view_model = new Message_View_Model(context.getApplication());
        message_view_model.selectAttachmentNonPaged(thread, chat_id, chats -> {

            AttachmentViewpager adapter = new AttachmentViewpager(chats, context, handler);
            handler.post(
                    () -> {
                        binding.recycler.setAdapter(adapter);
                    });


            binding.recycler.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    adapter.pauseAll();

                }
            });


            dialog.setOnDismissListener(dialogInterface -> {
                try {
                    adapter.pauseAll();
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            });

        });

        binding.recycler.setOffscreenPageLimit(4);


        //dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //dialog.getWindow().setStatusBarColor(context.getResources().getColor(R.color.Green));
        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        dialog.show();


    }

    public static void report(Activity context, AllInterFace allInterFace, String name) {
/*

        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.report_lay, null);

        ReportLayBinding binding = ReportLayBinding.bind(dialogView.getRootView());

        binding.heading.setText("Want To Report " + name + " ?");
        binding.tvBlock.setText(" Check to Block " + name);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);

        builder.setView(dialogView);
        builder.setCancelable(true);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        binding.btCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        binding.btCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        binding.btreport.setOnClickListener(view -> {
            allInterFace.isClicked(binding.accept.isChecked());
            dialog.dismiss();
        });
*/

    }

    public static class GetFileBitmap extends AsyncTask<Void, Void, Bitmap> {

        String path;
        ImageView imageView;

        Context context;

        public GetFileBitmap(String path, ImageView imageView, Context context) {
            this.path = path;
            this.imageView = imageView;
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            FutureTarget<Bitmap> futureTarget = Glide.with(context)
                    .asBitmap()
                    .override(600, 600)
                    .load(path)
                    .submit();
            try {
                return futureTarget.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public static void show_popup_menu2(View imageView, Activity activity, OnMenuItemClick onMenuItemClick) {
        //Toast.makeText(activity, "clicked "+block, Toast.LENGTH_SHORT).show();
        Context wrapper = new ContextThemeWrapper(activity, R.style.pop_up_menu_style);
        PopupMenu popupMenu = new PopupMenu(wrapper, imageView, Gravity.BOTTOM | Gravity.END);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.messagemenu2, popupMenu.getMenu());
        popupMenu.show();


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                onMenuItemClick.OnClick(menuItem.getItemId());
                return true;
            }
        });
    }

    public static String rotateImage(int degree, String imagePath) {

        if (degree <= 0) {
            return imagePath;
        }
        try {
            Bitmap b = BitmapFactory.decodeFile(imagePath);

            Matrix matrix = new Matrix();
            if (b.getWidth() > b.getHeight()) {
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            } else if (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imagePath;
    }


    public static String[] storge_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storge_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.CAMERA
    };

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storge_permissions_33;
        } else {
            p = storge_permissions;
        }
        return p;
    }

    public static boolean isAllowed(Activity activity) {
        String[] permissions = MethodClass.permissions();
        List<String> reqper = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                reqper.add(permissions[i]);
            }
        }

        if (reqper.size() > 0) {
            activity.requestPermissions(reqper.toArray(new String[0]), 1890);
            return false;
        } else {
            return true;
        }

    }

    public static void cashattachmentImage2(File bm, String filename, Handler handler, Activity context, AllInterFace allInterFace) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = CATCH_DIR_Memory;
                File file = new File(path + "/" + filename + ".jpg");

                if (file.exists()) {
                    file.delete();
                }
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (file.exists()) {


                    try {
                        Uri returnUri = Uri.fromFile(bm);
                        InputStream inputStream = context.getContentResolver().openInputStream(returnUri);
                        FileOutputStream out = new FileOutputStream(file);
                        copyStream(inputStream, out);
                        out.close();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (file.exists()) {

                                    allInterFace.IsClicked(file.getAbsolutePath());
                                } else {
                                    allInterFace.IsClicked(null);
                                }

                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                        allInterFace.IsClicked(null);
                    }


                }


            }
        }).start();

    }

    public static void cashattachmentFILE2(File bm, String filename, Handler handler, Activity context, AllInterFace allInterFace) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = CATCH_DIR_Memory;
                File file = new File(path + "/" + filename + "_" + bm.getName());

                if (file.exists()) {
                    file.delete();
                }
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (file.exists()) {


                    try {
                        Uri returnUri = Uri.fromFile(bm);
                        InputStream inputStream = context.getContentResolver().openInputStream(returnUri);
                        FileOutputStream out = new FileOutputStream(file);
                        copyStream(inputStream, out);
                        out.close();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (file.exists()) {

                                    allInterFace.IsClicked(file.getAbsolutePath());
                                } else {
                                    allInterFace.IsClicked(null);
                                }

                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                        allInterFace.IsClicked(null);
                    }


                }


            }
        }).start();

    }

}
