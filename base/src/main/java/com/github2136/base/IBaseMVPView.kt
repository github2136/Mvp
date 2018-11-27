package com.github2136.base

import androidx.annotation.StringRes

/**
 * Created by yb on 2018/11/1.
 */
interface IBaseMVPView {
    // 显示进度框
    fun showProgressDialog()

    // 显示进度框
    fun showProgressDialog(@StringRes resId: Int)

    // 显示进度框
    fun showProgressDialog(msg: String)

    // 关闭进度框
    fun dismissDialog()
}