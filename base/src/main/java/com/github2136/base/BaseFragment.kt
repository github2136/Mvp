package com.github2136.base

import android.content.Context
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

/**
 * Created by yb on 2018/11/2.
 */
abstract class BaseFragment<P : BaseMVPPresenter<*>> : Fragment(), IBaseMVPView {
    protected val TAG = this.javaClass.name
    private var isInit: Boolean = false
    protected lateinit var mPresenter: P
    protected var mRootView: View? = null
    protected lateinit var mContext: Context
    protected lateinit var mHandler: Handler
    protected var mToast: Toast? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        attach(context)
    }

    private fun attach(context: Context) {
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHandler = Handler(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mRootView == null) {
            mRootView = inflater.inflate(getViewResId(), container, false)
        }
        val parent = mRootView!!.parent as ViewGroup
        parent?.removeView(mRootView)
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isInit) {
            isInit = true
            mPresenter = getPresenter()
            initData(savedInstanceState)
        }
    }

    override fun onDestroyView() {
        if (isInit) {
            cancelRequest()
        }
        super.onDestroyView()
    }

    fun showToast(msg: String) {
        mToast?.let {
            it.setText(msg)
            it.duration = Toast.LENGTH_SHORT
            it.show()
        }
    }

    fun showToast(@StringRes resId: Int) {
        mToast?.let {
            it.setText(resId)
            it.duration = Toast.LENGTH_SHORT
            it.show()
        }
    }

    fun showToastLong(msg: String) {
        mToast?.let {
            it.setText(msg)
            it.duration = Toast.LENGTH_LONG
            it.show()
        }
    }

    fun showToastLong(@StringRes resId: Int) {
        mToast?.let {
            it.setText(resId)
            it.duration = Toast.LENGTH_LONG
            it.show()
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Handler
    ///////////////////////////////////////////////////////////////////////////
    class Handler(fragment: BaseFragment<*>) : android.os.Handler() {
        var weakReference: WeakReference<BaseFragment<*>> = WeakReference(fragment)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val fragment = weakReference.get()
            fragment?.handleMessage(msg)
        }
    }

    protected fun handleMessage(msg: Message) {}

    protected abstract fun getPresenter(): P

    //布局ID
    protected abstract fun getViewResId(): Int

    //初始化
    protected abstract fun initData(savedInstanceState: Bundle?)

    //取消请求
    fun cancelRequest() {}
}