package com.github2136.mvp.model;

import android.content.Context;

import com.github2136.base.BaseMVPModel;

import okhttp3.Callback;

/**
 *
  **/
public class UserModel extends BaseMVPModel {

    public UserModel(Context context, String tag) {
        super(context, tag);
    }

    public void get(Callback callback) {
        httpGet("http://www.asfrvre.com", "", null, callback);
    }
}
