package com.zhuang.notepad;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.zhuang.notepad.network.RetrofitHelper;
import com.zhuang.notepad.update.TinkerManager;
import com.zhuang.notepad.update.TinkerApplicationContext;

import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by zhuang on 2017/6/15.
 */

@SuppressWarnings("unused")
@DefaultLifeCycle(application = "com.zhuang.notepad.ZApplication", flags = ShareConstants.TINKER_ENABLE_ALL, loadVerifyFlag = false)
public class ZApplicationLike extends DefaultApplicationLike {

    private static final String TAG = "Tinker.ZApplicationLike";
    //小米推送
    private static final String APP_ID = "2882303761517591485";
    private static final String APP_KEY = "5791759143485";

    public ZApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag,
                            long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(getApplication());
        RetrofitHelper.initialize(getApplication());
        initMiPush();
    }

    private void initMiPush() {
        // 注册push服务，注册成功后会向DemoMessageReceiver发送广播
        // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
        if (shouldInit()) {
            MiPushClient.registerPush(getApplication(), APP_ID, APP_KEY);
        }
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getApplication().getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * install multiDex before install tinker
     * so we don't need to put the tinker lib classes in the main dex
     *
     * @param base
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        //you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        TinkerApplicationContext.application = getApplication();
        TinkerApplicationContext.context = getApplication();
        TinkerManager.setTinkerApplicationLike(this);

        TinkerManager.initFastCrashProtect();
        //should set before tinker is installed
        TinkerManager.setUpgradeRetryEnable(true);
        //installTinker after load multiDex
        //or you can put com.tencent.tinker.** to main dex
        TinkerManager.installTinker(this);
        Tinker tinker = Tinker.with(getApplication());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }

}