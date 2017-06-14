package com.zhuang.notepad.user;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.zhuang.notepad.BaseActivity;
import com.zhuang.notepad.BuildConfig;
import com.zhuang.notepad.R;
import com.zhuang.notepad.databinding.ActivityUserBinding;
import com.zhuang.notepad.utils.PictureUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserActivity extends BaseActivity {

    private ActivityUserBinding binding;
    private AlertDialog alertDialog;
    private static final int REQUEST_TAKE_PHOTO = 10;//拍照标志
    private static final int REQUEST_TAKE_GALLERY = 11;//相册标志
    private static final int REQUEST_PERMISSION = 12;//权限请求
    private static final int REQUEST_PREVIEW = 13;//照片预览
    private String mCurrentPhotoPath;//存放照片地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user);
    }

    /**
     * 拍照或相册选择框
     *
     * @param view
     */
    public void showCheckPictureDialog(View view) {
        if (alertDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        requestPermission();
                    } else {
                        getImageFromCamera();
                    }
                }
            }).setNegativeButton("取消", null);
            alertDialog = builder.create();
        }
        alertDialog.show();
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
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 跳转到拍照界面
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File file = PictureUtil.createImageFile();
                Uri photoURI;
                if (Build.VERSION.SDK_INT > 23) {
                    photoURI = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            file);
                } else {
                    photoURI = Uri.fromFile(file);
                }
                mCurrentPhotoPath = file.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            } catch (IOException e) {
                return;
            }
        }
    }

    /**
     * 跳转到相册界面
     */
    protected void getImageFromCamera() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_TAKE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                PictureUtil.galleryAddPic(this, mCurrentPhotoPath);
                gotoPreView();
            } else {
                PictureUtil.deleteTempFile(mCurrentPhotoPath);
            }
        } else if (requestCode == REQUEST_TAKE_GALLERY && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            mCurrentPhotoPath = PictureUtil.getRealPathFromURI(this, uri);
            gotoPreView();
        } else if (requestCode == REQUEST_PREVIEW && resultCode == RESULT_OK) {
            File file = new File(mCurrentPhotoPath);
            binding.imageView.setImageURI(Uri.fromFile(file));
        }
    }

    /**
     * 图片预览
     */
    private void gotoPreView() {
        Intent intent = new Intent(this, AvatarPreviewActivity.class);
        intent.putExtra("mCurrentPhotoPath", mCurrentPhotoPath);
        startActivityForResult(intent, REQUEST_PREVIEW);
    }
}