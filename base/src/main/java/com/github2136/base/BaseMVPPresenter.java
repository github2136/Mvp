package com.github2136.base;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.github2136.util.JsonUtil;
import com.github2136.util.SPUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 *
 */
public abstract class BaseMVPPresenter<V extends IBaseMVPView> {
    protected WeakReference<V> vWeakReference;
    protected final String failedStr = "无法连接服务器";
    protected JsonUtil mJsonUtil;
    protected SPUtil mSpUtil;
    protected Handler mHandler;

    public BaseMVPPresenter(V v) {
        vWeakReference = new WeakReference<>(v);
        if (v instanceof AppCompatActivity) {
            mSpUtil = SPUtil.getInstance(((AppCompatActivity) v));
        } else if (v instanceof Fragment) {
            mSpUtil = SPUtil.getInstance(((Fragment) v).getContext());
        }
        initPresenter();
    }

    private void initPresenter() {
        mJsonUtil = JsonUtil.getInstance();
        mHandler = new Handler(Looper.getMainLooper());
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
    protected boolean isViewVisible() {
        V v = vWeakReference.get();
        if (v != null) {
            if (v instanceof AppCompatActivity) {
                AppCompatActivity a = ((AppCompatActivity) v);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    return !(a.isFinishing() || a.isDestroyed());
                } else {
                    return !a.isFinishing();
                }
            } else if (v instanceof Fragment) {
                Fragment f = ((Fragment) v);
                return !f.isDetached();
            }
        }
        return false;
    }

    public abstract class HttpCallback implements Callback {
        @Override
        public void onResponse(final Call call, final Response response) throws IOException {
            final String bodyStr = response.body().string();
            if (isViewVisible()) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        V v = vWeakReference.get();
                        if (v != null) {
                            onResponse(v, response, bodyStr);
                        }
                    }
                });
            }
        }

        @Override
        public void onFailure(final Call call, final IOException e) {
            if (isViewVisible()) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        V v = vWeakReference.get();
                        if (v != null) {
                            onFailure(v, failedStr);
                        }
                    }
                });
            }
        }

        protected abstract void onFailure(V v, String str);

        protected abstract void onResponse(V v, Response response, String bodyStr);
    }

    //取消请求
    public abstract void cancelRequest();
}
