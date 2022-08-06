package com.nil.treeclause;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit2.Call;
import retrofit2.http.Multipart;

public interface UploadAPIs {
    @FormUrlEncoded
    @POST("/login")
    public void Login(
            @Field("email") String name,
            @Field("password") String password,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/get_ward")
    public void getward(
            @Field("user_id") String name,
            Callback<Response> callback);

    @FormUrlEncoded
    @POST("/get_tree_type")
    public void gettree(
            @Field("tree_type") String name,
            Callback<Response> callback);

    @Multipart
    @POST("/images/upload")
    Call<ServerResponse> uploadimage(
            @Field("user_id") RequestBody user_id,
            @Field("ward_no") RequestBody ward_no,
            @Field("serial_no") RequestBody serial_no,
            @Field("tree_type") RequestBody tree_type,
            @Field("comman_name") RequestBody comman_name,
            @Field("scientific_name") RequestBody scientific_name,
            @Field("height") RequestBody height,
            @Field("girth") RequestBody girth,
            @Field("condition") RequestBody condition,
            @Field("ownership") RequestBody ownership,
            @Field("owner_name") RequestBody owner_name,
            @Field("address") RequestBody address,
            @Field("heritage") RequestBody heritage,
            @Field("rare") RequestBody rare,
            @Field("lat") RequestBody lat,
            @Field("lng") RequestBody lng,
            @Field("image") MultipartBody.Part image);


}
