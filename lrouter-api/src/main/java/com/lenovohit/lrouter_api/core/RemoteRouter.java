package com.lenovohit.lrouter_api.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.lenovohit.lrouter_api.ILocalRouterAIDL;
import com.lenovohit.lrouter_api.base.LRouterAppcation;
import com.lenovohit.lrouter_api.exception.LRException;
import com.lenovohit.lrouter_api.utils.ProcessUtil;

import java.util.HashMap;

/**
 * 负责与LocalRouter进行数据交互
 * Created by yuzhijun on 2017/5/27.
 */
public class RemoteRouter {
    private static final String TAG = "RemoteRouter";
    public static final String PROCESS_NAME = "com.lenovohit.lrouter.RemoteRouter";
    //远程路由是否停止标志
    boolean mStopping = false;
    private static RemoteRouter sInstance = null;
    private LRouterAppcation mContext;
    //LocalRouter服务实现了AIDL文件定义的接口的HashMap
    private static HashMap<String, LocalRouterServiceWrapper> sLocalRouterClassesHashMap;
    //用于跨进程访问LocalRouter,KEY为进程名字
    private HashMap<String,ServiceConnection> mLocalRouterServiceConnectionHashMap;
    private HashMap<String,ILocalRouterAIDL> mILocalRouterAIDLHashMap;

    private RemoteRouter(LRouterAppcation context){
        this.mContext = context;
        String checkProcessName = ProcessUtil.getProcessName(context, ProcessUtil.getMyProcessId());
        if (!PROCESS_NAME.equals(checkProcessName)){
            throw new LRException("不是当前远程路由的进程则不进行初始化");
        }
        sLocalRouterClassesHashMap = new HashMap<>();
        mLocalRouterServiceConnectionHashMap = new HashMap<>();
        mILocalRouterAIDLHashMap = new HashMap<>();
    }

    public static RemoteRouter getInstance(LRouterAppcation context){
        if (null == sInstance){
            synchronized (sInstance){
                if (null == sInstance){
                    sInstance = new RemoteRouter(context);
                }
            }
        }
        return sInstance;
    }

    /**
     * 注册本地路由服务
     * */
    public static void registerLocalRouterServiceConnection(Class<? extends LocalRouterService> localRouterServiceClazz,String processName){
        if (null == sLocalRouterClassesHashMap){
            sLocalRouterClassesHashMap = new HashMap<>();
        }

        LocalRouterServiceWrapper localRouterServiceWrapper  = new LocalRouterServiceWrapper(localRouterServiceClazz);
        sLocalRouterClassesHashMap.put(processName,localRouterServiceWrapper);
    }

    /**
     * 检查是否有本地路由服务注册
     * */
    public boolean checkLocalRouterHasRegistered(String processName){
        if (sLocalRouterClassesHashMap != null){
            LocalRouterServiceWrapper localRouterServiceWrapper = sLocalRouterClassesHashMap.get(processName);
            if (null == localRouterServiceWrapper){
                return false;
            }

            Class<? extends LocalRouterService> clazz = localRouterServiceWrapper.targetClass;
            return null != clazz;
        }
        return false;
    }

    /**
     * 连接本地路由服务,以便于跨进程访问
     * */
    public boolean connectLocalRouter(final String processName){
        if (sLocalRouterClassesHashMap != null) {
            LocalRouterServiceWrapper localRouterServiceWrapper = sLocalRouterClassesHashMap.get(processName);
            if (null == localRouterServiceWrapper){
                return false;
            }

            Class<? extends LocalRouterService> clazz = localRouterServiceWrapper.targetClass;
            if (null == clazz){
                return false;
            }

            Intent binderIntent = new Intent(mContext, clazz);
            ServiceConnection serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    ILocalRouterAIDL localRouterAIDL = ILocalRouterAIDL.Stub.asInterface(service);
                    ILocalRouterAIDL tempLocalRouterAIDL = mILocalRouterAIDLHashMap.get(processName);
                    if (null == tempLocalRouterAIDL){
                        mILocalRouterAIDLHashMap.put(processName, localRouterAIDL);
                        mLocalRouterServiceConnectionHashMap.put(processName, this);
                        try{
                            localRouterAIDL.connectRemoteRouter(processName);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mILocalRouterAIDLHashMap.remove(processName);
                    mLocalRouterServiceConnectionHashMap.remove(processName);
                }
            };
            mContext.bindService(binderIntent,serviceConnection, Context.BIND_AUTO_CREATE);
            return true;
        }
        return false;
    }

    /**
     * 判断本地路由访问的action是否是异步的
     * */
    public boolean checkIfLocalRouterAsync(String processName,String requestData){
        ILocalRouterAIDL target = mILocalRouterAIDLHashMap.get(processName);
        if (null == target){
            LocalRouterServiceWrapper localRouterServiceWrapper = sLocalRouterClassesHashMap.get(processName);
            if (null == localRouterServiceWrapper){
                return false;
            }

            Class<? extends LocalRouterService> clazz = localRouterServiceWrapper.targetClass;
            if (null == clazz){
                return false;
            }else{
                return true;
            }
        }else{
            try{
               return  target.checkIfLocalRouterAsync(requestData);
            }catch (Exception e){
                return false;
            }
        }
    }


}
