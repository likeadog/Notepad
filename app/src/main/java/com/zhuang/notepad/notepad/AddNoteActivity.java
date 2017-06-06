package com.zhuang.notepad.notepad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.zhuang.notepad.BaseActivity;
import com.zhuang.notepad.R;
import com.zhuang.notepad.model.Note;
import com.zhuang.notepad.network.BaseReturnMsg;
import com.zhuang.notepad.network.NoteService;
import com.zhuang.notepad.network.RetrofitHelper;
import com.zhuang.notepad.network.ReturnDataList;
import com.zhuang.notepad.view.LoadingDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNoteActivity extends BaseActivity {

    private EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        content = (EditText)findViewById(R.id.content);
    }

    public void back(View view){
        finish();
    }

    /**
     * 提交
     *
     * @param view
     */
    public void addNote(View view) {

        final LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.show(getSupportFragmentManager(),"loading");

        String noteText = content.getText()+"";
        NoteService service = RetrofitHelper.createServiceWidthToken(NoteService.class);
        Call<BaseReturnMsg> call = service.addNote(noteText);
        call.enqueue(new Callback<BaseReturnMsg>() {
            @Override
            public void onResponse(Call<BaseReturnMsg> call, Response<BaseReturnMsg> response) {
                BaseReturnMsg baseReturnMsg = response.body();
                Log.e(TAG, baseReturnMsg.getDetail());
                //成功
                if (baseReturnMsg.getCode() == 0) {
                    loadingDialog.setSuccess();
                    setResult(RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<BaseReturnMsg> call, Throwable t) {
                Log.e(TAG, t.toString());
                loadingDialog.setFailure();
            }
        });
    }
}
