package com.lenovohit.lrouter_api.hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import com.lenovohit.lrouter_api.intercept.StartupInterceptor;

/**
 * hook Instrumentation来拦截Activity跳转请求
 * Created by yuzhijun on 2017/6/7.
 */
public class InstrumentationHook extends Instrumentation {
    private static StartupInterceptor mIntentInterceptor;

    public static void registIntentInterceptor(StartupInterceptor intentInterceptor){
        mIntentInterceptor = intentInterceptor;
    }

    public Activity newActivity(ClassLoader cl, String className,
                                Intent intent)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        Class<?> targetActivity = cl.loadClass(className);
        Object instanceOfTarget = targetActivity.newInstance();

        if (null != mIntentInterceptor){
            mIntentInterceptor.intentIntercept(instanceOfTarget,intent);
        }

        return (Activity) instanceOfTarget;
    }
}
