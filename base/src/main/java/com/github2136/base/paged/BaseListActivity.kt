package com.github2136.base.paged

import android.os.Bundle
import androidx.paging.*
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github2136.base.BaseActivity
import com.github2136.base.BaseMVPPresenter
import com.github2136.base.R

/**
 * Created by yb on 2018/11/28.
 */
abstract class BaseListActivity<T, P : BaseMVPPresenter<*>> : BaseActivity<P>() {
    var pageSize=10
    protected lateinit var rvContent: RecyclerView
    protected lateinit var srContent: SwipeRefreshLayout
//    protected var mPageNumber = 0//页码
//    protected var mHasItemClick = true
    protected lateinit var mAdapter: PagedListAdapter<T, RecyclerView.ViewHolder>


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
//        //加载监听
//        mAdapter.setOnLoadMoreListener(mOnLoadMoreListener)
//        //启用加载更多
//        mAdapter.setEnableLoadMore(true)
//        //加载失败点击
//        mAdapter.isFailedRefresh = true
        //刷新监听
        srContent.setOnRefreshListener(mOnRefreshListener)
        val data=  LivePagedListBuilder(object : DataSource.Factory<Int, T>() {
            override fun create(): DataSource<Int, T> {
                return ListPageDataSource()
            }
        }, PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(pageSize)
                .build()).build()

        getFirstPage()
        initListData(savedInstanceState)
//        if (mHasItemClick) {
//            mAdapter.setOnItemClickListener(mOnItemClickListener)
//            mAdapter.setOnItemLongClickListener(mOnItemLongClickListener)
//        }
    }

    /**
     * 获取第一页数据
     */
    protected fun getFirstPage() {
//        mPageNumber = 0
        mHandler.post {
            srContent.isRefreshing = true
//            mAdapter.setIsRefresh(true)
            getListData(0,pageSize)
        }
    }

//    private val mOnLoadMoreListener = object : BaseLoadMoreRecyclerAdapter.OnLoadMoreListener {
//        override fun onLoadMore() {
//            //获取更多时禁止刷新
//            srContent.isEnabled = false
//            getListData()
//        }
//
//        override fun onRefresh() {
//            getFirstPage()
//        }
//    }

    private val mOnRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        //刷新时禁止获取更多
//        mPageNumber = 0
//        mAdapter.setIsRefresh(true)
        getListData(0,pageSize)
    }

//    private val mOnItemClickListener = BasePagedRecyclerAdapter.OnItemClickListener { baseRecyclerAdapter, i ->
//        var i = i
//        //点击事件
//        if (mAdapter.headView != null) {
//            i--
//        }
//        itemClick(mAdapter.getItem(i), i)
//    }
//    private val mOnItemLongClickListener = BasePagedRecyclerAdapter.OnItemLongClickListener { baseRecyclerAdapter, i ->
//        var i = i
//        //长按事件
//        if (mAdapter.headView != null) {
//            i--
//        }
//        itemLongClick(mAdapter.getItem(i), i)
//    }

//    protected fun getDataSuccessful(list: List<T>) {
//        //获取成功
//        srContent.isRefreshing = false
//        srContent.isEnabled = true
//        mAdapter.setIsRefresh(false)
//        if (mPageNumber == 0) {
//            mAdapter.isFailed = false
//            rvContent.adapter = mAdapter
//            mAdapter.setData(null)
//        }
//        mAdapter.loadMoreSucceed(list)
//        mPageNumber++
//    }
//
//    protected fun getDataFailure() {
//        //获取失败
//        srContent.isRefreshing = false
//        srContent.isEnabled = true
//        mAdapter.setIsRefresh(false)
//        if (mPageNumber == 0) {
//            mAdapter.setData(null)
//            mAdapter.isFailed = true
//            rvContent.adapter = mAdapter
//        } else {
//            mAdapter.loadMoreFailed()
//        }
//    }

    inner class ListPageDataSource : PageKeyedDataSource<Int, T>() {
        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {}

        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>) {
            getListData(0, params.requestedLoadSize)
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
            getListData(params.key * params.requestedLoadSize, params.requestedLoadSize)
        }
    }

    protected abstract fun initListData(savedInstanceState: Bundle?)

    protected abstract fun getAdapter(): PagedListAdapter<T, RecyclerView.ViewHolder>

    protected abstract fun getListData(skip: Int, limit: Int)

    protected fun itemClick(t: T, position: Int) {}

    protected fun itemLongClick(t: T, position: Int) {}
}