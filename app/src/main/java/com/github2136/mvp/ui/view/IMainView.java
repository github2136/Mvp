package com.github2136.mvp.ui.view;

import com.github2136.base.IBaseMVPView;

/**
 * Created by yb on 2018/10/23.
 **/
public interface IMainView extends IBaseMVPView {
    void getSuccessful(String msg);

    void getFailure(String msg);
}
