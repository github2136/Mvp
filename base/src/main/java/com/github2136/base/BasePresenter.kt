package com.github2136.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.github2136.util.JsonUtil
import com.github2136.util.SPUtil

/**
 * Created by yb on 2018/11/1.
 */
abstract class BasePresenter(app: Application) : AndroidViewModel(app) {
    protected lateinit var mTag: String
    val loadingStr = "请稍后……"
    val failedStr = "无法连接服务器"
    var mJsonUtil: JsonUtil = JsonUtil.instance
    var mSpUtil: SPUtil = SPUtil.getInstance(app)
    //显示dialog
    val ldDialog = MutableLiveData<String>()

    open fun init(tag: String) {
        mTag = tag
    }

    //取消请求
    abstract fun cancelRequest()
}