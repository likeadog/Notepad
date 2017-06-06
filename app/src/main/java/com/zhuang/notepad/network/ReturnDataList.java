package com.zhuang.notepad.network;

import java.util.List;

/**
 * Created by zhuang on 2017/5/22.
 */

public class ReturnDataList<T> {
    private int code;
    private List<T> data;

    public int getCode() {
        return code;
    }

    public List<T> getData() {
        return data;
    }
}
