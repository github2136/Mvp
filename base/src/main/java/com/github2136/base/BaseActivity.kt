package com.github2136.base

import android.os.Bundle
import android.os.Message
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.lang.ref.WeakReference

/**
 * Created by yb on 2018/11/2.
 */
abstract class BaseActivity<P : BaseMVPPresenter<*>> : AppCompatActivity(), IBaseMVPView {
    protected lateinit var mPresenter: P
    protected val TAG = this.javaClass.name
    protected lateinit var mApp: BaseApplication
    protected lateinit var mActivity: BaseActivity<P>
    protected lateinit var mHandler: Handler
    protected var mToast: Toast? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mApp = application as BaseApplication
        mApp.addActivity(this)
        mActivity = this
        setContentView(getLayoutId())
        mHandler = Handler(this)
        initPresenter()
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
        initData(savedInstanceState)
    }

    override fun onDestroy() {
        cancelRequest()
        mApp.removeActivity(this)
        super.onDestroy()
    }

    //获得presenter
    protected fun getPresenter(clazz: Class<P>): P {
        this.mPresenter = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(clazz)
        return mPresenter
    }

    ///////////////////////////////////////////////////////////////////////////
    // Handler
    ///////////////////////////////////////////////////////////////////////////
    class Handler(activity: BaseActivity<*>) : android.os.Handler() {
        var weakReference: WeakReference<BaseActivity<*>> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val activity = weakReference.get()
            activity?.handleMessage(msg)
        }
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

    // 显示进度框
    override fun showProgressDialog() {}

    // 显示进度框
    override fun showProgressDialog(@StringRes resId: Int) {}

    // 显示进度框
    override fun showProgressDialog(msg: String) {}

    // 关闭进度框
    override fun dismissDialog() {}

    protected fun handleMessage(msg: Message) {}

    //初始化Presenter
    protected abstract fun initPresenter()

    //布局ID
    protected abstract fun getLayoutId(): Int

    //初始化
    protected abstract fun initData(savedInstanceState: Bundle?)

    //取消请求
    protected fun cancelRequest() {
        mPresenter.cancelRequest()
    }
}