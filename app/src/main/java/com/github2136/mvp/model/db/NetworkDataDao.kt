package com.github2136.mvp.model.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github2136.mvp.model.entity.NetworkData

/**
 * Created by yb on 2018/11/23.
 */
@Dao
interface NetworkDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts : List<NetworkData>)

    @Query("SELECT * FROM networkdata order by rowNumber")
    fun getAllData(): DataSource.Factory<Int, NetworkData>
}