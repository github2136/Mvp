package com.github2136.base.paged

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.github2136.base.BaseMVPPresenter
import com.github2136.base.IBaseMVPView

/**
 *  Created by yb on 2018/11/28.
 **/
abstract class BaseListMVPPresenter<V>(app: Application) : BaseMVPPresenter<V>(app) where  V : IBaseMVPView, V : LifecycleOwner {
    private val params = MutableLiveData<String>()
    private var result = Transformations.map(params){

    }

}