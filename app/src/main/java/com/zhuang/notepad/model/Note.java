package com.zhuang.notepad.model;

import android.graphics.Color;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhuang on 2017/5/22.
 */

public class Note {
    private long id;
    private String note;//日记内容
    private String time;
    private String color;

    public long getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTime() {
        return time;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getColorInt1() {
        return Color.parseColor("#"+color);
    }

    public int getColorInt2() {
        return Color.parseColor("#BB"+color);
    }

    public int getColorInt3() {
        return Color.parseColor("#99"+color);
    }

    public int getColorInt4() {
        return Color.parseColor("#55"+color);
    }

    public String[] getTitle(){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            Date time1 = sdf.parse(time);
            String time2 = sdf1.format(time1);
            String[] arrays = time2.split(" ");
            String dateStr = arrays[0];
            String timeStr = arrays[1];
            return new String[]{dateStr,timeStr};
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
