package com.github2136.mvp.model

import android.app.Application
import androidx.collection.ArrayMap
import com.github2136.base.BaseMVPModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Created by yb on 2018/11/28.
 */
class DataModel(app: Application, tag: String) : BaseMVPModel(app, tag) {
    fun getStr(response: (call: Call, response: Response) -> Unit, failure: (call: Call, e: IOException) -> Unit) {
        httpGet("http://www.baidu.com", "", null, object : Callback {
            override fun onFailure(call: Call, e: IOException) = failure(call, e)

            override fun onResponse(call: Call, response: Response) = response(call, response)
        })
    }

    fun getList(array: ArrayMap<String, Any>, sync: Boolean = false,
                response: (call: Call, response: Response) -> Unit = { _, _ -> },
                failure: (call: Call, e: IOException) -> Unit = { _, _ -> })
            : Response? {
        return if (sync) {
            httpGet("https://leancloud.cn:443/1.1/classes/", "todos", array)
        } else {
            httpGet("https://leancloud.cn:443/1.1/classes/", "todos", array, object : Callback {
                override fun onFailure(call: Call, e: IOException) = failure(call, e)
                override fun onResponse(call: Call, response: Response) = response(call, response)
            })
            null
        }
    }
}