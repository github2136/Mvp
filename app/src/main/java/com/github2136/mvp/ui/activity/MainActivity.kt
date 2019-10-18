package com.github2136.mvp.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.collection.ArrayMap
import androidx.lifecycle.Observer
import com.github2136.base.BaseActivity
import com.github2136.base.download.DownloadTask
import com.github2136.base.download.DownloadUtil
import com.github2136.mvp.R
import com.github2136.mvp.presenter.MainPresenter
import com.github2136.util.FileUtil
import com.github2136.util.PermissionUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

/**
 * Created by yb on 2018/11/2.
 */
class MainActivity : BaseActivity<MainPresenter>(), View.OnClickListener {
    override fun getLayoutId()=R.layout.activity_main

    private val permissionUtil by lazy { PermissionUtil(this) }
    val download by lazy { DownloadUtil.getInstance(application) }

    override fun initData(savedInstanceState: Bundle?) {
        mPresenter.get()
        btn_retry.setOnClickListener(this)
        btn_list.setOnClickListener(this)
        btn_list2.setOnClickListener(this)
        btn_download1.setOnClickListener(this)
        btn_download2.setOnClickListener(this)
        btn_stop1.setOnClickListener(this)
        btn_stop2.setOnClickListener(this)
        val permission = ArrayMap<String, String>()
        permission[Manifest.permission.WRITE_EXTERNAL_STORAGE] = "文件写入"
        permissionUtil.getPermission(permission) {

        }
    }

    override fun initObserve() {
        mPresenter.ldGet.observe(this, Observer { t ->
            if (t?.isSuccess == true) {
                tv_txt.text = t.str
            } else {
                tv_txt.text = mPresenter.failedStr
            }
        })
    }

    val downStr1 = "http://125.124.92.69/upload/app/fireservicesunit.apk"
    //    val downStr1="https://dldir1.qq.com/music/clntupate/QQMusic_YQQFloatLayer.exe"
    val downStr2 = "https://d1.music.126.net/dmusic/4ec1/201978183952/cloudmusicsetup2.5.5.197810.exe"

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_retry -> {
                mPresenter.get()
            }
            R.id.btn_list -> {
                startActivity(Intent(this, ListActivity::class.java))
            }
            R.id.btn_list2 -> {
                startActivity(Intent(this, DBListActivity::class.java))
            }
            R.id.btn_download1 -> {

                download.getPathExists(downStr1)
                download
                    .download(
                        downStr1,
                        FileUtil.getExternalStorageRootPath() + File.separator + "abc.exe") { state, progress, path, error ->
                        when (state) {
                            DownloadUtil.STATE_SUCCESS -> {
                                Log.e("download1", "success $path")
                            }
                            DownloadUtil.STATE_FAIL -> {
                                Log.e("download1", "fail $error")
                            }
                            DownloadUtil.STATE_DOWNLOAD -> {
                                pb1.progress = progress
                                Log.e("download1", "download $progress")
                            }
                            DownloadUtil.STATE_STOP -> {
                                Log.e("download1", "stop")
                            }
                        }
                    }
            }
            R.id.btn_stop1 -> {
                download.stop(downStr1)
            }
            R.id.btn_download2 -> {
                download.getPathExists(downStr2)
                download
                    .download(
                        downStr2,
                        FileUtil.getExternalStorageRootPath() + File.separator + "def.exe") { state, progress, path, error ->
                        when (state) {
                            DownloadUtil.STATE_SUCCESS -> {
                                Log.e("download2", "success $path")
                            }
                            DownloadUtil.STATE_FAIL -> {
                                Log.e("download1", "fail $error")
                            }
                            DownloadUtil.STATE_DOWNLOAD -> {
                                pb2.progress = progress
                                Log.e("download2", "download $progress")
                            }
                            DownloadUtil.STATE_STOP -> {
                                Log.e("download2", "stop")
                            }
                        }
                    }
            }
            R.id.btn_stop2 -> {
                download.stop(downStr2)
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        permissionUtil.onRestart()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}