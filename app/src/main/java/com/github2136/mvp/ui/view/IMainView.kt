package com.github2136.mvp.ui.view

import com.github2136.base.IBaseMVPView

/**
 * Created by yb on 2018/11/2.
 */
interface IMainView : IBaseMVPView {
    fun getSuccessful(msg: String?)

    fun getFailure(msg: String?)
}