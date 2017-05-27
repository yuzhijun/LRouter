package com.lenovohit.lrouter_api.base;

import android.app.Application;
import android.content.res.Configuration;

import com.lenovohit.lrouter_api.core.LocalRouter;
import com.lenovohit.lrouter_api.utils.ILRLogger;
import com.lenovohit.lrouter_api.utils.LRLoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 程序的初始化入口
 * Created by yuzhijun on 2017/5/27.
 */
public abstract class LRouterAppcation extends Application{
    private static final String TAG = "LRouterApplicaton";
    private static final String PROCESS_NAME = "com.lenovohit.lrouter.RemoteRouter";
    //保存application实例
    private static LRouterAppcation mInstance;
    //同个进程中业务module中类application的总和
    private ArrayList<AnologyApplicationWrapper> mAnologyApplications;
    //所有进程对应的类application的HashMap
    private HashMap<String,ArrayList<AnologyApplicationWrapper>> mAnologyApplicationHashMap;

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
                    initLocalRouter();
                    //注册所有的本地路由服务,主要是用于跨进程通讯
                    initLocalRouterService();

                }
            });
        }catch (Exception e){
            LRLoggerFactory.getLRLogger(TAG).log("LRouterApplicaion初始化失败", ILRLogger.LogLevel.ERROR);
        }
    }



    private void initLocalRouter() {
        LocalRouter.getInstance(this);
        mAnologyApplicationHashMap = new HashMap<>();
    }

    private void initLocalRouterService() {

    }

    /**
     * 注册所有的LocalRouterService
     * */
    public abstract void regiterLocalRouterService();
    /**
     * 注册所有的类application
     * */
    protected abstract void regiterAnologyApplication();

    public abstract boolean needMultipleProcess();

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
