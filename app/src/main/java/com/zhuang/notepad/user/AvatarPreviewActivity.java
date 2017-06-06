package com.zhuang.notepad.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zhuang.notepad.BaseActivity;
import com.zhuang.notepad.R;
import com.zhuang.notepad.network.RetrofitHelper;
import com.zhuang.notepad.network.UserService;
import com.zhuang.notepad.utils.PictureUtil;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvatarPreviewActivity extends BaseActivity {

    private SimpleDraweeView imageView;
    private String path;//图片path
    private String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);
        init();
    }

    private void init() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        path = getIntent().getStringExtra("path");
        uri = getIntent().getStringExtra("uri");
        imageView = (SimpleDraweeView) findViewById(R.id.imageView);
        if (uri != null) {
            imageView.setImageURI(uri);
        } else {
            imageView.setImageURI(Uri.fromFile(new File(path)));
        }
    }

    public void submitPhoto(View view) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (uri != null) {
            path = PictureUtil.getRealPathFromURI(getApplicationContext(), Uri.parse(uri));
        }
        final String compressFilePath = PictureUtil.compressBitmap(getApplicationContext(), path);
        File file = new File(compressFilePath);
        builder.addFormDataPart("fileUp", "android.jpg", RequestBody.create(MediaType.parse("image/jpeg"), file));
        MultipartBody requestBody = builder.build();

        UserService userService = RetrofitHelper.createServiceWidthToken(UserService.class);
        Call<Map> call = userService.saveAvatar(requestBody);
        call.enqueue(new Callback<Map>() {
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {
                PictureUtil.deleteTempFile(compressFilePath);
                Map map = response.body();
                if (map.get("avatar") != null) {
                    SingleUser.getInstance().setAvatar(map.get("avatar").toString());
                    SharedPreferences sp = getSharedPreferences("notepad", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("avatar", map.get("avatar").toString());
                    editor.commit();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Map> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
        //结束该activity，返回
        finish();
    }
}
