package com.lenovohit.rxlrouter_api.intercept;

/**
 * rx拦截器
 * Created by yuzhijun on 2017/6/9.
 */
public abstract class RxAopInterceptor {
    /**
     * 进入请求方法前
     * */
    public abstract void enterRequestIntercept(String methodName,String[] paramNames,Object[] paramValues);
    /**
     * 执行完请求方法后
     * */
    public abstract  void exitRequestIntercept(String methodName,String[] paramNames,Object[] paramValues,long lengthMillis);
}
