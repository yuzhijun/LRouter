package com.lenovohit.rxlrouter_api.intercept;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 拦截方法
 * Created by yuzhijun on 2017/6/9.
 */
public class RxNavigationInvocationHandler implements InvocationHandler{
    private final Object proxyObject;
    private RxAopInterceptor mRxAopInterceptor;

    public void registerIRxNavigation(RxAopInterceptor rxAopInterceptor){
        this.mRxAopInterceptor = rxAopInterceptor;
    }

    public RxNavigationInvocationHandler(Object proxyObject){
        this.proxyObject = proxyObject;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

       if (null != mRxAopInterceptor){
           mRxAopInterceptor.enterRequestIntercept(proxyObject,method,objects);
       }
        long startNanos = System.nanoTime();
        Object result = method.invoke(proxyObject, objects);
        long stopNanos = System.nanoTime();
        long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);
        if (null != mRxAopInterceptor){
            mRxAopInterceptor.exitRequestIntercept(proxyObject,method,objects,lengthMillis);
        }
        return result;
    }
}
