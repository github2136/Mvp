package com.github2136.base

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView

/**
 * Created by yb on 2018/11/2.
 */
abstract class BaseListActivity <T,P: BaseMVPPresenter<*>> : BaseActivity<P>() {
    protected lateinit var rvContent: RecyclerView
    protected lateinit  var srContent: SwipeRefreshLayout
    protected var mPageNumber = 0//页码
    protected var mHasItemClick = true
    protected lateinit  var mAdapter: BaseLoadMoreRecyclerAdapter<T>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initData(savedInstanceState)
    }

    protected abstract fun getLayoutId(): Int

    override fun initData(savedInstanceState: Bundle?) {
        srContent = findViewById(R.id.sr_content)
        rvContent = findViewById(R.id.rv_content)

        mAdapter = getAdapter()
        //加载监听
        mAdapter.setOnLoadMoreListener(mOnLoadMoreListener)
        //启用加载更多
        mAdapter.setEnableLoadMore(true)
        //加载失败点击
        mAdapter.isFailedRefresh = true
        //刷新监听
        srContent.setOnRefreshListener(mOnRefreshListener)
        getFirstPage()
        initListData(savedInstanceState)
        if (mHasItemClick) {
            mAdapter.setOnItemClickListener(mOnItemClickListener)
            mAdapter.setOnItemLongClickListener(mOnItemLongClickListener)
        }
    }

    /**
     * 获取第一页数据
     */
    protected fun getFirstPage() {
        mPageNumber = 0
        mHandler.post {
            srContent.isRefreshing = true
            mAdapter.setIsRefresh(true)
            getListData()
        }
    }

    private val mOnLoadMoreListener = object : BaseLoadMoreRecyclerAdapter.OnLoadMoreListener {
        override fun onLoadMore() {
            //获取更多时禁止刷新
            srContent.isEnabled = false
            getListData()
        }

        override fun onRefresh() {
            getFirstPage()
        }
    }
    private val mOnRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        //刷新时禁止获取更多
        mPageNumber = 0
        mAdapter.setIsRefresh(true)
        getListData()
    }

    private val mOnItemClickListener = BaseRecyclerAdapter.OnItemClickListener { baseRecyclerAdapter, i ->
        var i = i
        //点击事件
        if (mAdapter.headView != null) {
            i--
        }
        itemClick(mAdapter.getItem(i), i)
    }
    private val mOnItemLongClickListener = BaseRecyclerAdapter.OnItemLongClickListener { baseRecyclerAdapter, i ->
        var i = i
        //长按事件
        if (mAdapter.headView != null) {
            i--
        }
        itemLongClick(mAdapter.getItem(i), i)
    }

    protected fun getDataSuccessful(list: List<T>) {
        //获取成功
        srContent.isRefreshing = false
        srContent.isEnabled = true
        mAdapter.setIsRefresh(false)
        if (mPageNumber == 0) {
            mAdapter.isFailed = false
            rvContent.adapter = mAdapter
            mAdapter.setData(null)
        }
        mAdapter.loadMoreSucceed(list)
        mPageNumber++
    }

    protected fun getDataFailure() {
        //获取失败
        srContent.isRefreshing = false
        srContent.isEnabled = true
        mAdapter.setIsRefresh(false)
        if (mPageNumber == 0) {
            mAdapter.setData(null)
            mAdapter.isFailed = true
            rvContent.adapter = mAdapter
        } else {
            mAdapter.loadMoreFailed()
        }
    }

    protected abstract fun initListData(savedInstanceState: Bundle?)

    protected abstract fun getAdapter(): BaseLoadMoreRecyclerAdapter<T>

    protected abstract fun getListData()

    protected fun itemClick(t: T, position: Int) {}

    protected fun itemLongClick(t: T, position: Int) {}
}