package com.github2136.base

import android.app.Application
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import androidx.collection.ArrayMap
import com.github2136.util.JsonUtil
import com.github2136.util.MessageDigestUtil
import com.github2136.util.SPUtil
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Created by yb on 2018/11/2.
 */
open class BaseModel(app: Application, tag: String) {
    protected val client: OkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .addNetworkInterceptor(OkHttpInterceptor())
            .build()
    }

    protected val handler by lazy { Handler(Looper.getMainLooper()) }
    protected var mApp: Application = app

    protected var mTag: String = tag
    protected var mSpUtil: SharedPreferences = SPUtil.getSharedPreferences(mApp)
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

    fun downloadFile(url: String, filePath: String, callback: ((Boolean?, Int) -> Unit)? = null) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
        val requestBuild = Request.Builder()
                .url(url)
                .tag(mTag)

        val request = requestBuild.build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback?.apply { handler.post { invoke(false, 0) } }
            }

            override fun onResponse(call: Call, response: Response) {
                var inputStream: InputStream
                val buf = ByteArray(2048)
                var len = 0
                var fos: FileOutputStream
                response.body()?.apply {
                    val total = contentLength()
                    var current = 0
                    inputStream = byteStream()
                    fos = FileOutputStream(file)
                    var time1 = System.currentTimeMillis()
                    var time2: Long

                    while ({ len = inputStream.read(buf);len }() != -1) {
                        current += len
                        fos.write(buf, 0, len)
                        time2 = System.currentTimeMillis()
                        if (time2 - time1 > 200) {
                            time1 = time2
                            callback?.apply { handler.post { invoke(null, (current.toDouble() / total * 100).toInt()) } }
                        }
                    }
                    fos.flush()
                    callback?.apply { handler.post { invoke(true, 100) } }
                    fos.close()
                    inputStream.close()
                } ?: callback?.apply { handler.post { invoke(false, 0) } }
            }
        })
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