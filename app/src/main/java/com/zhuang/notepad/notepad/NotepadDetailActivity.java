package com.zhuang.notepad.notepad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.zhuang.notepad.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotepadDetailActivity extends AppCompatActivity {

    private WebView webView;
    private String note;
    private String timeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad_detail);

        note = getIntent().getStringExtra("note");
        timeStr = getIntent().getStringExtra("time");
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setDefaultTextEncodingName("utf-8");// 避免中文乱码

        crateTitle();
    }

    private void crateTitle() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            Date time = sdf.parse(timeStr);
            String time2 = sdf1.format(time);
            String[] arrs = time2.split(" ");
            String dateStr = arrs[0];
            String timeStr = arrs[1];
            String title = "<div  style='margin-top:10px;'>" +
                    "<font size='5'>" + dateStr + "&nbsp;&nbsp;&nbsp;&nbsp;</font>"
                    + "<font color='#7B7B7B'>" + timeStr + "</font></div>";
            String content = "<div style='font-size:18px;margin-top:20px;'>" + note + "</div>";
            String html = "<div style='padding:10px;line-height:30px'>"+title + content+"</div>";
            webView.loadData(html, "text/html;charset=UTF-8", null);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void back(View view){
        finish();
    }
}
