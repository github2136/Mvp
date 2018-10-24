package com.github2136.base;

import android.content.Context;

import com.github2136.util.JsonUtil;
import com.github2136.util.SPUtil;

/**
 * model基础类
 */
public abstract class BaseMVPModel {
    protected Context mContext;

    protected String mTag;
    protected SPUtil mSpUtil;
    protected JsonUtil mJsonUtil;

    public BaseMVPModel(Context context) {
        mContext = context;
        mTag = context.getClass().getSimpleName();
        initMode();
    }

    private void initMode() {
        mJsonUtil = JsonUtil.getInstance();
        mSpUtil = SPUtil.getInstance(mContext);
    }

    public abstract void cancelRequest();
}