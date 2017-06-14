package com.zhuang.notepad;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zhuang.notepad.network.RetrofitHelper;

/**
 * Created by zhuang on 2017/5/25.
 */

public class ZApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        RetrofitHelper.initialize(this);
    }
}
