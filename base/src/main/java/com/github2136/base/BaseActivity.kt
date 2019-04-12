package com.github2136.base

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType


/**
 * Created by yb on 2018/11/2.
 */
abstract class BaseActivity<P : BasePresenter> : AppCompatActivity() {
    protected lateinit var mPresenter: P
    protected val TAG = this.javaClass.name
    protected lateinit var mApp: BaseApplication
    protected lateinit var mActivity: BaseActivity<P>
    protected lateinit var mHandler: Handler
    protected var mToast: Toast? = null
    protected val mDialog: ProgressDialog by lazy {
        val dialog = ProgressDialog(this)
        dialog.setCancelable(false)
        dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mApp = application as BaseApplication
        mApp.addActivity(this)
        mActivity = this
        setContentView(getLayoutId())
        mHandler = Handler(this)

        val type = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        if (type.size > 1) {
            getPresenter(type[1] as Class<P>)
        } else {
            getPresenter(type[0] as Class<P>)
        }
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
        mPresenter.ldDialog.observe(this, Observer { str -> showDialog(str) })
        initObserve()
        initData(savedInstanceState)
    }

    override fun onDestroy() {
        cancelRequest()
        mApp.removeActivity(this)
        super.onDestroy()
    }

    //获得presenter
    protected fun getPresenter(clazz: Class<P>) {
        this.mPresenter = ViewModelProviders.of(this).get(clazz)
        mPresenter.init(this.toString())
    }

    ///////////////////////////////////////////////////////////////////////////
    // Handler
    ///////////////////////////////////////////////////////////////////////////
    class Handler(activity: BaseActivity<*>) : android.os.Handler() {
        private var weakReference: WeakReference<BaseActivity<*>> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val activity = weakReference.get()
            activity?.handleMessage(msg)
        }
    }

    private var baseView: View? = null
    /**
     * 获取SnackBar所需的view
     */
    open fun findBaseView(): View {
        if (baseView == null) {
            baseView = findViewById(R.id.view)
        }
        return baseView!!
    }

    fun showSnackBar(msg: String) {
        Snackbar.make(findBaseView(), msg, Snackbar.LENGTH_SHORT).show()
    }

    fun showSnackBar(@StringRes resId: Int) {
        Snackbar.make(findBaseView(), resId, Snackbar.LENGTH_SHORT).show()
    }

    fun showSnackBarLong(msg: String) {
        Snackbar.make(findBaseView(), msg, Snackbar.LENGTH_LONG).show()
    }

    fun showSnackBarLong(@StringRes resId: Int) {
        Snackbar.make(findBaseView(), resId, Snackbar.LENGTH_LONG).show()
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

    open fun showDialog(msg: String?) {
        if (!TextUtils.isEmpty(msg)) {
            mDialog.setMessage(msg)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (!isDestroyed || !isFinishing) {
                    mDialog.show()
                }
            } else {
                if (!isFinishing) {
                    mDialog.show()
                }
            }
        } else {
            if (mDialog.isShowing) {
                mDialog.dismiss()
            }
        }
    }

    protected open fun handleMessage(msg: Message) {}

    //布局ID
    protected abstract fun getLayoutId(): Int

    //初始化
    protected abstract fun initData(savedInstanceState: Bundle?)

    //初始化回调
    protected abstract fun initObserve()

    //取消请求
    protected fun cancelRequest() {
        mPresenter.cancelRequest()
    }
}