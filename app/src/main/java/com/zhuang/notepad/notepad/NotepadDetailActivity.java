package com.zhuang.notepad.notepad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuang.notepad.BaseActivity;
import com.zhuang.notepad.R;
import com.zhuang.notepad.network.BaseReturnMsg;
import com.zhuang.notepad.network.NoteService;
import com.zhuang.notepad.network.RetrofitHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotepadDetailActivity extends BaseActivity {

    private TextView title;
    private TextView subTitle;
    private EditText content;
    private ProgressBar progressBar;
    private TextView updateBtn;
    private String note;
    private String timeStr;
    private long noteId;
    private int position;
    private boolean editable = false;//编辑状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad_detail);

        note = getIntent().getStringExtra("note");
        timeStr = getIntent().getStringExtra("time");
        noteId = getIntent().getLongExtra("noteId",0);
        position = getIntent().getIntExtra("position",0);
        title = (TextView) findViewById(R.id.title);
        subTitle = (TextView) findViewById(R.id.subTitle);
        content = (EditText) findViewById(R.id.content);
        updateBtn = (TextView) findViewById(R.id.updateBtn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        crateDetail();
    }

    /**
     * 编辑内容
     * @param view
     */
    public void updateNote(View view){
        if(editable){
            updateNote(content.getText()+"",noteId);
        }else{
            editable = true;
            content.setFocusableInTouchMode(editable);
            content.setFocusable(editable);
            updateBtn.setText("保存");
            //弹出软盘
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0,InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * 修改日记
     */
    public void updateNote(final String contentStr,long id) {
        updateBtn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        NoteService service = RetrofitHelper.createServiceWidthToken(NoteService.class);
        Call<BaseReturnMsg> call = service.updateNote(contentStr,id);
        call.enqueue(new Callback<BaseReturnMsg>() {
            @Override
            public void onResponse(Call<BaseReturnMsg> call, Response<BaseReturnMsg> response) {
                if(response.body().getCode() == 0){
                    Toast.makeText(NotepadDetailActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("position",position);
                    intent.putExtra("content",contentStr);
                    setResult(RESULT_OK,intent);
                    finish();
                }else{
                    updateBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    updateBtn.setText("保存");
                    Toast.makeText(NotepadDetailActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseReturnMsg> call, Throwable t) {
                Log.e(TAG, t.toString());
                updateBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                updateBtn.setText("保存");
                Toast.makeText(NotepadDetailActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void crateDetail() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            Date time = sdf.parse(timeStr);
            String time2 = sdf1.format(time);
            String[] arrs = time2.split(" ");
            String titleStr = arrs[0];
            String subTitleStr = arrs[1];
            title.setText(titleStr);
            subTitle.setText(subTitleStr);
            content.setText(note);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

//  ┏┓　　　┏┓
//┏┛┻━━━┛┻┓
//┃　　　　　　　┃
//┃　　　━　　　┃
//┃　┳┛　┗┳　┃
//┃　　　　　　　┃
//┃　　　┻　　　┃
//┃　　　　　　　┃
//┗━┓　　　┏━┛
//    ┃　　　┃   神兽保佑
//    ┃　　　┃   代码无BUG！
//    ┃　　　┗━━━┓
//    ┃　　　　　　　┣┓
//    ┃　　　　　　　┏┛
//    ┗┓┓┏━┳┓┏┛
//      ┃┫┫　┃┫┫
//      ┗┻┛　