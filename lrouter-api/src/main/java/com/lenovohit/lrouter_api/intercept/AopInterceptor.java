package com.lenovohit.lrouter_api.intercept;

import com.lenovohit.lrouter_api.annotation.InterceptInject;

/**
 * 拦截器
 * Created by yuzhijun on 2017/6/5.
 */
public abstract class AopInterceptor {

    public AopInterceptor(){
        InterceptInject.interceptorInject(this);
    }
    /**
     * 进入请求方法前
     * */
    public abstract void enterRequestIntercept(String methodName,String[] paramNames,Object[] paramValues);
    /**
     * 执行完请求方法后
     * */
    public abstract  void exitRequestIntercept(String methodName,String[] paramNames,Object[] paramValues,long lengthMillis);
}
