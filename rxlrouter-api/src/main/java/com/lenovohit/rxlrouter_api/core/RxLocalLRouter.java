package com.lenovohit.rxlrouter_api.core;

import android.content.Context;

import com.lenovohit.lrouter_api.base.LRouterAppcation;
import com.lenovohit.lrouter_api.core.LRAction;
import com.lenovohit.lrouter_api.core.LRActionResult;
import com.lenovohit.lrouter_api.core.LRouterRequest;
import com.lenovohit.lrouter_api.core.LRouterResponse;
import com.lenovohit.lrouter_api.core.LocalRouter;
import com.lenovohit.lrouter_api.exception.LRException;
import com.lenovohit.lrouter_api.intercept.ioc.Navigation;

import io.reactivex.Observable;

/**
 * 为了兼容Rxjava而引入
 * Created by yuzhijun on 2017/6/9.
 */
public class RxLocalLRouter extends LocalRouter {
    private static RxLocalLRouter sInstance = null;

    private RxLocalLRouter(LRouterAppcation context) {
        super(context);
    }

    public static RxLocalLRouter getInstance(LRouterAppcation context){
        if (sInstance == null){
            synchronized (LocalRouter.class){
                if (sInstance == null){
                    sInstance = new RxLocalLRouter(context);
                }
            }
        }
        return sInstance;
    }

    /**
     * 返回一个Observable对象用于rxjava实现
     * */
    @Navigation
    public Observable<String> rxNavigation(Context context, LRouterRequest request) throws Exception{
        LRouterResponse routerResponse = new LRouterResponse();
        if (mProcessName.equals(request.getProcessName())){
            //查找需要执行哪个action
            LRAction action = searchNavAction(request);
            if (null == action){
                throw new LRException(request.getAction()+"未找到,请检查是否有在"+request.getProvider()+"中注册");
            }
            //设置isIdle表示已经获取到此对象
            request.isIdle.set(true);
            //获取action是否需要非阻塞方式访问
            routerResponse.setAsync(action.needAsync(context,request));
            if (routerResponse.isAsync()) {//如果是异步则需要返回future对象即可
                LocalTask task = new LocalTask(routerResponse, request, context, action);
                return Observable.fromFuture(getThreadPool().submit(task));
            }else{
                LRActionResult result = action.invoke(context,request);
                routerResponse.mActionResultStr = result.toString();
                return Observable.just(routerResponse.mActionResultStr);
            }
        }else if (!LRouterAppcation.getInstance().needMultipleProcess()){
            throw new LRException("工程未打开多进程开关,但是子module又配置了多进程,请确认");
        }else{
            //如果是跨进程访问的话则将要发送的数据变成json再进行传输,否则跨进程需要对象实现Parcelable
            String processName = request.getProcessName();
            request.isIdle.set(true);
            //检查远程路由的服务是否连接上
            if (checkRemoteRouterConnect()){
                routerResponse.setAsync(mRemoteRouterAIDL.checkIfLocalRouterAsync(processName,request));
            }else {//未连接上远程路由服务
                routerResponse.setAsync(true);
                ConnectRemoteTask task = new ConnectRemoteTask(routerResponse, processName, request);
                return Observable.fromFuture(getThreadPool().submit(task));
            }

            if (!routerResponse.isAsync()) {//如果不是异步访问则直接调用
                routerResponse.mActionResultStr = mRemoteRouterAIDL.navigation(processName,request);
                return Observable.just(routerResponse.mActionResultStr);
            }else{
                RemoteTask task = new RemoteTask(processName, request);
                return Observable.fromFuture(getThreadPool().submit(task));
            }
        }
    }
}
