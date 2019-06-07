package com.github2136.mvp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import com.github2136.base.BaseActivity
import com.github2136.base.download.DownLoadUtil
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

    val download by lazy {
        DownLoadUtil(application)
    }

    override fun initData(savedInstanceState: Bundle?) {
        mPresenter.get()
        btn_retry.setOnClickListener(this)
        btn_list.setOnClickListener(this)
        btn_list2.setOnClickListener(this)
        btn_download.setOnClickListener(this)
        btn_stop.setOnClickListener(this)
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
            R.id.btn_download -> {

//                download
//                        .downloadFile("http://gdown.baidu.com/data/wisegame/df65a597122796a4/weixin_821.apk",
//                                FileUtil.getExternalStorageRootPath() + File.separator + "a.apk")
                download
                        .downloadFile("http://img1.gamersky.com/image2017/04/20170407_ljt_220_10/gamersky_04origin_07_20174715391B4.jpg",
                                FileUtil.getExternalStorageRootPath() + File.separator + "000.jpg") { path, progress ->
//                            download.deletePath("http://img1.gamersky.com/image2017/04/20170407_ljt_220_10/gamersky_04origin_07_20174715391B4.jpg")
                        }
//                download
//                        .downloadFile("https://pic3.zhimg.com/v2-49226022c89d0c3fb7344588aede7fc7_r.jpg",
//                                FileUtil.getExternalStorageRootPath()+File.separator+"a.jpg")

            }
            R.id.btn_stop -> {
                download.stop = true
            }
        }
    }
}