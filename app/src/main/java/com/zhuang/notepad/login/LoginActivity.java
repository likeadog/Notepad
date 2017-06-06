package com.zhuang.notepad.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.zhuang.notepad.BaseActivity;
import com.zhuang.notepad.R;
import com.zhuang.notepad.databinding.ActivityLoginBinding;
import com.zhuang.notepad.model.User;
import com.zhuang.notepad.network.LoginReturnMsg;
import com.zhuang.notepad.network.LoginService;
import com.zhuang.notepad.network.RetrofitHelper;
import com.zhuang.notepad.notepad.NotepadListActivity;
import com.zhuang.notepad.user.SingleUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        init();
    }

    private void init() {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri("asset:///test1.gif")
                .setAutoPlayAnimations(true)
                .build();
        binding.simpleDraweeView.setController(controller);

        loadUserData();
    }

    /**
     * 用户登录
     */
    public void login(View view) {
        //输入验证
        if (TextUtils.isEmpty(binding.userNameTextView.getText())) {
            binding.userNameTextView.setError("请输入用户名");
            binding.userNameTextView.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(binding.passwordTextView.getText())) {
            binding.passwordTextView.setError("请输入密码");
            binding.passwordTextView.requestFocus();
            return;
        }
        String name = binding.userNameTextView.getText().toString();
        String password = binding.passwordTextView.getText().toString();

        //loading
        setLoading(true);

        //网络请求
        LoginService service = RetrofitHelper.createService(LoginService.class);
        Call<LoginReturnMsg> call = service.login(name, password);
        call.enqueue(new Callback<LoginReturnMsg>() {
            @Override
            public void onResponse(Call<LoginReturnMsg> call, Response<LoginReturnMsg> response) {
                LoginReturnMsg loginReturnMsg = response.body();
                switch (loginReturnMsg.getCode()) {
                    case 0:
                        saveUserData(loginReturnMsg);
                        gotHomePage();
                        return;
                    case -4:
                        binding.userNameTextView.setError("用户不存在");
                        binding.userNameTextView.requestFocus();
                        setLoading(false);
                        return;
                    case -3:
                        binding.passwordTextView.setError("密码错误");
                        binding.passwordTextView.requestFocus();
                        setLoading(false);
                }
            }

            @Override
            public void onFailure(Call<LoginReturnMsg> call, Throwable t) {
                Log.e(TAG, t.toString());
                Toast toast = Toast.makeText(LoginActivity.this,"网络错误",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                setLoading(false);
            }
        });
    }

    /**
     * 保存用户登录信息
     */
    private void saveUserData(LoginReturnMsg loginReturnMsg) {
        String token = loginReturnMsg.getToken();
        String refreshtoken = loginReturnMsg.getRefreshtoken();
        String avatar  = loginReturnMsg.getAvatar();
        String name = binding.userNameTextView.getText().toString();
        String password = binding.passwordTextView.getText().toString();
        SharedPreferences sp = getSharedPreferences("notepad", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name",name);
        editor.putString("password", password);
        editor.putString("token", token);
        editor.putString("refreshtoken", refreshtoken);
        editor.putString("avatar", avatar);
        editor.commit();
        RetrofitHelper.token = token;
        User user = SingleUser.getInstance();
        user.setName(name);
        user.setPassword(password);
        user.setAvatar(avatar);
    }

    /**
     * 如果有用户信息，把用户信息加载到相应的界面上
     */
    private void loadUserData() {
        SharedPreferences sp = getSharedPreferences("notepad", Context.MODE_PRIVATE);
        String name = sp.getString("name", null);
        String password = sp.getString("password", null);
        String token = sp.getString("token", null);
        if (name != null) {
            binding.userNameTextView.setText(name);
        }
        if (password != null) {
            binding.passwordTextView.setText(password);
        }
    }

    /**
     * 设置loading状态
     *
     * @param loading
     */
    private void setLoading(boolean loading) {
        if (loading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.loginBtn.setVisibility(View.INVISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.loginBtn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 用户注册页面跳转
     *
     * @param view
     */
    public void registered(View view) {
        Intent intent = new Intent(this, RegisteredActivity.class);
        startActivity(intent);
    }

    /**
     * 登陆成功，跳转到首页
     */
    public void gotHomePage() {
        Intent intent = new Intent(this, NotepadListActivity.class);
        startActivity(intent);
        finish();
    }
}
