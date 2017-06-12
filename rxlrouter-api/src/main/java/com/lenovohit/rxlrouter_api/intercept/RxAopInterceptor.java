package com.lenovohit.rxlrouter_api.intercept;

import java.lang.reflect.Method;

/**
 * rx拦截器
 * Created by yuzhijun on 2017/6/9.
 */
public abstract class RxAopInterceptor {
    /**
     * 进入请求方法前
     * */
    public abstract void enterRequestIntercept(Object proxyObjec, Method method, Object[] objects);
    /**
     * 执行完请求方法后
     * */
    public abstract  void exitRequestIntercept(Object proxyObjec, Method method, Object[] objects,long lengthMillis);
}
