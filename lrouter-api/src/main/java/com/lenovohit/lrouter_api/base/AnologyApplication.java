package com.lenovohit.lrouter_api.base;

import android.content.res.Configuration;
import android.support.annotation.NonNull;

/**
 * 类application,用于module继承并拥有跟application相同的生命周期
 * Created by yuzhijun on 2017/5/27.
 */
public class AnologyApplication {

    protected LRouterAppcation mApplication;

    public AnologyApplication() {
    }

    public void setApplication(@NonNull LRouterAppcation application) {
        mApplication = application;
    }

    public void onCreate() {
    }

    public void onTerminate() {
    }

    public void onLowMemory() {
    }

    public void onTrimMemory(int level) {
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }
}
