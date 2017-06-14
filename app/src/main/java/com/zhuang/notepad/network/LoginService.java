package com.zhuang.notepad.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by zhuang on 2017/5/22.
 */

public interface LoginService {

    //验证token合法性
    @GET("ser/heartbeat")
    Call<BaseReturnMsg> validateToken();

    @GET("ser/login")
    Call<LoginReturnMsg> login(@Query("name") String name, @Query("password") String password);

    @GET("ser/register")
    Call<BaseReturnMsg> register(@Query("name") String name, @Query("password") String password);

    @GET("ser/logout")
    Call<BaseReturnMsg> logout();

    //刷新token
    @GET("ser/refresh")
    Call<TokenMsg> refreshToken(@Query("refreshtoken") String refreshtoken);
}
