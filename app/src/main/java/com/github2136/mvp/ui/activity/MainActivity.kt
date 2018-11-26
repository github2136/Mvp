package com.github2136.mvp.ui.activity

import android.os.Bundle
import android.view.View
import com.github2136.base.BaseActivity
import com.github2136.mvp.R
import com.github2136.mvp.presenter.MainPresenter
import com.github2136.mvp.ui.view.IMainView
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by yb on 2018/11/2.
 */
class MainActivity : BaseActivity<MainPresenter>(), IMainView, View.OnClickListener {

    override fun initPresenter() {
        getPresenter(MainPresenter::class.java).init(this)
    }

    override fun getViewResId(): Int {
        return R.layout.activity_main
    }

    override fun initData(savedInstanceState: Bundle?) {
        mPresenter.get()
        btn1.setOnClickListener(this)
    }

    override fun getSuccessful(msg: String?) {
        tv_txt.text = msg
    }

    override fun getFailure(msg: String?) {
        tv_txt.text = msg
    }

    override fun onClick(v: View?) {
        mPresenter.get()
    }
}