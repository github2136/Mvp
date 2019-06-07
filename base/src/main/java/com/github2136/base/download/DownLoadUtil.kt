package com.github2136.base.download

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.github2136.base.OkHttpInterceptor
import okhttp3.*
import java.io.*
import java.util.concurrent.TimeUnit

/**
 * Created by YB on 2019/6/6
 * 断点续传，每次只能下载一个文件，不可使用一个对象同时下载多个文件，如果返回路径则表示下载完成，如果返回""则表示文件尚未下载完成，如果返回null则表示下载失败
 */
class DownLoadUtil(app: Application) {
    protected val client: OkHttpClient by lazy {
        OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
    }
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private val downLoadDao = DownLoadDao(app)
    var stop = false
    //正在下载
    private var loading = false

    /**
     * 开始下载
     */
    fun downloadFile(url: String, filePath: String, callback: ((String?, Int) -> Unit)?) {
        if (loading) {
            return
        }
        loading = true
        stop = false
        //查看是否已经下载
        var fileObj = downLoadDao.get(url)
        fileObj?.apply {
            if (complete) {
                //记录存在并且已经下载完成
                loading = false
                callback?.apply { handler.post { invoke(filePath, 100) } }
                return
            }
        }

        if (fileObj == null) {
            //没下载过插入数据库数据
            fileObj = DownLoadFile(0, url, filePath, 0, 0, false)
            downLoadDao.install(fileObj)
        }

        val file = File(filePath)
        val randomFile = RandomAccessFile(file, "rw")

        val requestBuild = Request.Builder()
                .url(url)
                .addHeader("RANGE", "bytes=${fileObj.fileSize}-")
                .get()
        val request = requestBuild.build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                loading = false
                callback?.apply { handler.post { invoke(null, 0) } }
            }

            override fun onResponse(call: Call, response: Response) {
                var inputStream: InputStream
                val buf = ByteArray(2048)
                var len = 0

                response.body()?.apply {
                    val total = contentLength()
                    if (fileObj.fileSize == 0L) {
                        fileObj.fileTotal = total
                    }
                    randomFile.setLength(fileObj.fileTotal)
                    var current = fileObj.fileSize
                    inputStream = byteStream()
                    var time1 = System.currentTimeMillis()
                    var time2: Long
                    //跳过已下载的内容
                    randomFile.seek(fileObj.fileSize)
                    while ({ len = inputStream.read(buf);len }() != -1) {
                        current += len
                        randomFile.write(buf, 0, len)
                        fileObj.fileSize = current
                        if (current == fileObj.fileTotal) {
                            fileObj.complete = true
                        }

                        downLoadDao.update(fileObj)
                        time2 = System.currentTimeMillis()
                        if (time2 - time1 > 200) {
                            time1 = time2
                            callback?.apply { handler.post { invoke("", (current.toDouble() / total * 100).toInt()) } }
                        }
                        if (stop) {
                            break
                        }
                    }
                    inputStream.close()
                    loading = false
                    callback?.apply { handler.post { invoke(fileObj.filePath, 100) } }
                }
            }
        })
    }

    /**
     * 删除本地记录
     */
    fun deletePath(fileUrl: String) {
        downLoadDao.delete(fileUrl)
    }
}