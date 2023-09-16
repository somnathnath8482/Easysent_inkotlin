package com.easy.kotlintest.Networking.Helper.multipart;

import java.util.HashMap;

import okhttp3.RequestBody;

public class Utils {
    public static HashMap<String, RequestBody> getParam(HashMap<String, Object> param) {
        HashMap<String, RequestBody> tempParam = new HashMap<>();
        for (String key : param.keySet()) {
            tempParam.put(key, toRequestBody(param.get(key).toString()));
        }

        return tempParam;
    }


    public static RequestBody toRequestBody(String value) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, value);
    }
}
