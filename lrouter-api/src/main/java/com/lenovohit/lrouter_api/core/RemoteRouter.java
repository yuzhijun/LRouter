package com.lenovohit.lrouter_api.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.lenovohit.lrouter_api.ILocalRouterAIDL;
import com.lenovohit.lrouter_api.base.LRouterAppcation;
import com.lenovohit.lrouter_api.exception.LRException;
import com.lenovohit.lrouter_api.utils.ILRLogger;
import com.lenovohit.lrouter_api.utils.LRLoggerFactory;
import com.lenovohit.lrouter_api.utils.ProcessUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 负责与LocalRouter进行数据交互
 * Created by yuzhijun on 2017/5/27.
 */
public class RemoteRouter {
    private static final String TAG = "RemoteRouter";
    public static final String PROCESS_NAME = "com.lenovohit.yuzhijun.RemoteRouter";
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
            synchronized (RemoteRouter.class){
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
    public static void registerLocalRouterServiceConnection(String processName,Class<? extends LocalRouterService> localRouterServiceClazz){
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
                    if (null == tempLocalRouterAIDL){//如果绑定的本地路由还未在hashmap中则添加
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
     * 根据进程名来停止服务
     * */
    protected boolean stopRouterByProcessName(String processName){
        try{
            if (TextUtils.isEmpty(processName)){//如果传入的进程名为空则直接返回false
                LRLoggerFactory.getLRLogger(TAG).log("停止服务时候进程名为空", ILRLogger.LogLevel.ERROR);
                return false;
            }else if(PROCESS_NAME.equals(processName)){//如果是当前远程路由的进程则说明是要停止远程路由
                stopSelf();
                return true;
            }else if(null == mLocalRouterServiceConnectionHashMap.get(processName)){//如果服务未注册建议进行注册
                LRLoggerFactory.getLRLogger(TAG).log("要停止的服务未注册："+processName, ILRLogger.LogLevel.ERROR);
                return false;
            }else{//如果是停止某一个本地路由服务则跨进程调用localrouter自己停止并从hashmap中删除
                ILocalRouterAIDL aidl = mILocalRouterAIDLHashMap.get(processName);
                if (null != aidl) {
                    try {
                        aidl.stopRemoteRouter();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                //找到当前进程并从服务的hashmap中删除掉
                mContext.unbindService(mLocalRouterServiceConnectionHashMap.get(processName));
                mILocalRouterAIDLHashMap.remove(processName);
                mLocalRouterServiceConnectionHashMap.remove(processName);
                return true;
            }
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 停止当前的远程路由服务
     * */
    private void stopSelf(){
        mStopping = true;//将停止标志置为true
        new Thread(new Runnable() {//停止服务需要时间所以开启一个新的线程进行执行
            @Override
            public void run() {
                List<String> locals = new ArrayList<>();
                locals.addAll(mILocalRouterAIDLHashMap.keySet());
                for (String processName : locals) {
                    ILocalRouterAIDL aidl = mILocalRouterAIDLHashMap.get(processName);
                    if (null != aidl) {
                        try {
                            aidl.stopRemoteRouter();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        mContext.unbindService(mLocalRouterServiceConnectionHashMap.get(processName));
                        mILocalRouterAIDLHashMap.remove(processName);
                        mLocalRouterServiceConnectionHashMap.remove(processName);
                    }
                }
                try {
                    Thread.sleep(1000);
                    mContext.stopService(new Intent(mContext, RemoteRouterService.class));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        }).start();
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
            return null != clazz;
        }else{
            try{
               return  target.checkIfLocalRouterAsync(requestData);
            }catch (Exception e){
                return false;
            }
        }
    }

    /**
     * 通过进程名查找访问那个本地路由服务,查找到则调用，完成跨进程通讯
     * */
    public LRouterResponse navigation(String processName,String requestStr) {
        LRouterResponse routerResponse = new LRouterResponse();

        if (mStopping) {//如果远程服务已经停止
            LRActionResult result = new LRActionResult.Builder()
                    .code(LRActionResult.RESULT_REMOTE_STOPPING)
                    .msg("远程路由已经停止")
                    .build();
            routerResponse.mAsync = true;
            routerResponse.mActionResultStr = result.toString();
            return routerResponse;
        }

        if (PROCESS_NAME.equals(processName)) {//不能访问远程路由的进程
            LRActionResult result = new LRActionResult.Builder()
                    .code(LRActionResult.RESULT_TARGET_IS_REMOTE)
                    .msg("访问的进程" + PROCESS_NAME + "是远程路由的进程")
                    .build();
            routerResponse.mAsync = true;
            routerResponse.mActionResultStr = result.toString();
            return routerResponse;
        }

        ILocalRouterAIDL localRouterAIDL = mILocalRouterAIDLHashMap.get(processName);
        if (null == localRouterAIDL) {
            if (!connectLocalRouter(processName)) {//如果hashmap中没有则尝试重新连接,如果失败则返回错误
                LRActionResult result = new LRActionResult.Builder()
                        .code(LRActionResult.RESULT_ROUTER_NOT_REGISTER)
                        .msg("路由是否未注册,请先注册")
                        .build();
                routerResponse.mAsync = true;
                routerResponse.mActionResultStr = result.toString();
                return routerResponse;
            } else {//否则连接成功则等待服务启动完毕
                int time = 0;
                while (true) {
                    localRouterAIDL = mILocalRouterAIDLHashMap.get(processName);
                    if (null == localRouterAIDL) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        time++;
                    } else {
                        break;
                    }
                    if (time >= 600) {//如果超时则视为错误
                        LRActionResult result = new LRActionResult.Builder()
                                .code(LRActionResult.RESULT_CANNOT_BIND_LOCAL)
                                .msg("未能成功绑定本地路由服务")
                                .build();
                        routerResponse.mAsync = true;
                        routerResponse.mActionResultStr = result.toString();
                        return routerResponse;
                    }
                }
            }
        }
        //如果本地路由连接正常则访问
        try{
            String resultString = localRouterAIDL.navigation(requestStr);
            routerResponse.mActionResultStr = resultString;
        }catch (Exception e){
            e.printStackTrace();
            LRActionResult result = new LRActionResult.Builder()
                    .code(LRActionResult.RESULT_REMOTE_LOCAL_ERROR)
                    .msg("远程路由和本地路由通讯失败")
                    .build();
            routerResponse.mAsync = true;
            routerResponse.mActionResultStr = result.toString();
        }
        return routerResponse;
    }


}
