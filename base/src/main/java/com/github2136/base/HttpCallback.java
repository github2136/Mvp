package com.github2136.base;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 *
 */
public interface HttpCallback {
    void onFailure(Call call, Exception e);

    void onResponse(Call call, Response response, String bodyStr);
}
