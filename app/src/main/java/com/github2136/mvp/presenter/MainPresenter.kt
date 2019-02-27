package com.github2136.mvp.presenter

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github2136.base.BaseMVPPresenter
import com.github2136.base.CallbackData
import com.github2136.mvp.model.DataModel
import com.github2136.mvp.ui.view.IMainView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Created by yb on 2018/11/2.
 */
class MainPresenter(private val app: Application) : BaseMVPPresenter<IMainView>(app) {
    private lateinit var mDataModel: DataModel
    val ldGet: MutableLiveData<CallbackData> = MutableLiveData()


    override fun init(v: IMainView) {
        super.init(v)
        mDataModel = DataModel(app, v.toString())

        ldGet.observe(mView, Observer { t ->
            if (t?.isSuccess == true) {
                mView.getSuccessful(t.str)
            } else {
                mView.getFailure(failedStr)
            }
        })
    }

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