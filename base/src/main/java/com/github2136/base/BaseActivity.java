package com.github2136.base;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 *
 */
public abstract class BaseActivity<P extends BaseMVPPresenter> extends AppCompatActivity implements IBaseMVPView {
    protected P mPresenter;
    protected final String TAG = this.getClass().getName();
    protected BaseApplication mApp;
    protected BaseActivity mActivity;
    protected Handler mHandler;
    protected Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (BaseApplication) getApplication();
        mApp.addActivity(this);
        mActivity = this;
        setContentView(getViewResId());
        mHandler = new Handler(this);
        mPresenter = getPresenter();
        initData(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        cancelRequest();
        mApp.removeActivity(this);
        super.onDestroy();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Handler
    ///////////////////////////////////////////////////////////////////////////
    static class Handler extends android.os.Handler {
        WeakReference<BaseActivity> weakReference;

        Handler(BaseActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseActivity activity = weakReference.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    public void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void showToast(@StringRes int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
        }
        mToast.setText(resId);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void showToastLong(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        }
        mToast.setText(msg);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

    public void showToastLong(@StringRes int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(this, resId, Toast.LENGTH_LONG);
        }
        mToast.setText(resId);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

    // 显示进度框
    public void showProgressDialog() { }

    // 显示进度框
    public void showProgressDialog(@StringRes int resId) { }

    // 显示进度框
    public void showProgressDialog(String msg) {}

    // 关闭进度框
    public void dismissDialog() {}

    protected void handleMessage(Message msg) { }

    //获得presenter
    protected abstract P getPresenter();

    //布局ID
    protected abstract int getViewResId();

    //初始化
    protected abstract void initData(Bundle savedInstanceState);

    //取消请求
    public void cancelRequest() {}
}
