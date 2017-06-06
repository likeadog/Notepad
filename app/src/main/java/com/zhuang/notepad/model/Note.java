package com.zhuang.notepad.model;

/**
 * Created by zhuang on 2017/5/22.
 */

public class Note {
    private long id;
    private String note;//日记内容
    private String time;

    public long getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public String getTime() {
        return time;
    }
}
