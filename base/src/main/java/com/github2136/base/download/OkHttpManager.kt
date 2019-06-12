package com.github2136.base.download

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import android.os.AsyncTask.execute
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response


/**
 * Created by YB on 2019/6/11
 */
class OkHttpManager private constructor() {
    private val client: OkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .build()
    }

    fun call(url: String): Call {
        val request = Request.Builder()
            .url(url)
            .build()
        return client.newCall(request)
    }


    fun call(url: String, start: Long, end: Long): Call {
        val request = Request.Builder()
            .url(url)
            .header("RANGE", "bytes=$start-$end")
            .build()
        return client.newCall(request)
    }

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { OkHttpManager() }
    }
}