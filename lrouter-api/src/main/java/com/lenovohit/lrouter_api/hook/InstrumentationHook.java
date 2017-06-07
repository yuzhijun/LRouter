package com.lenovohit.lrouter_api.hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

/**
 * hook Instrumentation来拦截Activity跳转请求
 * Created by yuzhijun on 2017/6/7.
 */
public class InstrumentationHook extends Instrumentation {

    public Activity newActivity(ClassLoader cl, String className,
                                Intent intent)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        Class<?> targetActivity = cl.loadClass(className);
        Object instanceOfTarget = targetActivity.newInstance();



        return (Activity) instanceOfTarget;
    }
}
