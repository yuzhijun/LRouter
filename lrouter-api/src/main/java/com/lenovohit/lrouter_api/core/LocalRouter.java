package com.lenovohit.lrouter_api.core;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.lenovohit.lrouter_api.IRemoteRouterAIDL;
import com.lenovohit.lrouter_api.base.LRouterAppcation;
import com.lenovohit.lrouter_api.core.callback.IRequestCallBack;
import com.lenovohit.lrouter_api.exception.LRException;
import com.lenovohit.lrouter_api.intercept.ioc.Navigation;
import com.lenovohit.lrouter_api.utils.DefaultPoolExecutor;
import com.lenovohit.lrouter_api.utils.ILRLogger;
import com.lenovohit.lrouter_api.utils.LRLoggerFactory;
import com.lenovohit.lrouter_api.utils.ProcessUtil;

import java.lang.ref.SoftReference;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * 1.负责本地同进程module之间请求交互
 * 2.负责与RemoteRouter进行跨进程交互
 * Created by yuzhijun on 2017/5/27.
 */
public class LocalRouter {
    private static final String TAG = "LocalRouter";
    private Handler mHandler = new Handler();
    private LRouterAppcation mLRouterAppcation;
    private static LocalRouter sInstance = null;
    public String mProcessName = ProcessUtil.UNKNOWN_PROCESS_NAME;
    private static ExecutorService threadPool = null;
    //本地路由持有各个模块所有的provider,可能有多个进程localRouter访问所以用ConcurrentHashMap
    private ConcurrentHashMap<String,LRProvider> mProviderHashmap = null;
    //用于跨进程访问远程路由
    public IRemoteRouterAIDL mRemoteRouterAIDL;
    //空的task
    private EmptyTask emptyTask = new EmptyTask();

    protected LocalRouter(LRouterAppcation context) {
        mLRouterAppcation = context;
        mProviderHashmap = new ConcurrentHashMap<>();
        mProcessName = ProcessUtil.getProcessName(context, ProcessUtil.getMyProcessId());
        if (mLRouterAppcation.needMultipleProcess() && !RemoteRouter.PROCESS_NAME.equals(mProcessName)) {
            connect2RemoteRouter(mProcessName);
        }
    }

    public static LocalRouter getInstance(LRouterAppcation context){
        if (sInstance == null){
            synchronized (LocalRouter.class){
                if (sInstance == null){
                    sInstance = new LocalRouter(context);
                }
            }
        }
        return sInstance;
    }

    public static synchronized ExecutorService getThreadPool() {
        if (null == threadPool) {
            threadPool = DefaultPoolExecutor.getInstance();
        }
        return threadPool;
    }

