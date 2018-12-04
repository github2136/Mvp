package com.github2136.mvp.presenter

import android.app.Application
import androidx.collection.ArrayMap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.github2136.base.BaseMVPPresenter
import com.github2136.base.paged.Listing
import com.github2136.base.paged.NetworkState
import com.github2136.mvp.model.DataModel
import com.github2136.mvp.model.entity.NetworkData
import com.github2136.mvp.model.entity.NetworkResult
import com.github2136.mvp.ui.view.IListView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Created by yb on 2018/11/28.
 */
class ListPresenter(private val app: Application) : BaseMVPPresenter<IListView>(app) {
    private lateinit var mListModel: DataModel
    private val params = MutableLiveData<String>()

    private val repoResult = Transformations.map(params) {
        getList(it)
    }

    val posts = Transformations.switchMap(repoResult, { it.pagedList })!!
    val networkState = Transformations.switchMap(repoResult, { it.networkState })!!
    val refreshState = Transformations.switchMap(repoResult, { it.refreshState })!!
    override fun init(v: IListView) {
        super.init(v)
        mListModel = DataModel(app, v.toString())
    }

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun get(params: String) {
        this.params.value = params
    }

    fun retry() {
        val listing = repoResult?.value
        listing?.retry?.invoke()
    }

    private fun getList(params: String): Listing<NetworkData> {
        val sourceFactory = ListDataSourceFactory()
        val livePagedList = LivePagedListBuilder(sourceFactory,
                PagedList.Config.Builder()
                        .setInitialLoadSizeHint(10)
                        .setPageSize(10)
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


    inner class ListDataSourceFactory : DataSource.Factory<Int, NetworkData>() {
        val sourceLiveData = MutableLiveData<ListDataSource>()
        override fun create(): DataSource<Int, NetworkData> {
            val source = ListDataSource()
            sourceLiveData.postValue(source)
            return source
        }
    }

    inner class ListDataSource : PageKeyedDataSource<Int, NetworkData>() {
        val networkState = MutableLiveData<NetworkState>()
        val initialLoad = MutableLiveData<NetworkState>()
        private var retry: (() -> Any)? = null

        fun retryAllFailed() {
            val prevRetry = retry
            retry = null
            prevRetry?.invoke()
        }

        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, NetworkData>) {
            initialLoad.postValue(NetworkState.LOADING)
            val p = ArrayMap<String, Any>()
            p.put("limit", params.requestedLoadSize)
            p.put("skip", 0)
            mListModel.getList(p, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    retry = {
                        loadInitial(params, callback)
                    }
                    val error = NetworkState.error(failedStr)
                    initialLoad.postValue(error)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        retry = null
                        initialLoad.postValue(NetworkState.LOADED)
                        response.body()?.string()?.let {
                            val list = mJsonUtil.getObjectByStr(it, NetworkResult::class.java)
                            list?.let {
                                callback.onResult(it.results, 0, 1)
                            }
                        }
                    } else {
                        retry = {
                            loadInitial(params, callback)
                        }
                        val error = NetworkState.error(failedStr)
                        initialLoad.postValue(error)
                    }
                }
            })
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, NetworkData>) {
            networkState.postValue(NetworkState.LOADING)
            val p = ArrayMap<String, Any>()
            p.put("limit", params.requestedLoadSize)
            p.put("skip", params.key * params.requestedLoadSize)
            mListModel.getList(p, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    retry = {
                        loadAfter(params, callback)
                    }
                    networkState.postValue(NetworkState.error(failedStr))
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        retry = null
                        response.body()?.string()?.let {
                            val list = mJsonUtil.getObjectByStr(it, NetworkResult::class.java)
                            list?.let {
                                callback.onResult(it.results, params.key + 1)
                            }
                        }
                        networkState.postValue(NetworkState.LOADED)
                    } else {
                        retry = {
                            loadAfter(params, callback)
                        }
                        networkState.postValue(NetworkState.error(failedStr))
                    }
                }
            })
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, NetworkData>) {
        }
    }

    override fun cancelRequest() {
        mListModel.cancelRequest()
    }
}
