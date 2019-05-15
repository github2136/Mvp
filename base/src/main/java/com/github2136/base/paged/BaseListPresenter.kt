package com.github2136.base.paged

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.github2136.base.BasePresenter

/**
 *  Created by yb on 2018/11/28.
 **/
abstract class BaseListPresenter<T>(app: Application) : BasePresenter(app) {
    //初始化页数量一般为默认大小3倍
    open var initSize = 30
    //每页数量
    open var pageSize = 10
    //提前XX数量项开始查询一般为pagesize的整数倍
    open var prefetchSize = 30
    private val params = MutableLiveData<Array<Any>>()
    private val repoResult = Transformations.map(params) { getList(it) }

    val list = Transformations.switchMap(repoResult) { it.pagedList }
    val networkState = Transformations.switchMap(repoResult) { it.networkState }
    val refreshState = Transformations.switchMap(repoResult) { it.refreshState }
    //刷新
    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }

    private fun getList(vararg paramsStr: Any): Listing<T> {
        val sourceFactory = ListDataSourceFactory(paramsStr)
        val livePagedList = LivePagedListBuilder(
            sourceFactory,
            PagedList.Config.Builder()
                .setInitialLoadSizeHint(initSize)
                .setPrefetchDistance(prefetchSize)
                .setPageSize(pageSize)
                .build()
        ).build()
        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.initialLoad
        }
        return Listing(
            pagedList = livePagedList,
            networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
                it.networkState
            },
            retry = {
                sourceFactory.sourceLiveData.value?.retryAllFailed()
            },
            refresh = {
                sourceFactory.sourceLiveData.value?.invalidate()
            },
            refreshState = refreshState
        )
    }

    fun get(vararg params: Any) {
        this@BaseListPresenter.params.value = arrayOf(*params)
    }

    inner class ListDataSourceFactory(private vararg val paramsStr: Any) : DataSource.Factory<Int, T>() {
        val sourceLiveData = MutableLiveData<ListDataSource>()
        override fun create(): DataSource<Int, T> {
            val source = getDataSource(paramsStr)
            sourceLiveData.postValue(source)
            return source
        }
    }

    abstract inner class ListDataSource : PageKeyedDataSource<Int, T>() {
        val networkState = MutableLiveData<NetworkState>()
        val initialLoad = MutableLiveData<NetworkState>()
        var retry: (() -> Any)? = null

        fun retryAllFailed() {
            val prevRetry = retry
            retry = null
            prevRetry?.let {
                getNetworkExecutor().execute {
                    it.invoke()
                }
            }
        }

        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>) {}

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {}

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {}
    }

    abstract fun getDataSource(vararg paramsStr: Any): ListDataSource
}