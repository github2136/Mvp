package com.github2136.mvp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.github2136.base.BaseActivity
import com.github2136.base.download.DownloadTask
import com.github2136.base.download.DownloadUtil
import com.github2136.mvp.R
import com.github2136.mvp.presenter.MainPresenter
import com.github2136.util.FileUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

/**
 * Created by yb on 2018/11/2.
 */
class MainActivity : BaseActivity<MainPresenter>(), View.OnClickListener {
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

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
                download.getPathExists("https://pkg.zhimg.com/zhihu/futureve-app-zhihuwap-ca40fb89fbd4fb3a3884429e1c897fe2-release-5.45.0(1266).apk")
                download
                    .download(
                        "https://pkg.zhimg.com/zhihu/futureve-app-zhihuwap-ca40fb89fbd4fb3a3884429e1c897fe2-release-5.45.0(1266).apk",
                        FileUtil.getExternalStorageRootPath() + File.separator + "abc.apk") { state, progress, path ->
                        when (state) {
                            DownloadTask.STATE_SUCCESS -> {
                                Log.e("download1", "success $path")
                            }
                            DownloadTask.STATE_FAIL -> {
                                Log.e("download1", "fail")
                            }
                            DownloadTask.STATE_DOWNLOAD -> {
                                pb1.progress = progress
                                Log.e("download1", "download $progress")
                            }
                            DownloadTask.STATE_STOP -> {
                                Log.e("download1", "stop")
                            }
                        }
                    }
            }
            R.id.btn_stop1 -> {
                download.stop("https://pkg.zhimg.com/zhihu/futureve-app-zhihuwap-ca40fb89fbd4fb3a3884429e1c897fe2-release-5.45.0(1266).apk")
            }
            R.id.btn_download2 -> {
                download.getPathExists("https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk")
                download
                    .download(
                        "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk",
                        FileUtil.getExternalStorageRootPath() + File.separator + "def.apk") { state, progress, path ->
                        when (state) {
                            DownloadTask.STATE_SUCCESS -> {
                                Log.e("download2", "success $path")
                            }
                            DownloadTask.STATE_FAIL -> {
                                Log.e("download2", "fail")
                            }
                            DownloadTask.STATE_DOWNLOAD -> {
                                pb2.progress = progress
                                Log.e("download2", "download $progress")
                            }
                            DownloadTask.STATE_STOP -> {
                                Log.e("download2", "stop")
                            }
                        }
                    }
            }
            R.id.btn_stop2 -> {
                download.stop("https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk")
            }
        }
    }
}