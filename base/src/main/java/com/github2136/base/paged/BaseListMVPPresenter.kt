package com.github2136.base.paged

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.github2136.base.BaseMVPPresenter
import com.github2136.base.IBaseMVPView

/**
 *  Created by yb on 2018/11/28.
 **/
abstract class BaseListMVPPresenter<T, V : IBaseMVPView>(app: Application) : BaseMVPPresenter<V>(app) {
    //初始化页数量
    open var initSize = 10
    //每页数量
    open var pageSize = 10
    private val params = MutableLiveData<String>()
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

    private fun getList(paramsStr: String): Listing<T> {
        val sourceFactory = ListDataSourceFactory(paramsStr)
        val livePagedList = LivePagedListBuilder(sourceFactory,
                PagedList.Config.Builder()
                        .setInitialLoadSizeHint(initSize)
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

    fun get(params: String = "") {
        this@BaseListMVPPresenter.params.value = params
    }

    inner class ListDataSourceFactory(private val paramsStr: String) : DataSource.Factory<Int, T>() {
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
            prevRetry?.invoke()
        }

        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>) {}

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {}

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {}
    }

    abstract fun getDataSource(paramsStr: String): ListDataSource
}