package com.github2136.mvp.presenter;

import android.support.v7.app.AppCompatActivity;

import com.github2136.base.BaseMVPPresenter;

import com.github2136.mvp.ui.view.IMainView;

/**
 * Created by yb on 2018/10/23.
 **/
public class MainPresenter extends BaseMVPPresenter <IMainView>{
    public MainPresenter(AppCompatActivity activity, IMainView view) {
        super(activity, view);
    }

    @Override
    public void cancelRequest() {

    }
}
