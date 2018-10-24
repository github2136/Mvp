package com.github2136.mvp.model;

import android.content.Context;

import com.github2136.base.BaseMVPModel;
import com.github2136.base.HttpCallback;

import okhttp3.Callback;

/**
 * Created by yb on 2018/10/23.
 **/
public class UserModel extends BaseMVPModel {

    public UserModel(Context context, String tag) {
        super(context, tag);
    }

    public void get(HttpCallback callback) {
        httpGet("http://www.baidu.com", "", null, callback);
    }
}
