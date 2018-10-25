package com.github2136.base;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.github2136.util.JsonUtil;
import com.github2136.util.SPUtil;

import java.io.IOException;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *
 */
public abstract class BaseMVPPresenter<V extends IBaseMVPView> {
    protected V mView;
    protected AppCompatActivity mActivity;
    protected Fragment mFragment;
    protected final String failedStr = "无法连接服务器";
    protected JsonUtil mJsonUtil;
    protected SPUtil mSpUtil;
    protected Handler mHandler;

    public BaseMVPPresenter(AppCompatActivity activity, V view) {
        mActivity = activity;
        initPresenter(view);
    }

    public BaseMVPPresenter(Fragment fragment, V view) {
        mFragment = fragment;
        initPresenter(view);
    }

    private void initPresenter(V view) {
        this.mView = view;
        mJsonUtil = JsonUtil.getInstance();
        mHandler = new Handler(Looper.getMainLooper());
        if (mActivity == null) {
            mSpUtil = SPUtil.getInstance(mFragment.getContext());
        } else {
            mSpUtil = SPUtil.getInstance(mActivity);
        }
    }

    public String getSPString(String key) {
        return mSpUtil.getString(key);
    }

    public boolean getSPBoolean(String key) {
        return mSpUtil.getBoolean(key);
    }

    public float getSPFloat(String key) {
        return mSpUtil.getFloat(key);
    }

    public int getSPInt(String key) {
        return mSpUtil.getInt(key);
    }

    public long getSPLong(String key) {
        return mSpUtil.getLong(key);
    }

    public Set<String> getSPStringSet(String key) {
        return mSpUtil.getStringSet(key);
    }

    public void setSPValue(String key, String strVal) {
        mSpUtil.edit().putValue(key, strVal).apply();
    }

    public void setSPValue(String key, boolean boolVal) {
        mSpUtil.edit().putValue(key, boolVal).apply();
    }

    public void setSPValue(String key, float floatVal) {
        mSpUtil.edit().putValue(key, floatVal).apply();
    }

    public void setSPValue(String key, int intVal) {
        mSpUtil.edit().putValue(key, intVal).apply();
    }

    public void setSPValue(String key, Set<String> setVal) {
        mSpUtil.edit().putValue(key, setVal).apply();
    }

    public void postMain(Runnable runnable) {
        mHandler.post(runnable);
    }

    /**
     * 界面是否存在
     *
     * @return
     */
    protected boolean isViewGone() {
        if (mFragment != null) {
            return mFragment.isDetached();
        } else if (mActivity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return mActivity.isFinishing() || mActivity.isDestroyed();
            } else {
                return mActivity.isFinishing();
            }
        } else {
            return false;
        }
    }

    public abstract class HttpCallback implements Callback {
        @Override
        public void onResponse(final Call call, final Response response) throws IOException {
            final String bodyStr = response.body().string();
            if (!isViewGone()) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onResponse(call, response, bodyStr);
                    }
                });
            }
        }

        @Override
        public void onFailure(final Call call, final IOException e) {
            if (!isViewGone()) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onFailure(call, e, failedStr);
                    }
                });
            }
        }

        protected abstract void onFailure(Call call, IOException e, String str);

        protected abstract void onResponse(Call call, Response response, String bodyStr);
    }

    //取消请求
    public abstract void cancelRequest();
}
