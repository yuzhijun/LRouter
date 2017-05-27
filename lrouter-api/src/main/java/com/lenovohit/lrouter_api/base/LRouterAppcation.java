package com.lenovohit.lrouter_api.base;

import android.app.Application;
import android.content.res.Configuration;

import com.lenovohit.lrouter_api.utils.ILRLogger;
import com.lenovohit.lrouter_api.utils.LRLoggerFactory;

/**
 * 程序的初始化入口
 * Created by yuzhijun on 2017/5/27.
 */
public abstract class LRouterAppcation extends Application{
    private static final String TAG = "LRouterApplicaton";
    //保存application实例
    private static LRouterAppcation mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        try{
            //启动新线程进行初始化避免加载等待时间过长
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //初始化LocalRouter

                }
            });
        }catch (Exception e){
            LRLoggerFactory.getLRLogger(TAG).log("LRouterApplicaion初始化失败", ILRLogger.LogLevel.ERROR);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static LRouterAppcation getmInstance() {
        return mInstance;
    }
}
