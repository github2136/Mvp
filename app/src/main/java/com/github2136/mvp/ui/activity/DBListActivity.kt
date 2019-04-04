package com.github2136.mvp.ui.activity

import android.os.Bundle
import com.github2136.base.paged.BaseListDBActivity
import com.github2136.mvp.R
import com.github2136.mvp.model.entity.NetworkData
import com.github2136.mvp.presenter.DbListPresenter
import com.github2136.mvp.ui.adapter.NetworkDataAdapter

class DBListActivity : BaseListDBActivity<NetworkData, DbListPresenter>() {
    override fun initListData(savedInstanceState: Bundle?) {
        mPresenter.get()
    }

    override fun getAdapter() = NetworkDataAdapter {
        mPresenter.retry()
    }

    override fun getLayoutId() = R.layout.activity_dblist

    override fun initObserve() {

    }
}
