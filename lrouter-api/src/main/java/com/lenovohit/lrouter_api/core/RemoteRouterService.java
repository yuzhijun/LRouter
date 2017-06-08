package com.lenovohit.lrouter_api.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.lenovohit.lrouter_api.IRemoteRouterAIDL;
import com.lenovohit.lrouter_api.base.LRouterAppcation;
import com.lenovohit.lrouter_api.exception.LRException;
import com.lenovohit.lrouter_api.utils.ILRLogger;
import com.lenovohit.lrouter_api.utils.LRLoggerFactory;

/**
 * 远程路由服务
 * Created by yuzhijun on 2017/5/27.
 */
public final class RemoteRouterService extends Service {
    private static final String TAG = "RemoteRouterService";
    public static final String PROCESS_NAME = "process_name";

    @Override
    public void onCreate() {
        super.onCreate();
        if (!(getApplication() instanceof LRouterAppcation)) {
            throw new LRException("请检查你的AndroidManifest.xml和applicaiton是LRouterApplicaiton");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        String processName = intent.getStringExtra(PROCESS_NAME);
        if (RemoteRouter.getInstance(LRouterAppcation.getInstance()).mStopping){
            LRLoggerFactory.getLRLogger(TAG).log("远程路由已经停止", ILRLogger.LogLevel.ERROR);
            return null;
        }

        if (null == processName || processName.length() == 0){
            LRLoggerFactory.getLRLogger(TAG).log("绑定service时候intent中进程名为空", ILRLogger.LogLevel.ERROR);
            return null;
        }

        boolean hasRegistered = RemoteRouter.getInstance(LRouterAppcation.getInstance()).checkLocalRouterHasRegistered(processName);
        if (!hasRegistered){
            LRLoggerFactory.getLRLogger(TAG).log("本地路由服务未注册,应该在主工程Application中的regiterLocalRouterService进行注册", ILRLogger.LogLevel.ERROR);
            return null;
        }

        //连接本地路由并绑定服务进行跨进程访问，达到双向传输数据的效果
        RemoteRouter.getInstance(LRouterAppcation.getInstance()).connectLocalRouter(processName);
        return mStub;
    }

    IRemoteRouterAIDL.Stub mStub = new IRemoteRouterAIDL.Stub() {
        @Override
        public boolean checkIfLocalRouterAsync(String processName, LRouterRequest routerRequset) throws RemoteException {
            return RemoteRouter.getInstance(LRouterAppcation.getInstance()).checkIfLocalRouterAsync(processName,routerRequset);
        }

        @Override
        public String navigation(String processName,LRouterRequest routerRequest) throws RemoteException{
           try{
                return RemoteRouter.getInstance(LRouterAppcation.getInstance())
                        .navigation(processName,routerRequest)
                        .mActionResultStr;
           }catch (Exception e){
                e.printStackTrace();
               return new LRActionResult.Builder()
                       .code(LRActionResult.RESULT_ERROR)
                       .msg(e.getMessage())
                       .build()
                       .toString();
           }
        }

        @Override
       public boolean stopRouter(String processName) throws RemoteException{
            return RemoteRouter.getInstance(LRouterAppcation.getInstance()).stopRouterByProcessName(processName);
       }
    };
}
