package com.github2136.mvp.ui.activity

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import com.github2136.base.BaseActivity
import com.github2136.base.paged.NetworkState
import com.github2136.mvp.R
import com.github2136.mvp.model.entity.NetworkData
import com.github2136.mvp.presenter.ListPresenter
import com.github2136.mvp.ui.adapter.NetworkDataAdapter
import com.github2136.mvp.ui.view.IListView
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : BaseActivity<ListPresenter>(), IListView {

    override fun initPresenter() {
        getPresenter(ListPresenter::class.java).init(this)
    }

    override fun getViewResId(): Int {
        return R.layout.activity_list
    }

    override fun initData(savedInstanceState: Bundle?) {
        val adapter = NetworkDataAdapter { mPresenter.retry() }
        rv_content.adapter = adapter

        mPresenter.posts.observe(this, Observer<PagedList<NetworkData>> {
            adapter.submitList(it)
        })
        mPresenter.networkState.observe(this, Observer {
            adapter.networkState = it
        })

        mPresenter.refreshState.observe(this, Observer {
            adapter.refreshState = it
            srl_content.isRefreshing = it == NetworkState.LOADING
        })
        srl_content.setOnRefreshListener {
            mPresenter.refresh()
        }

        mPresenter.get("sss")
    }

    override fun getSuccessful() {

    }

    override fun getFailure(msg: String?) {

    }

}
