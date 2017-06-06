package com.zhuang.notepad.login;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.zhuang.notepad.BaseActivity;
import com.zhuang.notepad.R;
import com.zhuang.notepad.databinding.ActivityRegisteredBinding;
import com.zhuang.notepad.network.BaseReturnMsg;
import com.zhuang.notepad.network.LoginService;
import com.zhuang.notepad.network.RetrofitHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisteredActivity extends BaseActivity {

    private ActivityRegisteredBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registered);
    }

    /**
     * 注册请求
     *
     * @param view
     */
    public void registered(View view) {
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

        LoginService service = RetrofitHelper.createService(LoginService.class);
        Call<BaseReturnMsg> call = service.register(name, password);
        call.enqueue(new Callback<BaseReturnMsg>() {
            @Override
            public void onResponse(Call<BaseReturnMsg> call, Response<BaseReturnMsg> response) {
                BaseReturnMsg baseReturnMsg = response.body();
                if (baseReturnMsg.getCode() == 0) {
                    registerSuccess();
                }else{
                    registerFailure();
                }
            }

            @Override
            public void onFailure(Call<BaseReturnMsg> call, Throwable t) {

            }
        });
    }

    private void registerSuccess(){
        finish();
        Toast toast = Toast.makeText(this,"注册成功",Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    private void registerFailure(){
        binding.userNameTextView.setError("用户已被注册");
        binding.userNameTextView.requestFocus();
        setLoading(false);
    }

    /**
     * 设置loading状态
     *
     * @param loading
     */
    private void setLoading(boolean loading) {
        if (loading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.register.setVisibility(View.INVISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.register.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 返回登录页
     */
    public void backToLogin(View view) {
        finish();
    }
}
