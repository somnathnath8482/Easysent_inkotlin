package com.easy.kotlintest.Helper.FileHandel;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.easy.kotlintest.BuildConfig;
import com.easy.kotlintest.Helper.FileHandel.Onselect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class PickFile {
    ActivityResultLauncher pdf_launcher;
    ActivityResultLauncher<PickVisualMediaRequest> pic_13_kMedia;
    Onselect onselect;
    Handler handler;
    Context context;
    ActivityResultLauncher<Intent> capture_image ;
    private File fileo;
    public PickFile(AppCompatActivity activity, Context context, Handler handler) {
        this.handler = handler;
        this.context = context;
        pdf_launcher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            // Here, no request code

                            if (result.getData() != null) {
                                if (result.getData().getData() != null) {
                                    try {

                                        Uri returnUri = result.getData().getData();

                                        readUri(returnUri);


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    //If uploaded with the new Android Photos gallery
                                    ClipData clipData = result.getData().getClipData();
                                    // pickiT.getMultiplePaths(clipData);
                                }
                            }


                        }
                    }
                });
        pic_13_kMedia = activity.registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                readUri(uri);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        capture_image = activity. registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK) // 0 on real device , -1 on Emulator
                        {
                            Log.d("PhotoPicker", "capture image");
                            if (fileo!=null&&fileo.exists()){
                                //onselect.onSelect(fileo.getName(), fileo.getAbsolutePath());
                                new CompressImage(new Onselect() {
                                    @Override
                                    public void onSelect(String... strings) {
                                        if (strings!=null && strings.length>0){
                                            File compessed_file = new File(strings[0]);
                                            if (compessed_file.exists()){
                                                onselect.onSelect(compessed_file.getName(), compessed_file.getAbsolutePath());
                                            }
                                        }
                                    }
                                },fileo.getAbsolutePath(),context).execute();
                            }
                        }

                    }
                });
    }

    public PickFile(Fragment activity,  Context context,Handler handler) {
        this.handler = handler;
        this.context = context;

        pdf_launcher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            // Here, no request code

                            if (result.getData() != null) {
                                if (result.getData().getData() != null) {
                                    try {

                                        Uri returnUri = result.getData().getData();

                                        readUri(returnUri);


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    //If uploaded with the new Android Photos gallery
                                    ClipData clipData = result.getData().getClipData();
                                    // pickiT.getMultiplePaths(clipData);
                                }
                            }


                        }
                    }
                });
        pic_13_kMedia = activity.registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                readUri(uri);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        capture_image = activity. registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK) // 0 on real device , -1 on Emulator
                        {
                            Log.d("PhotoPicker", "capture image");
                            if (fileo!=null&&fileo.exists()){
                                //onselect.onSelect(fileo.getName(), fileo.getAbsolutePath());
                                new CompressImage(new Onselect() {
                                    @Override
                                    public void onSelect(String... strings) {
                                        if (strings!=null && strings.length>0){
                                            File compessed_file = new File(strings[0]);
                                            if (compessed_file.exists()){
                                                onselect.onSelect(compessed_file.getName(), compessed_file.getAbsolutePath());
                                            }
                                        }
                                    }
                                },fileo.getAbsolutePath(),context).execute();
                            }
                        }

                    }
                });
    }


    public void setOnselect(Onselect onselect) {
        this.onselect = onselect;
    }

    public void PickImage(boolean multiple) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            pic_13_kMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
        } else {
            Intent videoIntent = new Intent();
            videoIntent.setAction(Intent.ACTION_GET_CONTENT);
            videoIntent.setType("image/*");
            videoIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiple);
            videoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pdf_launcher.launch(videoIntent);
        }


    }


    public void captureImage() {
        // remove "Image" from child
        File imagePath = new File(context.getFilesDir(),"my_images");

        fileo = new File(imagePath.getAbsolutePath() +"/"+UUID.randomUUID()+".jpg");

        try {
            if (!imagePath.exists()){
                imagePath.mkdirs();
            }
            if (fileo.exists()) {
                fileo.delete();
                fileo.createNewFile();

            } else {
                fileo.createNewFile();
            }


            Uri outputFileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", fileo);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            capture_image.launch(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    public void PickPDF() {
        if (true) {


            Intent videoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            videoIntent.addCategory(Intent.CATEGORY_OPENABLE);
            videoIntent.setType("application/pdf");
            videoIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            videoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            videoIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

            pdf_launcher.launch(videoIntent);
        }
    }
    public void Pickvideo() {
        Intent videoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        videoIntent.addCategory(Intent.CATEGORY_OPENABLE);
        videoIntent.setType("video/*");
        videoIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        videoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        videoIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        pdf_launcher.launch(videoIntent);
    }
    public void PickDoc() {
        if (true) {
            Intent videoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            videoIntent.addCategory(Intent.CATEGORY_OPENABLE);
            videoIntent.setType("*/*");
            videoIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            videoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            videoIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

            pdf_launcher.launch(videoIntent);
        }
    }
    private void readUri(Uri returnUri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (returnUri.getScheme().equalsIgnoreCase("file")) {
                    File file = new File(returnUri.getPath());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (file.exists()) {
                                if (onselect!=null) {
                                    onselect.onSelect(file.getName(), file.getAbsolutePath());
                                }
                            } else {
                                Toast.makeText(context, "existing File selection failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else if (returnUri.getScheme().equalsIgnoreCase("content")) {
                    //DocumentFile documentFile = DocumentFile.fromSingleUri(context, returnUri);

                    String name  =  getName(context.getContentResolver(),returnUri);

                    if (name != null ) {

                        String path = new File(context.getFilesDir(), "").getAbsolutePath(); // remove "Image" from child
                        File file = new File(path + "/" + name);


                        try {
                            if (file.exists()) {
                                file.delete();
                                file.createNewFile();

                            } else {
                                file.createNewFile();
                            }


                            InputStream inputStream = context.getContentResolver().openInputStream(returnUri);
                            FileOutputStream out = new FileOutputStream(file);
                            copyStream(inputStream, out);
                            out.close();

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (file.exists()) {
                                        if (onselect!=null) {
                                            onselect.onSelect(name, file.getAbsolutePath());
                                        }
                                    } else {
                                        Toast.makeText(context, "no file", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "File creation failed", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }


                }
            }
        }).start();


    }

    private void copyStream(InputStream in, OutputStream out) throws IOException {
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

    private String getName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

}
