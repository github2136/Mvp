package com.github2136.mvp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.github2136.base.BaseActivity
import com.github2136.mvp.R
import com.github2136.mvp.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by yb on 2018/11/2.
 */
class MainActivity : BaseActivity<MainPresenter>(), View.OnClickListener {
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initData(savedInstanceState: Bundle?) {
        mPresenter.get()
        btn_retry.setOnClickListener(this)
        btn_list.setOnClickListener(this)
    }

    override fun initObserve() {
        mPresenter.ldGet.observe(this, Observer { t ->
            if (t?.isSuccess == true) {
                tv_txt.text = t.str
            } else {
                tv_txt.text = mPresenter.failedStr
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_retry -> {
                mPresenter.get()
            }
            R.id.btn_list -> {
                startActivity(Intent(this, ListActivity::class.java))
            }
            else -> {
            }
        }
    }
}