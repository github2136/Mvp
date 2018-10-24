package com.github2136.mvp.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.github2136.base.BaseActivity;

import com.github2136.mvp.R;
import com.github2136.mvp.presenter.MainPresenter;
import com.github2136.mvp.ui.view.IMainView;

public class MainActivity extends BaseActivity<MainPresenter> implements IMainView {
    TextView tvTxt;

    @Override
    protected MainPresenter getPresenter() {
        return new MainPresenter(this, this);
    }

    @Override
    protected int getViewResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        tvTxt = (TextView) findViewById(R.id.tv_txt);
        mPresenter.get();
    }

    @Override
    public void getSuccessful(String msg) {
        tvTxt.setText(msg);
    }

    @Override
    public void getFailure(String msg) {
        tvTxt.setText(msg);
    }
}
