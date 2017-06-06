package com.zhuang.notepad.user;

import com.zhuang.notepad.model.User;

/**
 * Created by zhuang on 2017/5/31.
 */

public class SingleUser{
    private static final User ourInstance = new User();
    public static User getInstance() {
        return ourInstance;
    }
}
