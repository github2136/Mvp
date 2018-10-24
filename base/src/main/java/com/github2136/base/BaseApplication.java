package com.github2136.base;

import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatActivity;

import com.github2136.util.CrashHandler;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by yubin on 2016/2/23.
 */
public class BaseApplication extends MultiDexApplication {
    private ArrayList<AppCompatActivity> mActivitys;

    @Override
    public void onCreate() {
        super.onCreate();
        mActivitys = new ArrayList<>();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.setCustomCrashHanler(this, new CrashHandler.CrashHandlerCallback() {
            @Override
            public void finishAll() {
                BaseApplication.this.finishAll();
            }

            @Override
            public void submitLog(Map<String, String> deviceInfo, String exception) {

            }
        });
    }


    public void addActivity(AppCompatActivity act) {
        this.mActivitys.add(act);
    }

    public void removeActivity(AppCompatActivity act) {
        if (mActivitys.contains(act)) {
            mActivitys.remove(act);
        }
    }

    public void finishAll() {
        for (int i = 0, len = mActivitys.size(); i < len; i++) {
            AppCompatActivity act = mActivitys.get(i);
            act.finish();
        }
    }
}