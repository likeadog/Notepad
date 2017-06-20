package com.zhuang.notepad.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.zhuang.notepad.BaseActivity;
import com.zhuang.notepad.R;
import com.zhuang.notepad.model.User;
import com.zhuang.notepad.network.BaseReturnMsg;
import com.zhuang.notepad.network.LoginService;
import com.zhuang.notepad.network.RetrofitHelper;
import com.zhuang.notepad.notepad.NotepadListActivity;
import com.zhuang.notepad.user.SingleUser;
import com.zhuang.notepad.utils.SharedPreferencesUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        validateToken();
    }

    /**
     * 跳转到登录页
     */
    private void gotoLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 跳转到首页
     */
    private void gotoHome() {
        Intent intent = new Intent(SplashActivity.this, NotepadListActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 验证token合法性
     */
    private void validateToken() {
        //取出token
        final String token = SharedPreferencesUtil.getToken(this);
        if (token == null) {
            Log.e(TAG, "token为空");
            gotoLogin();
            return;
        }
        LoginService service = RetrofitHelper.createServiceWidthToken(LoginService.class);
        Call<BaseReturnMsg> call = service.validateToken();
        call.enqueue(new Callback<BaseReturnMsg>() {
            @Override
            public void onResponse(Call<BaseReturnMsg> call, Response<BaseReturnMsg> response) {
                BaseReturnMsg baseReturnMsg = response.body();
                if (baseReturnMsg.getCode() == 0) {
                    setUserData();
                    gotoHome();
                } else {
                    gotoLogin();
                }
            }

            @Override
            public void onFailure(Call<BaseReturnMsg> call, Throwable t) {
                Log.e(TAG, t.toString());
                gotoLogin();
            }
        });
    }

    /**
     * 设置用户登录信息
     */
    private void setUserData() {
        SharedPreferences sp = getSharedPreferences("notepad", Context.MODE_PRIVATE);
        String name = sp.getString("name", null);
        String password = sp.getString("password", null);
        String avatar = sp.getString("avatar", null);
        User user = SingleUser.getInstance();
        user.setName(name);
        user.setPassword(password);
        user.setAvatar(avatar);
    }

}
