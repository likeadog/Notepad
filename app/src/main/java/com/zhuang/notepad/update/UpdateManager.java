package com.zhuang.notepad.update;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.zhuang.notepad.utils.SharedPreferencesUtil;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

import static android.app.DownloadManager.Request.NETWORK_WIFI;
import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by zhuang on 2017/6/19.
 */

public class UpdateManager {

    private Context context;
    private DownloadManager downloadManager;
    private String uri = "https://github.com/likeadog/Notepad/raw/master/app/update/app-debug.apk";

    public UpdateManager(Context context) {
        this.context = context;
        downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
    }

    public void update() {
        loadApkVersion();
    }

    /**
     * 已安装的apk版本与服务器比较
     *
     * @param remoteVersion
     */
    void compareCurrentVersion(float remoteVersion) {
        try {
            String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            if (Float.parseFloat(versionName) < remoteVersion) {
                compareLoadVersion(remoteVersion);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 已下载的apk版本与与远程的apk版本比较
     */
    void compareLoadVersion(float remoteVersion) {
        long downloadId = SharedPreferencesUtil.getDownloadId(context);
        Log.e("zhuang","has "+downloadId+"");
        Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
        if (uri != null) {
            PackageInfo loadInfo = getApkInfo(uri.getPath());
            String localPackage = context.getPackageName();
            if (loadInfo.packageName.equals(localPackage)) {
                float loadVersion = Float.parseFloat(loadInfo.versionName);
                if (loadVersion == remoteVersion) {
                    //不需要下载，直接安装
                    Log.e("zhuang", "已下载过更新的apk,版本为"+loadVersion+"直接安装");
                } else if (loadVersion < remoteVersion) {
                    //需要下载
                    Log.e("zhuang", "下载过的apk版本较老,已下载版本为"+loadVersion+"服务器版本为"+remoteVersion+"需要下载");
                    startDownload();
                }
            }
        } else {
            Log.e("zhuang", "没有下载过更新的apk,需要下载");
            startDownload();
        }
    }

    public void startDownload() {
        long downloadId = SharedPreferencesUtil.getDownloadId(context);
        //防止下载重复
        if (downloadId != -1) {
            downloadManager.remove(downloadId);
        }
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(uri));
        req.setAllowedNetworkTypes(NETWORK_WIFI);
        //移动网络是否允许下载
        req.setAllowedOverRoaming(false);
        //file:///storage/emulated/0/Android/data/your-package/files/Download/update.apk
        req.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "update.apk");
        //req.setMimeType("application/vnd.android.package-archive");
        long newDownloadId = downloadManager.enqueue(req);
        Log.e("zhuang","now "+newDownloadId+"");
        //把DownloadId保存到本地
        SharedPreferencesUtil.setDownloadId(context, newDownloadId);
    }

    /**
     * 获取已下载的apk程序信息
     */
    private PackageInfo getApkInfo(String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        return info;
    }

    void loadApkVersion() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com/likeadog/Notepad/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        ApkService apkService = retrofit.create(ApkService.class);
        Call<ApkVersion> call = apkService.getApkVersion();
        call.enqueue(new Callback<ApkVersion>() {
            @Override
            public void onResponse(Call<ApkVersion> call, Response<ApkVersion> response) {
                ApkVersion apkVersion = response.body();
                compareCurrentVersion(apkVersion.version);
            }

            @Override
            public void onFailure(Call<ApkVersion> call, Throwable t) {
                Log.e("zhuang", t.toString());
            }
        });
    }

    interface ApkService {
        @GET("master/app/update/apkVersion.json")
        Call<ApkVersion> getApkVersion();
    }

    class ApkVersion {
        float version;
    }

}
