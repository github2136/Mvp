package com.github2136.mvp.model

import android.app.Application
import androidx.collection.ArrayMap
import com.github2136.base.BaseMVPModel
import okhttp3.Callback
import okhttp3.Response

/**
 * Created by yb on 2018/11/28.
 */
class DataModel(app: Application, tag: String) : BaseMVPModel(app, tag) {
    fun getStr(callback: Callback) {
        httpGet("http://www.baidu.com", "", null, callback)
    }

    fun getList(array: ArrayMap<String, Any>, sync: Boolean = false, callback: Callback? = null): Response? {
        return if (sync) {
            httpGet("https://leancloud.cn:443/1.1/classes/", "todos", array)
        } else {
            httpGet("https://leancloud.cn:443/1.1/classes/", "todos", array, callback!!)
            null
        }
    }
}