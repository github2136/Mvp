package com.github2136.mvp.presenter

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.github2136.base.BaseMVPPresenter
import com.github2136.base.CallbackData
import com.github2136.mvp.model.DataModel

/**
 * Created by yb on 2018/11/2.
 */
class MainPresenter(private val app: Application) : BaseMVPPresenter(app) {
    private val mDataModel: DataModel  by lazy { DataModel(app, mTag) }
    val ldGet: MutableLiveData<CallbackData> = MutableLiveData()

    fun get() {
        mDataModel.getStr(
                response = { _, response ->
                    if (response.isSuccessful) {
                        ldGet.postValue(CallbackData(true, response.body()?.string()))
                    } else {
                        ldGet.postValue(CallbackData(false, failedStr))
                    }
                },
                failure = { _, _ -> ldGet.postValue(CallbackData(false, failedStr)) }
        )
    }

    override fun cancelRequest() {
        mDataModel.cancelRequest()
    }
}