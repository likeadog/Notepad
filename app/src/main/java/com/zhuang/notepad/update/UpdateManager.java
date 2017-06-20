package com.zhuang.notepad.update;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.zhuang.notepad.BuildConfig;
import com.zhuang.notepad.utils.SharedPreferencesUtil;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

import static android.app.DownloadManager.Request.NETWORK_WIFI;
import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by zhuang on 2017/6/19.
 */

public class UpdateManager {

    private Context context;
    private Context applicationContext;
    private DownloadManager downloadManager;
    private String uri = "https://github.com/likeadog/Notepad/raw/master/app/update/app-release.apk";

    public UpdateManager(Context context) {
        this.context = context;
        applicationContext = context.getApplicationContext();
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
            String versionName = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0).versionName;
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
        long downloadId = SharedPreferencesUtil.getDownloadId(applicationContext);
        if (downloadId != -1) {
            PackageInfo loadInfo = getApkInfo(downloadId);
            String localPackage = applicationContext.getPackageName();
            if (loadInfo.packageName.equals(localPackage)) {
                float loadVersion = Float.parseFloat(loadInfo.versionName);
                if (loadVersion == remoteVersion) {
                    //不需要下载，直接安装
                    Log.e("zhuang", "已下载过更新的apk,版本为"+loadVersion+"直接安装");
                    showDialog(downloadId);
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

    private void showDialog(final long downloadId){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(" ").setMessage("app有更新，是否更新？")
                .setNegativeButton("取消",null)
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        installApk(downloadId);
                    }
                });
        builder.create().show();
    }

    /**
     * 安装APK
     */
    public void installApk(long downloadId) {
        Uri contentUri;
        String path = getApkPath(downloadId);
        File apkFile = new File(path);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        //此处因为上下文是Context，所以要加此Flag，不然会报错
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            contentUri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider",
                    apkFile);
        }else{
            contentUri = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public void startDownload() {
        long downloadId = SharedPreferencesUtil.getDownloadId(applicationContext);
        //防止下载重复
        if (downloadId != -1) {
            downloadManager.remove(downloadId);
        }
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(uri));
        req.setTitle("notepad更新");
        req.setAllowedNetworkTypes(NETWORK_WIFI);
        //移动网络是否允许下载
        req.setAllowedOverRoaming(false);
        /**设置通知栏是否可见*/
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        //file:///storage/emulated/0/Android/data/your-package/files/Download/update.apk
        req.setDestinationInExternalFilesDir(applicationContext, Environment.DIRECTORY_DOWNLOADS, "notepad.apk");
        long newDownloadId = downloadManager.enqueue(req);
        //把DownloadId保存到本地
        SharedPreferencesUtil.setDownloadId(applicationContext, newDownloadId);
    }

    /**
     * 获取已下载的apk程序信息
     */
    private PackageInfo getApkInfo(long downloadId) {
        String path = getApkPath(downloadId);
        PackageManager pm = applicationContext.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        return info;
    }

    String getApkPath(long downloadId){
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor cur = downloadManager.query(query);
        if (cur != null) {
            if (cur.moveToFirst()) {
                String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                if (!TextUtils.isEmpty(uriString)) {
                    String path = Uri.parse(uriString).getPath();
                    return path;
                }
            }
            cur.close();
        }
        return null;
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
