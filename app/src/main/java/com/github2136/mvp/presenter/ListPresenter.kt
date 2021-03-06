package com.github2136.mvp.presenter

import android.app.Application
import android.util.Log
import androidx.collection.ArrayMap
import com.github2136.base.paged.BaseListPresenter
import com.github2136.base.paged.NetworkState
import com.github2136.mvp.model.DataModel
import com.github2136.mvp.model.entity.NetworkData
import com.github2136.mvp.model.entity.NetworkResult
import java.util.*

/**
 * Created by yb on 2018/12/5.
 */
class ListPresenter(private val app: Application) : BaseListPresenter<NetworkData>(app) {
    override var initSize = 40

    override fun getDataSource(vararg paramsStr: Any): ListDataSource {
        //paramsStr查询时的参数，如果查询时需要则添加到DataSource中
        return LDataSource()
    }

    private val mListModel: DataModel by lazy { DataModel(app, mTag) }

    override fun cancelRequest() {
        mListModel.cancelRequest()
    }

    inner class LDataSource : ListDataSource() {
        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, NetworkData>) {
            Log.e("LDataSource", "loadInitial ${params.requestedLoadSize}")
            //首页初始化数据必须使用同步方法
            initialLoad.postValue(NetworkState.LOADING)
            val p = ArrayMap<String, Any>()
            p["limit"] = initSize
            p["skip"] = 0
            p["order"] = "rowNumber"
            p["where"] = "{\"\$or\":[{\"valid\":{\"\$exists\":false}},{\"\$and\":[{\"valid\":{\"\$exists\":true}},{\"valid\":true}]}]}"
            val response = mListModel.getList(p, true)

            if (Random().nextBoolean() && response?.isSuccessful == true) {
                retry = null
                response.body()?.string()?.let {
                    val list = mJsonUtil.getObjectByStr(it, NetworkResult::class.java)
                    list?.let {
                        callback.onResult(it.results, 0, 0)
                    }
                }
                initialLoad.postValue(NetworkState.LOADED)
            } else {
                retry = {
                    loadInitial(params, callback)
                }
                val error = NetworkState.error(failedStr)
                initialLoad.postValue(error)
            }
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, NetworkData>) {
            Log.e("LDataSource", "loadAfter ${params.requestedLoadSize}")
            networkState.postValue(NetworkState.LOADING)
            val p = ArrayMap<String, Any>()
            p["limit"] = params.requestedLoadSize
            p["skip"] = params.key * params.requestedLoadSize + initSize
            p["order"] = "rowNumber"
            p["where"] = "{\"\$or\":[{\"valid\":{\"\$exists\":false}},{\"\$and\":[{\"valid\":{\"\$exists\":true}},{\"valid\":true}]}]}"
            mListModel.getList(p,
                               response = { _, response ->
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
                               },
                               failure = { _, _ ->
                                   retry = {
                                       loadAfter(params, callback)
                                   }
                                   networkState.postValue(NetworkState.error(failedStr))
                               })
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, NetworkData>) {
            Log.e("LDataSource", "loadBefore ${params.requestedLoadSize}")
        }
    }
}