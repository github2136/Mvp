package com.github2136.mvp.presenter

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github2136.base.BaseMVPPresenter
import com.github2136.base.CallbackData
import com.github2136.mvp.model.UserModel
import com.github2136.mvp.ui.activity.MainActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * Created by yb on 2018/11/2.
 */
class MainPresenter(private val app: Application) : BaseMVPPresenter<MainActivity>(app) {
    private lateinit var mUserModel: UserModel
    lateinit var ldGet: MutableLiveData<CallbackData>

    override fun init(v: MainActivity) {
        super.init(v)
        mUserModel = UserModel(app, v.toString())
    }

    fun get() {
        getView()?.let {
            if (!::ldGet.isInitialized) {
                ldGet = MutableLiveData()
            }
            ldGet.observe(it, Observer { t ->
                if (t?.isSuccess == true) {
                    it.getSuccessful(t.str)
                } else {
                    it.getFailure(failedStr)
                }
            })
            mUserModel.get(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    ldGet.postValue(CallbackData(false, failedStr))
                }

                override fun onResponse(call: Call?, response: Response?) {
                    if (response?.isSuccessful == true) {
                        ldGet.postValue(CallbackData(true, response.body()?.string()))
                    } else {
                        ldGet.postValue(CallbackData(false, failedStr))
                    }
                }
            })
        }
    }

    override fun cancelRequest() {
        mUserModel.cancelRequest()
    }
}