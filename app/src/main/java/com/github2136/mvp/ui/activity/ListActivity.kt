package com.github2136.mvp.ui.activity

import android.os.Bundle
import com.github2136.base.paged.BaseListActivity
import com.github2136.base.paged.BaseListAdapter
import com.github2136.mvp.R
import com.github2136.mvp.model.entity.NetworkData
import com.github2136.mvp.presenter.ListPresenter
import com.github2136.mvp.ui.adapter.NetworkDataAdapter
import com.github2136.mvp.ui.view.IListView

class ListActivity : BaseListActivity<NetworkData, ListPresenter>(), IListView {
    override fun getLayoutId(): Int {
        return R.layout.activity_list
    }

    override fun initListData(savedInstanceState: Bundle?) {
        //添加查询时的参数
        mPresenter.get("sss")
    }

    override fun getAdapter(): BaseListAdapter<NetworkData> {
        return NetworkDataAdapter { mPresenter.retry() }
    }

}