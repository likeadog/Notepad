package com.zhuang.notepad.user;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.zhuang.notepad.BaseActivity;
import com.zhuang.notepad.R;
import com.zhuang.notepad.network.DownLoadManager;
import com.zhuang.notepad.network.FileService;
import com.zhuang.notepad.network.RetrofitHelper;

import me.relex.photodraweeview.PhotoDraweeView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PhotoCheckActivity extends BaseActivity {

    PhotoDraweeView photoDraweeView;
    String url;
    private static final int REQUEST_PERMISSION = 12;//权限请求

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_check);

        url = getIntent().getStringExtra("url");
        photoDraweeView = (PhotoDraweeView)findViewById(R.id.photoDraweeView);
        photoDraweeView.setPhotoUri(Uri.parse(url));
    }

    private void savePhoto() throws Exception {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(RetrofitHelper.API_BASE_URL).build();
        FileService service = retrofit.create(FileService.class);
        Call<ResponseBody> call = service.downloadFileWithFixedUrl(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                DownLoadManager.writeResponseBodyToDisk(getApplicationContext(),response.body());
                Toast.makeText(PhotoCheckActivity.this,"下载成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    public void savePhoto(View view){
        requestPermission();
    }

    /**
     * 权限请求
     */
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        } else {
            try {
                savePhoto();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    savePhoto();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
