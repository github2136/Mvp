package com.github2136.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by yb on 2018/9/21.
 */
public abstract class BaseFragment<P extends BaseMVPPresenter> extends Fragment implements IBaseMVPView {
    protected final String TAG = this.getClass().getName();
    private boolean isInit;
    protected P mPresenter;
    protected View mRootView;
    protected Context mContext;
    protected Handler mHandler;
    protected Toast mToast;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        attach(context);
    }

    private void attach(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getViewResId(), container, false);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isInit) {
            isInit = true;
            mPresenter = getPresenter();
            initData(savedInstanceState);
        }
    }

    @Override
    public void onDestroyView() {
        if (isInit) {
            cancelRequest();
        }
        super.onDestroyView();
    }

    public void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void showToast(@StringRes int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, resId, Toast.LENGTH_SHORT);
        }
        mToast.setText(resId);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void showToastLong(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        }
        mToast.setText(msg);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

    public void showToastLong(@StringRes int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, resId, Toast.LENGTH_LONG);
        }
        mToast.setText(resId);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Handler
    ///////////////////////////////////////////////////////////////////////////
    static class Handler extends android.os.Handler {
        WeakReference<BaseFragment> weakReference;

        Handler(BaseFragment fragment) {
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BaseFragment fragment = weakReference.get();
            if (fragment != null) {
                fragment.handleMessage(msg);
            }
        }
    }

    protected void handleMessage(Message msg) { }

    protected abstract P getPresenter();

    //布局ID
    protected abstract int getViewResId();

    //初始化
    protected abstract void initData(Bundle savedInstanceState);

    //取消请求
    public void cancelRequest() {}
}
