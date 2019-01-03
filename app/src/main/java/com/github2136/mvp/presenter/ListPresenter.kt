package com.github2136.mvp.presenter

import android.app.Application
import androidx.collection.ArrayMap
import com.github2136.base.paged.BaseListMVPPresenter
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
 * Created by yb on 2018/12/5.
 */
class ListPresenter(private val app: Application) : BaseListMVPPresenter<NetworkData, IListView>(app) {
    override var initSize = 40
    override fun getDataSource(paramsStr: String): ListDataSource {
        //paramsStr查询时的参数，如果查询时需要则添加到DataSource中
        return LDataSource()
    }

    private lateinit var mListModel: DataModel
    override fun init(v: IListView) {
        super.init(v)
        mListModel = DataModel(app, v.toString())
    }

    override fun cancelRequest() {
        mListModel.cancelRequest()
    }

    inner class LDataSource : ListDataSource() {
        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, NetworkData>) {
            //首页初始化数据必须使用同步方法
            initialLoad.postValue(NetworkState.LOADING)
            val p = ArrayMap<String, Any>()
            p.put("limit", params.requestedLoadSize)
            p.put("skip", 0)
            val response = mListModel.getList(p, true);
            if (response?.isSuccessful == true) {
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

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, NetworkData>) {
            networkState.postValue(NetworkState.LOADING)
            val p = ArrayMap<String, Any>()
            p.put("limit", params.requestedLoadSize)
            p.put("skip", (params.key - 1) * params.requestedLoadSize + initSize)
            mListModel.getList(p, callback = object : Callback {
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
    }
}