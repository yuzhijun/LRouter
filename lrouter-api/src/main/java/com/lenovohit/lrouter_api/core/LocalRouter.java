package com.lenovohit.lrouter_api.core;

import com.lenovohit.lrouter_api.base.LRouterAppcation;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 1.负责本地同进程module之间请求交互
 * 2.负责与RemoteRouter进行跨进程交互
 * Created by yuzhijun on 2017/5/27.
 */
public class LocalRouter {
    private LRouterAppcation mLRouterAppcation;
    private static LocalRouter sInstance = null;
    //本地路由持有各个模块所有的provider
    private ConcurrentHashMap<String,LRProvider> mProviderHashmap = null;

    private LocalRouter(LRouterAppcation context) {
        mLRouterAppcation = context;
        mProviderHashmap = new ConcurrentHashMap<>();
    }

    public static LocalRouter getInstance(LRouterAppcation context){
        if (sInstance == null){
            synchronized (sInstance){
                if (sInstance == null){
                    sInstance = new LocalRouter(context);
                }
            }
        }
        return sInstance;
    }



}
