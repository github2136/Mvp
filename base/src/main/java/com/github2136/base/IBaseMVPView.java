package com.github2136.base;

import android.support.annotation.StringRes;

/**
 *
 */
public interface IBaseMVPView {
    // 显示进度框
    void showProgressDialog();

    // 显示进度框
    void showProgressDialog(@StringRes int resId);

    // 显示进度框
    void showProgressDialog(String msg);

    // 关闭进度框
    void dismissDialog();
}
