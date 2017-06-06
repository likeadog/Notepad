package com.zhuang.notepad.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.zhuang.notepad.BR;

/**
 * Created by zhuang on 2017/5/22.
 */

public class User extends BaseObservable{
    private String id;
    private String name;
    private String password;
    private String avatar;//头像url

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
        notifyPropertyChanged(BR.avatar);
    }
}
