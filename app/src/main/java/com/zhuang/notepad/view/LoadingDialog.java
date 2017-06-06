package com.zhuang.notepad.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhuang.notepad.R;

/**
 * Created by zhuang on 2016/11/16.
 */

public class LoadingDialog extends DialogFragment{

    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bac_radius_dialog_loading);
        View rootView = inflater.inflate(R.layout.toast_success, container, false);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        imageView = (ImageView)rootView.findViewById(R.id.imageView);
        textView = (TextView)rootView.findViewById(R.id.textView);
        return rootView;
    }

    /**
     * 延迟一个时间关闭
     */
    private void hideDelay(){
        new Handler().postDelayed(new Runnable() {
            public void run() {
               dismiss();
            }
        }, 1000); //2900 for release
    }

    public void setSuccess(){
        progressBar.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        textView.setText("提交成功");
    }

    public void setFailure(){
        progressBar.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_failure));
        textView.setText("提交失败");
        hideDelay();
    }
}
