package com.github2136.base

import android.app.Application
import androidx.collection.ArrayMap
import com.github2136.util.JsonUtil
import com.github2136.util.MessageDigestUtil
import com.github2136.util.SPUtil
import okhttp3.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Created by yb on 2018/11/2.
 */
open class BaseModel(app: Application, tag: String) {
    protected var client: OkHttpClient = OkHttpClient()
    protected var mApp: Application = app

    protected var mTag: String = tag
    protected var mSpUtil: SPUtil = SPUtil.getInstance(mApp)
    protected var mJsonUtil: JsonUtil = JsonUtil.instance

    protected fun httpGet(url: String,
                          method: String,
                          params: ArrayMap<String, Any>?,
                          callback: Callback) {
        val urlSb = StringBuilder(url + method)

        params?.let {
            urlSb.append("?")
            for ((key, value) in it) {
                urlSb.append(key)
                urlSb.append("=")
                urlSb.append(value)
                urlSb.append("&")
            }
            urlSb.deleteCharAt(urlSb.length - 1)
        }
        val timestamp = Date().time.toString()
        val sign = MessageDigestUtil.getMessageDigest((timestamp + "V1K3AAxOEfvktc3leSVBpCWn").toByteArray(), "MD5") +
                "," + timestamp
        val request = Request.Builder()
                .url(urlSb.toString())
                .addHeader("X-LC-Id", "3s0xLb9cJWhTWg35ClYDB1y5-gzGzoHsz")
                .addHeader("X-LC-Sign", sign)
                .tag(mTag)
                .build()
        client.newCall(request).enqueue(callback)
    }

    protected fun httpGet(url: String,
                          method: String,
                          params: ArrayMap<String, Any>?
    ): Response? {
        val urlSb = StringBuilder(url + method)

        params?.let {
            urlSb.append("?")
            for ((key, value) in it) {
                urlSb.append(key)
                urlSb.append("=")
                urlSb.append(value)
                urlSb.append("&")
            }
            urlSb.deleteCharAt(urlSb.length - 1)
        }
        val timestamp = Date().time.toString()
        val sign = MessageDigestUtil.getMessageDigest((timestamp + "V1K3AAxOEfvktc3leSVBpCWn").toByteArray(), "MD5") +
                "," + timestamp
        val request = Request.Builder()
                .url(urlSb.toString())
                .addHeader("X-LC-Id", "3s0xLb9cJWhTWg35ClYDB1y5-gzGzoHsz")
                .addHeader("X-LC-Sign", sign)
                .tag(mTag)
                .build()


        return client.newCall(request).execute()
    }

    protected fun httpPost(url: String,
                           method: String,
                           params: ArrayMap<String, Any>?,
                           callback: Callback) {

        val JSON = MediaType.parse("application/json")
        var json = ""
        params?.let {
            json = mJsonUtil.getGson().toJson(params)
        }
        val body = RequestBody.create(JSON, json)

        val request = Request.Builder()
                .url(url + method)
                .tag(mTag)
                .post(body)
                .build()

        client.newCall(request).enqueue(callback)
    }

    fun cancelRequest() {
        for (call in client.dispatcher().queuedCalls()) {
            if (call.request().tag() == mTag)
                call.cancel()
        }
        for (call in client.dispatcher().runningCalls()) {
            if (call.request().tag() == mTag)
                call.cancel()
        }
    }
}