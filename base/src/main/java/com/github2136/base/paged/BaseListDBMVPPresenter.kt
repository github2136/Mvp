package com.github2136.base.paged

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.github2136.base.BaseMVPPresenter

/**
 *  Created by yb on 2018/11/28.
 **/
abstract class BaseListDBMVPPresenter<T>(app: Application) : BaseMVPPresenter(app) {
    //初始化页数量一般为默认大小3倍
    open var initSize = 30
    //每页数量
    open var pageSize = 10
    //提前XX数量项开始查询一般为pagesize的整数倍
    open var prefetchSize = 30
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
        val dn = getBoundaryCallback()
        val livePagedList = LivePagedListBuilder(
                getDBSource(),
                PagedList.Config.Builder()
                        .setInitialLoadSizeHint(initSize)
                        .setPageSize(pageSize)
                        .setPrefetchDistance(prefetchSize)
                        .setEnablePlaceholders(false)
                        .build())
                .setBoundaryCallback(dn)
                .build()
        val refreshState = dn.initialLoad
        return Listing(
                pagedList = livePagedList,
                networkState = dn.networkState,
                retry = {
                    dn.retryAllFailed()
                },
                refresh = {
                    dn.onZeroItemsLoaded()
                },
                refreshState = refreshState
        )
    }

    fun get(params: String = "") {
        this@BaseListDBMVPPresenter.params.value = params
    }

    abstract inner class DNBoundaryCallback : PagedList.BoundaryCallback<T>() {
        val networkState = MutableLiveData<NetworkState>()
        val initialLoad = MutableLiveData<NetworkState>()
        var retry: (() -> Any)? = null
        val helper = PagingRequestHelper(executor)
        fun retryAllFailed() {
            val prevRetry = retry
            retry = null
            prevRetry?.let {
                getNetworkExecutor().execute {
                    it.invoke()
                }
            }
        }
    }

    abstract fun getBoundaryCallback(): DNBoundaryCallback
    //本地数据库数据
    abstract fun getDBSource(): DataSource.Factory<Int, T>

}