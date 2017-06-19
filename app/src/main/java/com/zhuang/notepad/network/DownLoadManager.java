package com.zhuang.notepad.network;

import android.content.Context;
import android.util.Log;

import com.zhuang.notepad.utils.PictureUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

/**
 * Created by zhuang on 2017/6/16.
 */

public class DownLoadManager {
    private static final String TAG = "DownLoadManager";
    private static String APK_CONTENTTYPE = "application/vnd.android.package-archive";
    private static String PNG_CONTENTTYPE = "image/png";
    private static String JPG_CONTENTTYPE = "image/jpeg";
    private static String fileSuffix = "";

    public static boolean writeResponseBodyToDisk(Context context, ResponseBody body) {
        Log.e(TAG, "contentType:>>>>" + body.contentType().toString());
        try {
            String type = body.contentType().toString();
            String path;
            if (type.equals(APK_CONTENTTYPE)) {
                fileSuffix = ".apk";
                String fileName = System.currentTimeMillis() + fileSuffix;
                path = context.getExternalFilesDir(null) + File.separator + fileName;
            } else {
                if (type.equals(PNG_CONTENTTYPE)) {
                    fileSuffix = ".png";
                } else if (type.equals(JPG_CONTENTTYPE)) {
                    fileSuffix = ".jpg";
                }
                String fileName = System.currentTimeMillis() + fileSuffix;
                path = PictureUtil.getAlbumDir() + File.separator + fileName;
            }
            File futureStudioIconFile = new File(path);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                Log.e(TAG,fileSize+"");
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                Log.e(TAG, path);
                //图片要加载到图库
                if (!type.equals(APK_CONTENTTYPE)) {
                    PictureUtil.galleryAddPic(context, path);
                    Log.e(TAG,futureStudioIconFile.getAbsolutePath());
                    Log.e(TAG,futureStudioIconFile.getPath());
                    Log.e(TAG,futureStudioIconFile.getName());
                    Log.e(TAG,futureStudioIconFile.length()+"");
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}