package com.easy.kotlintest.Networking.Helper.multipart;

import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class Params {

    public static List<MultipartBody.Part> createPartList(Collection<PART> list) {
        List<MultipartBody.Part> partList = new ArrayList<>();
        for (PART part :
                list) {
            partList.add(prepareFilePart(part.getKey(), part.getFile()));
        }
        return partList;
    }


    public static MultipartBody.Part createMultiPart(PART part) {
        return prepareFilePart(part.getKey(), part.getFile());
    }



    @NonNull
    private static MultipartBody.Part prepareFilePart(String partName, File file) {
        // create RequestBody instance from file
        System.out.println(file.getAbsolutePath());
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getMimeType(file)),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    @NonNull
    private static String getMimeType(@NonNull File file) {
        String type = null;
        final String url = file.toString();
        final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        if (type == null) {
            type = "*/*";
        }
        return type;
    }


}