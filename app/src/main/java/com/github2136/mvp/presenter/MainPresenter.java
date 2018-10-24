package com.github2136.mvp.presenter;

import android.support.v7.app.AppCompatActivity;

import com.github2136.base.BaseMVPPresenter;

import com.github2136.base.HttpCallback;
import com.github2136.mvp.model.UserModel;
import com.github2136.mvp.ui.view.IMainView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by yb on 2018/10/23.
 **/
public class MainPresenter extends BaseMVPPresenter<IMainView> {
    private UserModel mUserModel;

    public MainPresenter(AppCompatActivity activity, IMainView view) {
        super(activity, view);
        mUserModel = new UserModel(activity, activity.toString());
    }

    public void get() {
        mUserModel.get(new HttpCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                if (!isViewGone()) {
                    mView.getFailure(failedStr);
                }
            }

            @Override
            public void onResponse(Call call, Response response, String bodyStr) {
                if (!isViewGone()) {
                    if (response.isSuccessful()) {
                        mView.getSuccessful(bodyStr);
                    } else {
                        mView.getFailure(failedStr);
                    }
                }
            }
        });
    }

    @Override
    public void cancelRequest() {
        mUserModel.cancelRequest();
    }
}
