package com.zhuang.notepad.network;

import android.util.Log;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zhuang on 2016/12/30.
 */

public class RetrofitHelper {

    public static final String API_BASE_URL = "https://www.liguanjian.com/";
    public static String token = null;

    private static OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    public static <T> T createService(Class<T> serviceClass) {
        return createService(serviceClass, null);
    }

    public static <T> T createServiceWidthToken(Class<T> serviceClass) {
        return createService(serviceClass, token);
    }

    public static <T> T createService(Class<T> serviceClass, final String authToken) {
        if (authToken != null) {
            httpBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    HttpUrl httpUrl = request.url().newBuilder()
                            .setQueryParameter("token", authToken)
                            .build();
                    request = request.newBuilder().url(httpUrl).build();
                    return chain.proceed(request);
                }
            });
        }
        Retrofit retrofit = builder.client(httpBuilder.build()).build();
        return retrofit.create(serviceClass);
    }
}
