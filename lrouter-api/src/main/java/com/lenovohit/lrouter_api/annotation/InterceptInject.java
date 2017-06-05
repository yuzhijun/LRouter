package com.lenovohit.lrouter_api.annotation;

import com.lenovohit.lrouter_api.annotation.ioc.Interceptor;
import com.lenovohit.lrouter_api.exception.LRException;
import com.lenovohit.lrouter_api.intercept.AopInterceptor;
import com.lenovohit.lrouter_api.intercept.RequstAspect;
import com.lenovohit.lrouter_api.utils.ILRLogger;
import com.lenovohit.lrouter_api.utils.LRLoggerFactory;

/**
 * 用于拦截器的注入
 * Created by yuzhijun on 2017/6/5.
 */
public class InterceptInject {
    private static final String TAG = "InterceptInject";

    public static void interceptorInject(AopInterceptor aopInterceptor){
        try{
            if (null == aopInterceptor){
                throw new LRException("注入的interceptor不能为空");
            }

            Class<? extends AopInterceptor> clazz = aopInterceptor.getClass();
            Interceptor interceptor = clazz.getAnnotation(Interceptor.class);
            if (null != interceptor){
                RequstAspect.interceptorInject(aopInterceptor);
            }
        }catch (Exception e){
            e.printStackTrace();
            LRLoggerFactory.getLRLogger(TAG).log("注入interceptor失败", ILRLogger.LogLevel.ERROR);
        }
    }
}
