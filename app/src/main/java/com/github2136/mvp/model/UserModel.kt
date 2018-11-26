package com.github2136.mvp.model

import android.app.Application
import com.github2136.base.BaseMVPModel
import okhttp3.Callback

/**
 * Created by yb on 2018/11/2.
 */
class UserModel(app: Application, tag: String) : BaseMVPModel(app, tag) {

    fun get(callback: Callback) {
        httpGet("http://www.baidu.com", "", null, callback)
    }
}