package com.zhuang.notepad.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by zhuang on 2017/6/16.
 */

public interface FileService {

    @GET
    Call<ResponseBody> downloadFileWithFixedUrl(@Url String fileUrl);
}
