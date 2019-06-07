package com.github2136.base.download

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by YB on 2019/6/6
 */
class DBHelper(context: Context) : SQLiteOpenHelper(context, "DownloadDB.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.apply {
            execSQL("CREATE TABLE IF NOT EXISTS ${DownLoadDao.TAB_NAME}(${DownLoadDao.COL_ID} INTEGER PRIMARY KEY AUTOINCREMENT, ${DownLoadDao.COL_FILE_URL} VARCHAR, ${DownLoadDao.COL_FILE_PATH} VARCHAR, ${DownLoadDao.COL_FILE_SIZE} LONG, ${DownLoadDao.COL_FILE_TOTAL} LONG,${DownLoadDao.COL_COMPLETE} INTEGER)")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.apply {
            execSQL("DROP TABLE ${DownLoadDao.TAB_NAME}")
            onCreate(this)
        }
    }
}