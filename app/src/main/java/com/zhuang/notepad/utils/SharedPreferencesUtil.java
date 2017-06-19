package com.zhuang.notepad.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhuang on 2017/6/13.
 */

public class SharedPreferencesUtil {

    public static String getToken(Context context){
        SharedPreferences sp = context.getSharedPreferences("notepad", Context.MODE_PRIVATE);
        String token = sp.getString("token", null);
        return token;
    }

    public static String getRefreshToken(Context context){
        SharedPreferences sp = context.getSharedPreferences("notepad", Context.MODE_PRIVATE);
        String refreshtoken = sp.getString("refreshtoken", null);
        return refreshtoken;
    }

    public static void setToken(Context context,String token){
        SharedPreferences sp = context.getSharedPreferences("notepad", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", token);
        editor.commit();
    }

    public static void setRefreshToken(Context context,String refreshtoken){
        SharedPreferences sp = context.getSharedPreferences("notepad", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("refreshtoken", refreshtoken);
        editor.commit();
    }

    public static void setAllData(Context context,String name,
                                  String password,String avatar,String token,String refreshtoken){
        SharedPreferences sp = context.getSharedPreferences("notepad", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name",name);
        editor.putString("password", password);
        editor.putString("token", token);
        editor.putString("refreshtoken", refreshtoken);
        editor.putString("avatar", avatar);
        editor.commit();
    }

    public static void setDownloadId(Context context,long downloadId){
        SharedPreferences sp = context.getSharedPreferences("notepad", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("downloadId", downloadId);
        editor.commit();
    }

    public static long getDownloadId(Context context){
        SharedPreferences sp = context.getSharedPreferences("notepad", Context.MODE_PRIVATE);
        long downloadId = sp.getLong("downloadId", -1L);
        return downloadId;
    }
}
