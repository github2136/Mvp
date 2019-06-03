package com.github2136.base.paged

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github2136.base.BaseActivity
import com.github2136.base.R

/**
 * Created by yb on 2018/11/28.
 */
abstract class BaseListActivity<T, P : BaseListPresenter<T>> : BaseActivity<P>() {
    protected val rvContent by lazy { findViewById<RecyclerView>(R.id.rv_content) }
    protected val srContent by lazy { findViewById<SwipeRefreshLayout>(R.id.sr_content) }
    protected val mAdapter by lazy { getAdapter() }

    override fun initData(savedInstanceState: Bundle?) {
        mAdapter.setOnItemClickListener { position -> itemClick(mAdapter.getItem(position)!!, position) }
        mAdapter.setOnItemLongClickListener { position -> itemClick(mAdapter.getItem(position)!!, position) }

        rvContent.adapter = mAdapter
        mPresenter.list.observe(this, Observer<PagedList<T>> {
            mAdapter.submitList(it)
        })
        mPresenter.networkState.observe(this, Observer {
            mAdapter.networkState = it
        })

        mPresenter.refreshState.observe(this, Observer {
            mAdapter.refreshState = it
            srContent.isRefreshing = it == NetworkState.LOADING
        })
        srContent.setOnRefreshListener {
            mPresenter.refresh()
        }
        initListData(savedInstanceState)
    }

    protected abstract fun initListData(savedInstanceState: Bundle?)

    protected abstract fun getAdapter(): BaseListAdapter<T>

    protected open fun itemClick(t: T, position: Int) {}

    protected open fun itemLongClick(t: T, position: Int) {}
}