package com.github2136.mvp.ui.activity;

import android.os.Bundle;

import com.github2136.base.BaseActivity;

import github2136.com.mvp.R;
import com.github2136.mvp.presenter.MainPresenter;
import com.github2136.mvp.ui.view.IMainView;

public class MainActivity extends BaseActivity<MainPresenter> implements IMainView {

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

    }
}
