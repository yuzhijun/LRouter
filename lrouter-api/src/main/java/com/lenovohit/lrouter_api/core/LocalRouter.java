package com.lenovohit.lrouter_api.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.lenovohit.lrouter_api.IRemoteRouterAIDL;
import com.lenovohit.lrouter_api.base.LRouterAppcation;
import com.lenovohit.lrouter_api.exception.LRException;
import com.lenovohit.lrouter_api.utils.ProcessUtil;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 1.负责本地同进程module之间请求交互
 * 2.负责与RemoteRouter进行跨进程交互
 * Created by yuzhijun on 2017/5/27.
 */
public class LocalRouter {
    private LRouterAppcation mLRouterAppcation;
    private static LocalRouter sInstance = null;
    private String mProcessName = ProcessUtil.UNKNOWN_PROCESS_NAME;
    private static ExecutorService threadPool = null;
    //本地路由持有各个模块所有的provider
    private ConcurrentHashMap<String,LRProvider> mProviderHashmap = null;
    //用于跨进程访问远程路由
    private IRemoteRouterAIDL mRemoteRouterAIDL;

    private LocalRouter(LRouterAppcation context) {
        mLRouterAppcation = context;
        mProviderHashmap = new ConcurrentHashMap<>();
        mProcessName = ProcessUtil.getProcessName(context, ProcessUtil.getMyProcessId());
        if (mLRouterAppcation.needMultipleProcess() && !RemoteRouter.PROCESS_NAME.equals(mProcessName)) {
            connect2RemoteRouter(mProcessName);
        }
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

    private static synchronized ExecutorService getThreadPool() {
        if (null == threadPool) {
            threadPool = Executors.newCachedThreadPool();
        }
        return threadPool;
    }

    public void connect2RemoteRouter(String processName){
        Intent bindRemoteRouterServiceIntent = new Intent(mLRouterAppcation,RemoteRouterService.class);
        bindRemoteRouterServiceIntent.putExtra(RemoteRouterService.PROCESS_NAME,processName);
        mLRouterAppcation.bindService(bindRemoteRouterServiceIntent,mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void disconnectRemoteRouter() {
        if (null == mServiceConnection) {
            return;
        }
        mLRouterAppcation.unbindService(mServiceConnection);
        mRemoteRouterAIDL = null;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemoteRouterAIDL = IRemoteRouterAIDL.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteRouterAIDL = null;
        }
    };

    /**
     * 本地路由持有各个module的provider,provider中有多个action
     * */
    public void registerProvider(String providerName, LRProvider provider) {
        mProviderHashmap.put(providerName, provider);
    }

    public LRouterResponse navigation(Context context, LRouterRequest request) throws Exception {
        LRouterResponse routerResponse = new LRouterResponse();
        //如果当前进程等于请求的进程说明是在同个进程下则不需要跨进程访问
        if (mProcessName.equals(request.getProcessName())){
            //将传输的参数放进params中
            HashMap<String, String> params = new HashMap<>();
            params.putAll(request.getParams());
            //查找需要执行哪个action
            LRAction action = searchNavAction(request);
            if (null == action){
                throw new LRException(request.getAction()+"未找到,请检查是否有在"+request.getProvider()+"中注册");
            }

            //设置isIdle表示已经获取到此对象
            request.isIdle.set(true);
            //获取action是否需要非阻塞方式访问
            routerResponse.mAsync = action.needAsync(context,params);
            if (routerResponse.mAsync){//如果是异步则需要返回future对象即可
                LocalTask task = new LocalTask(routerResponse, params, context, action);
                routerResponse.mAsyncResponse = getThreadPool().submit(task);
            }else{//如果是同步,则立即返回返回值
                LRActionResult result = action.invoke(context,params);
                routerResponse.mActionResultStr = result.toString();
            }
        }else if (!mLRouterAppcation.needMultipleProcess()){
            throw new LRException("工程未打开多进程开关,但是子module又配置了多进程,请确认");
        }else{
            //如果是跨进程访问的话则将要发送的数据变成json再进行传输,否则跨进程需要对象实现Parcelable
            String processName = request.getProcessName();
            String aidlTransportStr = request.toString();
            request.isIdle.set(true);
            //检查远程路由的服务是否连接上
            if (!checkRemoteRouterConnect()){
                routerResponse.mAsync = mRemoteRouterAIDL.checkIfLocalRouterAsync(processName,aidlTransportStr);
            }else{//未连接上远程路由服务
                routerResponse.mAsync = true;

            }
        }


        return routerResponse;
    }

    private boolean checkRemoteRouterConnect(){
        boolean result = false;
        if (null != mRemoteRouterAIDL) {
            result = true;
        }
        return result;
    }

    /**
     * 寻找action如果未找到则返回null
     * */
    private LRAction searchNavAction(LRouterRequest request){
        if (null != mProviderHashmap){
            LRProvider provider = mProviderHashmap.get(request.getProvider());
            if (null == provider){
                return null;
            }else{
                LRAction action = provider.findAction(request.getAction());
                return action;
            }
        }
        return null;
    }

    /**
     * 检查是否action是异步的
     * */
    public boolean checkIfLocalRouterAsync(LRouterRequest request){
        if (mProcessName.equals(request.getProcessName()) && checkRemoteRouterConnect()) {
            return findRequestAction(request).needAsync(mLRouterAppcation, request.getParams());
        } else {
            return true;
        }
    }

    /**
     * 根据请求的request查找action
     * */
    private LRAction findRequestAction(LRouterRequest request){
        LRProvider targetProvider = mProviderHashmap.get(request.getProvider());
        ErrorAction defaultNotFoundAction = new ErrorAction(false, LRActionResult.ACTION_NOT_FOUND, "未找到action");
        if (null == targetProvider) {
            return defaultNotFoundAction;
        } else {
            LRAction targetAction = targetProvider.findAction(request.getAction());
            if (null == targetAction) {
                return defaultNotFoundAction;
            } else {
                return targetAction;
            }
        }
    }

    //本地异步任务
    private class LocalTask implements Callable<String> {

        private LRouterResponse mResponse;
        private HashMap<String, String> mRequestData;
        private Context mContext;
        private LRAction mAction;
        private Object mObject;
        public LocalTask(LRouterResponse routerResponse, HashMap<String, String> requestData, Context context, LRAction maAction) {
            this.mContext = context;
            this.mResponse = routerResponse;
            this.mRequestData = requestData;
            this.mAction = maAction;
        }

        @Override
        public String call() throws Exception {
            LRActionResult actionResult = mAction.invoke(mContext,mRequestData);
            return actionResult.toString();
        }
    }

}
