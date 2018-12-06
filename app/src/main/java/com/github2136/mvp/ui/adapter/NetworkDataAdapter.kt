package com.github2136.mvp.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.github2136.base.ViewHolderRecyclerView
import com.github2136.base.paged.BaseListAdapter
import com.github2136.mvp.R
import com.github2136.mvp.model.entity.NetworkData

/**
 * Created by yb on 2018/11/29.
 */
class NetworkDataAdapter(retryCallback: () -> Unit) : BaseListAdapter<NetworkData>(retryCallback, DIFF_CALLBACK) {

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_data
    }

    override fun onBindView(t: NetworkData, holder: ViewHolderRecyclerView, position: Int) {
        holder.setText(R.id.tv_txt, t.name)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NetworkData>() {
            override fun areItemsTheSame(oldConcert: NetworkData, newConcert: NetworkData): Boolean =
                    oldConcert.objectId == newConcert.objectId

            override fun areContentsTheSame(oldConcert: NetworkData, newConcert: NetworkData): Boolean =
                    oldConcert == newConcert
        }
    }
}