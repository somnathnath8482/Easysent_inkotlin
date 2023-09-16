package com.easy.kotlintest.Networking.Helper;



import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

public interface ApiInterface {


    @FormUrlEncoded
    @POST()
    Call<String> PostRequestFormData(@Url String url, @Header("Authorization") String header, @FieldMap HashMap<String, Object> param);

    @Multipart
    @POST()
    Call<String> PostRequestMultipart(@Url String url,@Header("Authorization") String header, @Part MultipartBody.Part file, @PartMap HashMap<String, RequestBody> param);

    @GET()
    Call<String> GETRequest(@Url String url,@Header("Authorization") String Auth);


    @POST()
    @Headers({"Content-Type: application/json"})
    Call<String> PostReqJson(@Url String url,@Header("Authorization") String header, @Body String onj);



}
