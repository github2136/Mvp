package com.github2136.base;

import android.content.Context;
import android.util.ArrayMap;

import com.github2136.util.JsonUtil;
import com.github2136.util.SPUtil;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 *
 */
public abstract class BaseMVPModel {
    protected OkHttpClient client;
    protected Context mContext;

    protected String mTag;
    protected SPUtil mSpUtil;
    protected JsonUtil mJsonUtil;

    public BaseMVPModel(Context context, String tag) {
        mContext = context;
        mTag = tag;
        initMode();
    }

    private void initMode() {
        mJsonUtil = JsonUtil.getInstance();
        mSpUtil = SPUtil.getInstance(mContext);
        client = new OkHttpClient().newBuilder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();

    }

    protected void httpGet(final String url,
                           final String method,
                           final ArrayMap<String, Object> params,
                           Callback callback) {
        StringBuilder urlSb = new StringBuilder(url + method);
        if (params != null && !params.isEmpty()) {
            urlSb.append("?");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                urlSb.append(entry.getKey());
                urlSb.append("=");
                urlSb.append(entry.getValue());
                urlSb.append("&");
            }
            urlSb.deleteCharAt(urlSb.length() - 1);
        }
        Request request = new Request.Builder()
                .url(urlSb.toString())
                .tag(mTag)
                .build();
        client.newCall(request).enqueue(callback);
    }

    protected void httpPost(final String url,
                            final String method,
                            final ArrayMap<String, Object> params,
                            Callback callback) {

        MediaType JSON = MediaType.parse("application/json");
        String json = "";
        if (params != null && !params.isEmpty()) {
            json = JsonUtil.getInstance().getGson().toJson(params);
        }
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url + method)
                .tag(mTag)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void cancelRequest() {
        for (Call call : client.dispatcher().queuedCalls()) {
            if (call.request().tag().equals(mTag))
                call.cancel();
        }
        for (Call call : client.dispatcher().runningCalls()) {
            if (call.request().tag().equals(mTag))
                call.cancel();
        }
    }
}