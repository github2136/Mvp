package com.github2136.base.download

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

/**
 * Created by YB on 2019/6/6
 */
class DownLoadDao(context: Context) {
    private val dbHelper = DBHelper(context)
    private val db: SQLiteDatabase = dbHelper.writableDatabase
    fun install(file: DownLoadFile): Long {
        db.beginTransaction()
        val id: Long
        try {
            val contentValues = ContentValues()
            contentValues.put(COL_FILE_URL, file.fileUrl)
            contentValues.put(COL_FILE_PATH, file.filePath)
            contentValues.put(COL_FILE_SIZE, file.fileSize)
            contentValues.put(COL_FILE_TOTAL, file.fileTotal)
            contentValues.put(COL_COMPLETE, if (file.complete) 1 else 0)
            id = db.insert(TAB_NAME, null, contentValues)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return id
    }

    fun update(file: DownLoadFile): Boolean {
        db.beginTransaction()
        val id: Int
        try {
            val contentValues = ContentValues()
            contentValues.put(COL_FILE_PATH, file.filePath)
            contentValues.put(COL_FILE_SIZE, file.fileSize)
            contentValues.put(COL_FILE_TOTAL, file.fileTotal)
            contentValues.put(COL_COMPLETE, if (file.complete) 1 else 0)
            id = db.update(TAB_NAME, contentValues, "$COL_FILE_URL = ?", arrayOf(file.fileUrl))
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return id > 0
    }

    fun get(id: Long): DownLoadFile? {
        val cursor = db.query(TAB_NAME, COLS, "$COL_ID = ?", arrayOf(id.toString()), null, null, null)
        val file: DownLoadFile?
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(COL_ID)
            val fileUrlIndex = cursor.getColumnIndex(COL_FILE_URL)
            val filePathIndex = cursor.getColumnIndex(COL_FILE_PATH)
            val fileSizeIndex = cursor.getColumnIndex(COL_FILE_SIZE)
            val fileTotalIndex = cursor.getColumnIndex(COL_FILE_TOTAL)
            val completeIndex = cursor.getColumnIndex(COL_COMPLETE)

            file = DownLoadFile(
                    cursor.getLong(idIndex),
                    cursor.getString(fileUrlIndex),
                    cursor.getString(filePathIndex),
                    cursor.getLong(fileSizeIndex),
                    cursor.getLong(fileTotalIndex),
                    cursor.getInt(completeIndex) == 1
            )
        } else {
            file = null
        }
        cursor.close()
        return file
    }

    fun get(fileUrl: String): DownLoadFile? {
        val cursor = db.query(TAB_NAME, COLS, "$COL_FILE_URL = ?", arrayOf(fileUrl), null, null, null)
        val file: DownLoadFile?
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(COL_ID)
            val fileUrlIndex = cursor.getColumnIndex(COL_FILE_URL)
            val filePathIndex = cursor.getColumnIndex(COL_FILE_PATH)
            val fileSizeIndex = cursor.getColumnIndex(COL_FILE_SIZE)
            val fileTotalIndex = cursor.getColumnIndex(COL_FILE_TOTAL)
            val completeIndex = cursor.getColumnIndex(COL_COMPLETE)

            file = DownLoadFile(
                    cursor.getLong(idIndex),
                    cursor.getString(fileUrlIndex),
                    cursor.getString(filePathIndex),
                    cursor.getLong(fileSizeIndex),
                    cursor.getLong(fileTotalIndex),
                    cursor.getInt(completeIndex) == 1
            )
        } else {
            file = null
        }
        cursor.close()
        return file
    }

    fun delete(fileUrl: String) {
        db.delete(TAB_NAME, "$COL_FILE_URL = ?", arrayOf(fileUrl))
    }

    companion object {
        const val TAB_NAME = "DownLoadFile"
        const val COL_ID = "id"
        const val COL_FILE_URL = "fileUrl"
        const val COL_FILE_PATH = "filePath"
        const val COL_FILE_SIZE = "fileSize"
        const val COL_FILE_TOTAL = "fileTotal"
        const val COL_COMPLETE = "complete"
        val COLS = arrayOf(COL_ID, COL_FILE_URL, COL_FILE_PATH, COL_FILE_SIZE, COL_FILE_TOTAL, COL_COMPLETE)
    }
}