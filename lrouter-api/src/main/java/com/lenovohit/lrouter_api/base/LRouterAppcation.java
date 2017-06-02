package com.lenovohit.lrouter_api.base;

import android.app.Application;
import android.content.res.Configuration;

import com.lenovohit.lrouter_api.core.LocalRouter;
import com.lenovohit.lrouter_api.core.RemoteRouter;
import com.lenovohit.lrouter_api.function.LRouterAnologyApplication;
import com.lenovohit.lrouter_api.utils.ILRLogger;
import com.lenovohit.lrouter_api.utils.LRLoggerFactory;
import com.lenovohit.lrouter_api.utils.ProcessUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * 程序的初始化入口
 * Created by yuzhijun on 2017/5/27.
 */
public abstract class LRouterAppcation extends Application{
    private static final String TAG = "LRouterApplicaton";
    private static final int PRIORITY = 9999;//给框架的类application一个高优先级
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
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
                    //初始化LocalRouter
                    initLocalRouter();
                    //注册所有的本地路由服务,主要是用于跨进程通讯
                    initLocalRouterService();
                    //注册所有的module的类application
                    regiterAnologyApplication();
                    //因为多进程的程序application会被执行多次,所以根据进程获取各类application并设置application实例
                    setAnologyApplicaiton();
                    //调用各类application的生命周期onCreate
                    invokeOnCreate();
//                }
//            }).start();
        }catch (Exception e){
            LRLoggerFactory.getLRLogger(TAG).log("LRouterApplicaion初始化失败", ILRLogger.LogLevel.ERROR);
        }
    }

    private void initLocalRouter() {
        LocalRouter.getInstance(this);
        mAnologyApplicationHashMap = new HashMap<>();
    }

    private void initLocalRouterService() {
        //如果是允许多进程才需要注册框架类application类
        if (needMultipleProcess()){
            registerAnologyApplication(RemoteRouter.PROCESS_NAME,PRIORITY,LRouterAnologyApplication.class);
        }
    }

    /**
     * 注册所有的module的类applicatiion
     * @return true 注册成功
     * @return false 已经注册
     * */
    protected boolean registerAnologyApplication(String processName,int priority,Class<? extends  AnologyApplication> anologyApplication){
        boolean result = false;
        if (null != mAnologyApplicationHashMap){
            ArrayList<AnologyApplicationWrapper> anologyApplicationWrappers = mAnologyApplicationHashMap.get(processName);

            if (null == anologyApplicationWrappers){
                anologyApplicationWrappers = new ArrayList<>();
                mAnologyApplicationHashMap.put(processName,anologyApplicationWrappers);
            }

            if (anologyApplicationWrappers.size() > 0){
                for (AnologyApplicationWrapper anologyApplicationWrapper : anologyApplicationWrappers){
                    if (anologyApplication.getName().equals(anologyApplicationWrapper.anologyApplicationClass.getName())){
                        result = false;//类application已经注册
                        return result;
                    }
                }
            }

            //未注册则注册
            AnologyApplicationWrapper anologyApplicationWrapper = new AnologyApplicationWrapper(priority,anologyApplication);
            anologyApplicationWrappers.add(anologyApplicationWrapper);
            result = true;
        }
        return result;
    }

    /**
     * 实例化所有的类application并设置application实例
     * */
    private void setAnologyApplicaiton(){
        if (null != mAnologyApplicationHashMap){
            mAnologyApplications = mAnologyApplicationHashMap.get(ProcessUtil.getProcessName(this, ProcessUtil.getMyProcessId()));
        }
            if (null != mAnologyApplications && mAnologyApplications.size() > 0){
                try{
                    //根据优先级进行排序
                    Collections.sort(mAnologyApplications);
                    for (AnologyApplicationWrapper anologyApplicationWrapper : mAnologyApplications){
                        anologyApplicationWrapper.instance = anologyApplicationWrapper.anologyApplicationClass.newInstance();
                        if (null != anologyApplicationWrapper.instance){
                            anologyApplicationWrapper.instance.setApplication(this);
                        }
                    }
                }catch (Exception e){
                    LRLoggerFactory.getLRLogger(TAG).log("设置类application的application实例失败", ILRLogger.LogLevel.ERROR);
                }
            }
    }

    /**
     * 调用各类application的生命周期onCreate
     * */
    private void invokeOnCreate(){
        if (null != mAnologyApplications && mAnologyApplications.size() > 0){
            for (AnologyApplicationWrapper anologyApplicationWrapper : mAnologyApplications) {
                if (null != anologyApplicationWrapper && null != anologyApplicationWrapper.instance) {
                    anologyApplicationWrapper.instance.onCreate();
                }
            }
        }
    }

    /**
     * 注册所有的LocalRouterService
     * */
    public abstract void regiterLocalRouterService();
    /**
     * 注册所有的类application
     * */
    protected abstract void regiterAnologyApplication();

    /**
     * 是否需要开启多进程
     * */
    public abstract boolean needMultipleProcess();

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (null != mAnologyApplications && mAnologyApplications.size() > 0){
            for (AnologyApplicationWrapper anologyApplicationWrapper : mAnologyApplications) {
                if (null != anologyApplicationWrapper && null != anologyApplicationWrapper.instance) {
                    anologyApplicationWrapper.instance.onTerminate();
                }
            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (null != mAnologyApplications && mAnologyApplications.size() > 0){
            for (AnologyApplicationWrapper anologyApplicationWrapper : mAnologyApplications) {
                if (null != anologyApplicationWrapper && null != anologyApplicationWrapper.instance) {
                    anologyApplicationWrapper.instance.onLowMemory();
                }
            }
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (null != mAnologyApplications && mAnologyApplications.size() > 0){
            for (AnologyApplicationWrapper anologyApplicationWrapper : mAnologyApplications) {
                if (null != anologyApplicationWrapper && null != anologyApplicationWrapper.instance) {
                    anologyApplicationWrapper.instance.onTrimMemory(level);
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (null != mAnologyApplications && mAnologyApplications.size() > 0){
            for (AnologyApplicationWrapper anologyApplicationWrapper : mAnologyApplications) {
                if (null != anologyApplicationWrapper && null != anologyApplicationWrapper.instance) {
                    anologyApplicationWrapper.instance.onConfigurationChanged(newConfig);
                }
            }
        }
    }

    public static LRouterAppcation getInstance() {
        return mInstance;
    }
}
