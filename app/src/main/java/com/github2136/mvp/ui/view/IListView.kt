package com.github2136.mvp.ui.view

import androidx.paging.PagedListAdapter
import com.github2136.base.IBaseMVPListView
import com.github2136.base.IBaseMVPView

/**
 * Created by yb on 2018/11/28.
 */
interface IListView : IBaseMVPListView  {

    fun getSuccessful()

    fun getFailure(msg: String?)
}