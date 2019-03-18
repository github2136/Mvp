package com.github2136.base

import android.app.Application
import androidx.collection.ArrayMap
import com.github2136.util.JsonUtil
import com.github2136.util.SPUtil
import okhttp3.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Created by yb on 2018/11/2.
 */
open class BaseMVPModel(app: Application, tag: String) {
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
        val timestamp = getUTCTime().toString()
        val request = Request.Builder()
                .url(urlSb.toString())
                .addHeader("X-LC-Id", "3s0xLb9cJWhTWg35ClYDB1y5-gzGzoHsz")
                .addHeader("X-LC-Sign", getMD5(timestamp + "V1K3AAxOEfvktc3leSVBpCWn") + "," + timestamp)
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
        val timestamp = getUTCTime().toString()
        val request = Request.Builder()
                .url(urlSb.toString())
                .addHeader("X-LC-Id", "3s0xLb9cJWhTWg35ClYDB1y5-gzGzoHsz")
                .addHeader("X-LC-Sign", getMD5(timestamp + "V1K3AAxOEfvktc3leSVBpCWn") + "," + timestamp)
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


    /**
     * 获取UTC时间
     *
     * @return
     */
    private fun getUTCTime(): Long {
        val cal = Calendar.getInstance()
        cal.timeZone = TimeZone.getTimeZone("UTC")
        return cal.timeInMillis
    }

    private fun getMD5(msg: String): String {
        val hash: ByteArray
        try {
            hash = MessageDigest.getInstance("MD5").digest(msg.toByteArray())
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Huh, MD5 should be supported?", e)
        }

        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            if (b.toInt() and 0xFF < 0x10) hex.append("0")
            hex.append(Integer.toHexString((b.toInt() and 0xFF)))
        }
        return hex.toString()
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