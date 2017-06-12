package com.lenovohit.lrouter_api.annotation;

import com.lenovohit.lrouter_api.annotation.ioc.IntentInterceptor;
import com.lenovohit.lrouter_api.exception.LRException;
import com.lenovohit.lrouter_api.hook.InstrumentationHook;
import com.lenovohit.lrouter_api.intercept.StartupInterceptor;
import com.lenovohit.lrouter_api.utils.ILRLogger;
import com.lenovohit.lrouter_api.utils.LRLoggerFactory;

/**
 * 用于拦截器的注入
 * Created by yuzhijun on 2017/6/12.
 */
public class IntentInterceptInject {
    private static final String TAG = "InterceptInject";

    public static void interceptorInject(Class<? extends StartupInterceptor> clazz){
        try{
            if (null == clazz){
                throw new LRException("注入的interceptor不能为空");
            }

            IntentInterceptor interceptor = clazz.getAnnotation(IntentInterceptor.class);
            if (null != interceptor){
                StartupInterceptor intentInterceptor = clazz.newInstance();
                InstrumentationHook.registIntentInterceptor(intentInterceptor);
            }
        }catch (Exception e){
            e.printStackTrace();
            LRLoggerFactory.getLRLogger(TAG).log("注入interceptor失败", ILRLogger.LogLevel.ERROR);
        }
    }
}
