/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github2136.base.paged

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.github2136.base.R
import com.github2136.base.ViewHolderRecyclerView

/**
 * A View Holder that can display a loading or have click action.
 * It is used to show the network state of paging.
 */
class NetworkStateItemViewHolder(view: View,
                                 private val retryCallback: () -> Unit)
    : ViewHolderRecyclerView(view) {
    private val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
    private val errorMsg = view.findViewById<TextView>(R.id.error_msg)
    private val retry = view.findViewById<View>(R.id.retry)

    init {
        retry?.setOnClickListener {
            retryCallback()
        }
    }

    fun bindTo(networkState: NetworkState?) {
        progressBar?.visibility = toVisibility(networkState?.status == Status.RUNNING)
        retry?.visibility = toVisibility(networkState?.status == Status.FAILED)
        errorMsg?.visibility = toVisibility(networkState?.msg != null)

        errorMsg?.text = networkState?.msg

        if (networkState?.status == Status.SUCCESS) {
            errorMsg?.visibility = View.VISIBLE
            errorMsg?.text = "已加载所有数据"
        }
    }

    companion object {
        fun create(parent: ViewGroup, layoutId: Int, retryCallback: () -> Unit): NetworkStateItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(layoutId, parent, false)
            return NetworkStateItemViewHolder(view, retryCallback)
        }

        fun toVisibility(constraint: Boolean): Int {
            return if (constraint) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}
