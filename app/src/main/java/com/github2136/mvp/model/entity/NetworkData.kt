package com.github2136.mvp.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by yb on 2018/11/20.
 */
@Entity
data class NetworkData(
        @PrimaryKey
        val objectId: String,
        val name: String,
        val rowNumber: Int
)