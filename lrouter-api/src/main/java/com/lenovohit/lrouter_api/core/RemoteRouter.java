package com.lenovohit.lrouter_api.core;

import com.lenovohit.lrouter_api.base.LRouterAppcation;
import com.lenovohit.lrouter_api.utils.ProcessUtil;

/**
 * 负责与LocalRouter进行数据交互
 * Created by yuzhijun on 2017/5/27.
 */
public class RemoteRouter {
    private static RemoteRouter sInstance = null;
    private LRouterAppcation mContext;
    public static final String PROCESS_NAME = "com.lenovohit.lrouter.RemoteRouter";

    private RemoteRouter(LRouterAppcation context){
        this.mContext = context;


    }

    public static RemoteRouter getInstance(LRouterAppcation context){
        String checkProcessName = ProcessUtil.getProcessName(context, ProcessUtil.getMyProcessId());
        if (!PROCESS_NAME.equals(checkProcessName)){
            return null;//不是当前远程路由的进程则不进行初始化
        }

        if (null == sInstance){
            synchronized (sInstance){
                if (null == sInstance){
                    sInstance = new RemoteRouter(context);
                }
            }
        }
        return sInstance;
    }


}
