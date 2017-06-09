package com.lenovohit.rxlrouter_api.intercept;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 拦截方法
 * Created by yuzhijun on 2017/6/9.
 */
public class RxNavigationInvocationHandler implements InvocationHandler{
    private final Object proxyObject;

    public RxNavigationInvocationHandler(Object proxyObject){
        this.proxyObject = proxyObject;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

        int i = 0;
        Object result = method.invoke(proxyObject, objects);


        return result;
    }
}
