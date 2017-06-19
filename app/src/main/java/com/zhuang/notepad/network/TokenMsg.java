package com.zhuang.notepad.network;

/**
 * Created by zhuang on 2017/6/9.
 */

public class TokenMsg {
    private int code;
    private String token;
    private String refreshtoken;

    public int getCode() {
        return code;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshtoken() {
        return refreshtoken;
    }
}
