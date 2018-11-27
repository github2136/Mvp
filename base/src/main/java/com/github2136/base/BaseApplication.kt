package com.github2136.base

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.github2136.util.CrashHandler
import com.squareup.leakcanary.LeakCanary
import java.util.*

/**
 * Created by yb on 2018/11/2.
 */
class BaseApplication : Application() {
    private var mActivitys: ArrayList<AppCompatActivity>? = null

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        // Normal app init code...
        mActivitys = ArrayList()
        CrashHandler.getInstance(this).setCallback(object : CrashHandler.CrashHandlerCallback {
            override fun finishAll() {
                this@BaseApplication.finishAll()
            }

            override fun submitLog(deviceInfo: Map<String, String>, exception: String) {

            }
        })
    }


    fun addActivity(act: AppCompatActivity) {
        this.mActivitys!!.add(act)
    }

    fun removeActivity(act: AppCompatActivity) {
        if (mActivitys!!.contains(act)) {
            mActivitys!!.remove(act)
        }
    }

    fun finishAll() {
        var i = 0
        val len = mActivitys!!.size
        while (i < len) {
            val act = mActivitys!![i]
            act.finish()
            i++
        }
    }
}