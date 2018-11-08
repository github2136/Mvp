package com.github2136.mvp.presenter;

import com.github2136.base.BaseMVPPresenter;
import com.github2136.mvp.model.UserModel;
import com.github2136.mvp.ui.activity.MainActivity;

import okhttp3.Response;

/**
 *
 **/
public class MainPresenter extends BaseMVPPresenter<MainActivity> {
    private UserModel mUserModel;

    public MainPresenter(MainActivity mainActivity) {
        super(mainActivity);
        mUserModel = new UserModel(mainActivity.getApplicationContext(), mainActivity.toString());
    }

    public void get() {
        mUserModel.get(new HttpCallback() {

            @Override
            protected void onFailure(MainActivity mainActivity, String str) {
                mainActivity.getFailure(str);
            }

            @Override
            protected void onResponse(MainActivity mainActivity, Response response, String bodyStr) {
                if (response.isSuccessful()) {
                    mainActivity.getSuccessful(bodyStr);
                } else {
                    mainActivity.getFailure(failedStr);
                }
            }
        });
    }

    @Override
    public void cancelRequest() {
        mUserModel.cancelRequest();
    }
}
