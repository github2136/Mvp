package com.github2136.base


import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import com.github2136.util.JsonUtil
import com.github2136.util.SPUtil
import java.lang.ref.WeakReference

/**
 * Created by yb on 2018/11/1.
 */
abstract class BaseMVPPresenter<V>(app: Application) : AndroidViewModel(app) where  V : IBaseMVPView, V : LifecycleOwner {
    protected lateinit var mView: WeakReference<V>
    protected val failedStr = "无法连接服务器"
    protected var mJsonUtil: JsonUtil = JsonUtil.instance
    protected var mSpUtil: SPUtil = SPUtil.getInstance(app)
    protected var mHandler: Handler = Handler(Looper.getMainLooper())

    open fun init(v: V) {
        mView = WeakReference(v)
    }

    fun getView(): V? {
        return mView.get()
    }

    fun postMain(runnable: Runnable) {
        mHandler.post(runnable)
    }

    //取消请求
    abstract fun cancelRequest()
}