package com.github2136.base.paged

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.github2136.base.R
import com.github2136.base.ViewHolderRecyclerView

/**
 *  Created by yb on 2018/12/2.
 **/
abstract class BaseListAdapter<T>(private val retryCallback: () -> Unit, diffCallback: DiffUtil.ItemCallback<T>) :
        PagedListAdapter<T, ViewHolderRecyclerView>(diffCallback) {
    //查询时网络状态
    var networkState: NetworkState? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    //刷新时状态
    var refreshState: NetworkState? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    protected lateinit var mLayoutInflater: LayoutInflater
    /**
     * 通过类型获得布局ID
     *
     * @param viewType
     * @return
     */
    abstract fun getLayoutId(viewType: Int): Int

    override fun getItemCount(): Int {
        var count = super.getItemCount()
        if (count == 0) {
            //无数据
            return if (refreshState != null && refreshState?.status != Status.RUNNING) 1 else 0
        } else {
            count += 1
        }
        return count
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemCount == 1) {
            when {
                refreshState?.status == Status.SUCCESS -> R.layout.item_network_empty
                refreshState?.status == Status.FAILED -> R.layout.item_network_failed
                else -> super.getItemViewType(position)
            }
        } else {
            if (position == itemCount - 1) {
                R.layout.item_network_state
            } else {
                super.getItemViewType(position)
            }
        }
    }

    public override fun getItem(position: Int): T? {
        return super.getItem(position)
    }

    protected abstract fun onBindView(t: T, holder: ViewHolderRecyclerView, position: Int)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRecyclerView {
        if (!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }
        return when (viewType) {
            R.layout.item_network_empty -> NetworkStateItemViewHolder.create(parent, viewType) { }
            R.layout.item_network_failed -> NetworkStateItemViewHolder.create(parent, viewType, retryCallback)
            R.layout.item_network_state -> NetworkStateItemViewHolder.create(parent, viewType, retryCallback)
            else -> ViewHolderRecyclerView(mLayoutInflater.inflate(getLayoutId(viewType), parent, false), itemClickListener, itemLongClickListener)
        }
    }

    override fun onBindViewHolder(holder: ViewHolderRecyclerView, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_network_empty -> {
                (holder as NetworkStateItemViewHolder).bindTo(refreshState)
            }
            R.layout.item_network_state -> {
                (holder as NetworkStateItemViewHolder).bindTo(networkState)
            }
            R.layout.item_network_failed -> {
                (holder as NetworkStateItemViewHolder).bindTo(refreshState)
            }
            else -> {
                getItem(position)?.let {
                    onBindView(it, holder, position)
                }
            }
        }


    }

    protected var itemClickListener: ((Int) -> Unit)? = null
    protected var itemLongClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(itemClickListener: ((Int) -> Unit)) {
        this.itemClickListener = itemClickListener
    }

    fun setOnItemLongClickListener(itemLongClickListener: ((Int) -> Unit)) {
        this.itemLongClickListener = itemLongClickListener
    }
}