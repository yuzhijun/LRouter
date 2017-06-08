package com.lenovohit.lrouter_api.core;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.lenovohit.lrouter_api.ILocalRouterAIDL;
import com.lenovohit.lrouter_api.base.LRouterAppcation;

/**
 * 本地路由服务
 * Created by yuzhijun on 2017/5/27.
 */
public class LocalRouterService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    ILocalRouterAIDL.Stub mStub = new ILocalRouterAIDL.Stub() {
        @Override
        public boolean checkIfLocalRouterAsync(LRouterRequest routerRequset) throws RemoteException {
            return LocalRouter.getInstance(LRouterAppcation.getInstance()).
                    checkIfLocalRouterAsync(routerRequset);
        }

        @Override
        public void connectRemoteRouter(String processName) throws RemoteException {
            LocalRouter.getInstance(LRouterAppcation.getInstance()).connect2RemoteRouter(processName);
        }

        @Override
        public String navigation(LRouterRequest routerRequest) throws RemoteException{
            try {
                LocalRouter.ListenerFutureTask routerResponse = LocalRouter.getInstance(LRouterAppcation.getInstance())
                        .navigation(LocalRouterService.this,routerRequest);
                return routerResponse.getLRouterResponse().get();
            } catch (Exception e) {
                e.printStackTrace();
                return new LRActionResult.Builder().msg(e.getMessage()).build().toString();
            }
        }

        @Override
        public boolean stopRemoteRouter() throws RemoteException{
            LocalRouter.getInstance(LRouterAppcation.getInstance()).disconnectRemoteRouter();
            return true;
        }
    };
}
