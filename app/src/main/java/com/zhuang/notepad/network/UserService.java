package com.zhuang.notepad.network;

import com.zhuang.notepad.model.Note;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by zhuang on 2017/6/1.
 */

public interface UserService {

    @POST("avatar")
    Call<Map> saveAvatar(@Body MultipartBody image);

    @GET("girl.json")
    Call<List<String>> getImageUrlList();

    @GET("girl")
    Call<List<String>> refreshImageUrlList();
}
