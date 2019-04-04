package com.github2136.mvp.presenter

import android.app.Application
import android.util.Log
import androidx.collection.ArrayMap
import androidx.paging.DataSource
import com.github2136.base.paged.BaseListDBMVPPresenter
import com.github2136.base.paged.NetworkState
import com.github2136.base.paged.PagingRequestHelper
import com.github2136.base.paged.executor
import com.github2136.mvp.model.DataModel
import com.github2136.mvp.model.db.DBHelper
import com.github2136.mvp.model.entity.NetworkData
import com.github2136.mvp.model.entity.NetworkResult
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.lang.RuntimeException

class DbListPresenter(private val app: Application) : BaseListDBMVPPresenter<NetworkData>(app) {
    //    override var initSize = 40
    private lateinit var mListModel: DataModel

    override fun init(tag: String) {
        super.init(tag)
        mListModel = DataModel(app, tag)
    }

    override fun getDBSource(): DataSource.Factory<Int, NetworkData> = DBHelper.getInstance(app).networkDataDao().getAllData()

    override fun getBoundaryCallback() = Boundary()

    inner class Boundary : DNBoundaryCallback() {
        override fun onZeroItemsLoaded() {
            Log.e("DbListPresenter", "onzero")
            initialLoad.postValue(NetworkState.LOADING)
            helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) { helperCallback ->
                val p = ArrayMap<String, Any>()
                p["limit"] = initSize
                p["skip"] = 0
                p["order"] = "rowNumber"
                p["where"] = "{\"\$or\":[{\"valid\":{\"\$exists\":false}},{\"\$and\":[{\"valid\":{\"\$exists\":true}},{\"valid\":true}]}]}"
                mListModel.getList(p,
                        response = { _, response ->
                            if (response.isSuccessful) {
                                retry = null
                                response.body()?.string()?.let {
                                    val list = mJsonUtil.getObjectByStr(it, NetworkResult::class.java)
                                    list?.let {
                                        executor.execute {
                                            DBHelper.getInstance(app).networkDataDao().insert(list.results)
                                            Log.e("DbListPresenter", "onzero succ")
                                            helperCallback.recordSuccess()
                                        }
                                    }
                                }
                                initialLoad.postValue(NetworkState.LOADED)
                            } else {
                                retry = {
                                    onZeroItemsLoaded()
                                }
                                initialLoad.postValue(NetworkState.error(failedStr))
                                Log.e("DbListPresenter", "onzero fail ${response.code()} ${response.message()}")
                                helperCallback.recordFailure(RuntimeException("request code ${response.code()}"))
                            }
                        },
                        failure = { _, e ->
                            retry = {
                                onZeroItemsLoaded()
                            }
                            initialLoad.postValue(NetworkState.error(failedStr))
                            Log.e("DbListPresenter", "onzero fail2 ${e.message}")
                            helperCallback.recordFailure(e)
                        })
            }
        }

        override fun onItemAtEndLoaded(itemAtEnd: NetworkData) {
            Log.e("DbListPresenter", "ItemAtEnd ${itemAtEnd.name}")
            networkState.postValue(NetworkState.LOADING)
            helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) { helperCallback ->
                val p = ArrayMap<String, Any>()
                p["limit"] = pageSize
                p["order"] = "rowNumber"
                p["where"] = "{\"\$or\":[{\"valid\":{\"\$exists\":false}},{\"\$and\":[{\"valid\":{\"\$exists\":true}},{\"valid\":true}]}]," +
                        "\"rowNumber\":{\"\$gt\":${itemAtEnd.rowNumber}}}"
                mListModel.getList(p,
                        response = { _, response ->
                            if (response.isSuccessful) {
                                retry = null
                                response.body()?.string()?.let {
                                    val list = mJsonUtil.getObjectByStr(it, NetworkResult::class.java)
                                    list?.let {
                                        executor.execute {
                                            DBHelper.getInstance(app).networkDataDao().insert(list.results)
                                            Log.e("DbListPresenter", "ItemAtEnd succ")
                                            helperCallback.recordSuccess()
                                        }
                                    }
                                }
                                networkState.postValue(NetworkState.LOADED)
                            } else {
                                retry = {
                                    onItemAtEndLoaded(itemAtEnd)
                                }
                                networkState.postValue(NetworkState.error(failedStr))
                                Log.e("DbListPresenter", "ItemAtEnd fail  ${response.code()} ${response.message()}\")")
                                helperCallback.recordFailure(RuntimeException("request code ${response.code()}"))
                            }
                        },
                        failure = { _, e ->
                            retry = {
                                onItemAtEndLoaded(itemAtEnd)
                            }
                            networkState.postValue(NetworkState.error(failedStr))
                            Log.e("DbListPresenter", "ItemAtEnd fail2 ${e.message}")
                            helperCallback.recordFailure(e)
                        })
            }
        }

        override fun onItemAtFrontLoaded(itemAtFront: NetworkData) {
            Log.e("DbListPresenter", "itemAtFront ${itemAtFront.name}")
            networkState.postValue(NetworkState.LOADING)
            helper.runIfNotRunning(PagingRequestHelper.RequestType.BEFORE) { helperCallback ->
                val p = ArrayMap<String, Any>()
                p["limit"] = pageSize
                p["order"] = "-rowNumber"
                p["where"] = "{\"\$or\":[{\"valid\":{\"\$exists\":false}},{\"\$and\":[{\"valid\":{\"\$exists\":true}},{\"valid\":true}]}]," +
                        "\"rowNumber\":{\"\$lt\":${itemAtFront.rowNumber}}}"
                mListModel.getList(p,
                        response = { _, response ->
                            if (response.isSuccessful) {
                                retry = null
                                response.body()?.string()?.let {
                                    val list = mJsonUtil.getObjectByStr(it, NetworkResult::class.java)
                                    list?.let {
                                        executor.execute {
                                            DBHelper.getInstance(app).networkDataDao().insert(list.results)
                                            Log.e("DbListPresenter", "itemAtFront succ")
                                            helperCallback.recordSuccess()
                                        }
                                    }
                                }
                                networkState.postValue(NetworkState.LOADED)
                            } else {
                                retry = {
                                    onItemAtFrontLoaded(itemAtFront)
                                }
                                networkState.postValue(NetworkState.error(failedStr))
                                Log.e("DbListPresenter", "itemAtFront fail  ${response.code()} ${response.message()}")
                                helperCallback.recordFailure(RuntimeException("request code ${response.code()}"))
                            }
                        },
                        failure = { _, e ->
                            retry = {
                                onItemAtFrontLoaded(itemAtFront)
                            }
                            networkState.postValue(NetworkState.error(failedStr))
                            Log.e("DbListPresenter", "itemAtFront fail2 ${e.message}")
                            helperCallback.recordFailure(e)
                        })
            }
        }
    }

    override fun cancelRequest() {

    }
}