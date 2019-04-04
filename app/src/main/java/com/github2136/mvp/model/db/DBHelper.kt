package com.github2136.mvp.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github2136.mvp.model.entity.NetworkData

/**
 * Created by yb on 2018/11/12.
 */
@Database(entities = [ NetworkData::class], version = 1)
abstract class DBHelper : RoomDatabase() {
    abstract fun networkDataDao(): NetworkDataDao

    companion object {
        @Volatile
        private var instance: DBHelper? = null

        fun getInstance(context: Context): DBHelper {
            if (instance == null) {
                synchronized(DBHelper::class) {
                    if (instance == null) {
                        val migration1 = object : Migration(1, 2) {
                            override fun migrate(database: SupportSQLiteDatabase) {
                                database.execSQL("")
                            }
                        }
                        instance = Room.databaseBuilder(context, DBHelper::class.java, "mvp.db")
                                .addMigrations(migration1)
                                .allowMainThreadQueries()
                                .build()
                    }
                }
            }
            return instance!!
        }
    }
}