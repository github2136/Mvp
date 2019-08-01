package com.github2136.base

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType

/**
 * Created by yb on 2018/11/2.
 */
abstract class BaseFragment<P : BasePresenter> : Fragment() {
    protected val TAG = this.javaClass.name
    protected lateinit var mPresenter: P
    protected lateinit var mContext: Context
    protected val mHandler by lazy { Handler(this) }
    protected var mToast: Toast? = null
    protected val mDialog: ProgressDialog by lazy {
        val dialog = ProgressDialog(activity)
        dialog.setCancelable(false)
        dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        attach(context)
    }

    private fun attach(context: Context) {
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getViewResId(), container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val type = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        if (type.size > 1) {
            getPresenter(type[1] as Class<P>)
        } else {
            getPresenter(type[0] as Class<P>)
        }
        mPresenter.ldDialog.observe(this, Observer { str -> showDialog(str) })
        initObserve()
        initData(savedInstanceState)
    }

    //获得presenter
    protected fun getPresenter(clazz: Class<P>) {
        this.mPresenter = ViewModelProviders.of(this).get(clazz)
        (mPresenter as BasePresenter).init(this.toString())
    }

    override fun onDestroyView() {
        cancelRequest()
        super.onDestroyView()
    }

    private var baseView: View? = null
    /**
     * 获取SnackBar所需的view
     */
    fun findBaseView(): View {
        if (baseView == null) {
            baseView = view!!.findViewById(R.id.view)
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
            if (isAdded && !isDetached) {
                mDialog.show()
            }
        } else {
            if (mDialog.isShowing) {
                mDialog.dismiss()
            }
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
    //布局ID
    protected abstract fun getViewResId(): Int

    //初始化
    protected abstract fun initData(savedInstanceState: Bundle?)

    //初始化回调
    protected abstract fun initObserve()

    //取消请求
    fun cancelRequest() {}
}