    public void connect2RemoteRouter(String processName){
        Intent bindRemoteRouterServiceIntent = new Intent(mLRouterAppcation,RemoteRouterService.class);
        bindRemoteRouterServiceIntent.putExtra(RemoteRouterService.PROCESS_NAME,processName);
        mLRouterAppcation.bindService(bindRemoteRouterServiceIntent,mServiceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 切断与远程路由的连接
     * */
    public void disconnectRemoteRouter() {
        if (null == mServiceConnection) {
            return;
        }
        mLRouterAppcation.unbindService(mServiceConnection);
        mRemoteRouterAIDL = null;
    }

    /**
     * 关闭远程路由
     * */
    public void stopRemoteRouter(){
        try{
            if (checkRemoteRouterConnect()){
                mRemoteRouterAIDL.stopRouter(RemoteRouter.PROCESS_NAME);
            }else{
                LRLoggerFactory.getLRLogger(TAG).log("远程路由已经断开了", ILRLogger.LogLevel.INFO);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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

    /**
     * 访问
     * */
    @Navigation
    public ListenerFutureTask navigation(Context context, LRouterRequest request) throws Exception {
        LRouterResponse routerResponse = new LRouterResponse();
        ListenerFutureTask futureTask = null;
        //如果当前进程等于请求的进程说明是在同个进程下则不需要跨进程访问
        if (mProcessName.equals(request.getProcessName())){
            //查找需要执行哪个action
            LRAction action = searchNavAction(request);
            if (null == action){
                throw new LRException(request.getAction()+"未找到,请检查是否有在"+request.getProvider()+"中注册");
            }
            //设置isIdle表示已经获取到此对象
            request.isIdle.set(true);
            //获取action是否需要非阻塞方式访问
            routerResponse.mAsync = action.needAsync(context,request);
            if (routerResponse.mAsync){//如果是异步则需要返回future对象即可
                LocalTask task = new LocalTask(routerResponse, request, context, action);
                futureTask = new ListenerFutureTask(task,routerResponse);
                getThreadPool().submit(futureTask);
            }else{//如果是同步,则立即返回返回值
                LRActionResult result = action.invoke(context,request);
                routerResponse.mActionResultStr = result.toString();
                futureTask = new ListenerFutureTask(emptyTask,routerResponse);
            }
        }else if (!mLRouterAppcation.needMultipleProcess()){
            throw new LRException("工程未打开多进程开关,但是子module又配置了多进程,请确认");
        }else{
            //如果是跨进程访问的话则将要发送的数据变成json再进行传输,否则跨进程需要对象实现Parcelable
            String processName = request.getProcessName();
            request.isIdle.set(true);
            //检查远程路由的服务是否连接上
            if (checkRemoteRouterConnect()){
                routerResponse.mAsync = mRemoteRouterAIDL.checkIfLocalRouterAsync(processName,request);
            }else{//未连接上远程路由服务
                routerResponse.mAsync = true;
                ConnectRemoteTask task = new ConnectRemoteTask(routerResponse, processName, request);
                futureTask = new ListenerFutureTask(task,routerResponse);
                getThreadPool().submit(futureTask);
                return futureTask;
            }

            if (!routerResponse.mAsync){//如果不是异步访问则直接调用
                routerResponse.mActionResultStr = mRemoteRouterAIDL.navigation(processName,request);
                futureTask = new ListenerFutureTask(emptyTask,routerResponse);
            }else{
                RemoteTask task = new RemoteTask(processName, request);
                futureTask = new ListenerFutureTask(task,routerResponse);
                getThreadPool().submit(futureTask);
            }
        }
        return futureTask;
    }

    //检查远程服务是否连接
    public boolean checkRemoteRouterConnect(){
        boolean result = false;
        if (null != mRemoteRouterAIDL) {
            result = true;
        }
        return result;
    }

    /**
     * 寻找action如果未找到则返回null
     * */
    protected LRAction searchNavAction(LRouterRequest request){
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
            return findRequestAction(request).needAsync(mLRouterAppcation, request);
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

    /**
     * 通过provider名字查找provider
     * */
    public LRProvider findProvider(String name){
        LRProvider targetProvider = null;
        if (null != mProviderHashmap){
            targetProvider = mProviderHashmap.get(name);
        }
        return targetProvider;
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    //本地异步任务
    public class LocalTask implements Callable<String> {

        private LRouterResponse mResponse;
        private LRouterRequest mRequestData;
        private Context mContext;
        private LRAction mAction;


        public LocalTask(LRouterResponse routerResponse, LRouterRequest requestData, Context context, LRAction maAction) {
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

    //远程异步服务
    public class RemoteTask implements Callable<String>{
        private String processName;
        private LRouterRequest request;
        public RemoteTask(String processName,LRouterRequest request){
            this.processName = processName;
            this.request = request;
        }

        @Override
        public String call() throws Exception {
            String result = mRemoteRouterAIDL.navigation(processName, request);
            return result;
        }
    }

    //异步启动远程路由服务并访问
    public class ConnectRemoteTask implements Callable<String> {
        private String processName;
        private LRouterRequest request;
        private LRouterResponse response;

        public ConnectRemoteTask(LRouterResponse response,String processName,LRouterRequest request){
            this.processName = processName;
            this.response = response;
            this.request = request;
        }

        @Override
        public String call() throws Exception {
            //连接远程路由服务
            connect2RemoteRouter(processName);

            int time = 0;
            while (true){
                if (null == mRemoteRouterAIDL) {
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
                    ErrorAction defaultNotFoundAction = new ErrorAction(true, LRActionResult.RESULT_CANNOT_BIND_REMOTE, "绑定远程服务超时");
                    LRActionResult result = defaultNotFoundAction.invoke(mLRouterAppcation, request);
                    response.mActionResultStr = result.toString();
                    return result.toString();
                }
            }
            String result = mRemoteRouterAIDL.navigation(processName, request);
            return result;
        }
    }

    /**
     * 空的task
     * */
    private class EmptyTask implements Callable<String>{
        @Override
        public String call() throws Exception {
            return null;
        }
    }


    /**
     * 用于监听请求结束
     * */
    public class ListenerFutureTask extends FutureTask<String>{
        private IRequestCallBack mCallBack;
        private LRouterResponse mLRouterResponse;

        public ListenerFutureTask(@NonNull Callable<String> callable,LRouterResponse lRouterResponse) {
            super(callable);
            this.mLRouterResponse = lRouterResponse;
            //采用软引用避免无法释放
            SoftReference<ListenerFutureTask> weakReference = new SoftReference<>(this);
            this.mLRouterResponse.mAsyncResponse = weakReference;
        }

        public LRouterResponse getLRouterResponse() {
            return mLRouterResponse;
        }
        public ListenerFutureTask setCallBack(IRequestCallBack callBack) {
            mCallBack = callBack;
            return this;
        }
        @Override
        protected void done() {
            if (null != mCallBack){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            mCallBack.onSuccess(get());
                        }catch (Exception e){
                            mCallBack.onFailure(e);
                        }
                    }
                });
            }
        }
    }
}
