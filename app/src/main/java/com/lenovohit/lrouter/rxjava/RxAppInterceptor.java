package com.lenovohit.lrouter.rxjava;

import com.lenovohit.rxlrouter_api.intercept.RxAopInterceptor;

import java.lang.reflect.Method;

/**
 * Created by yuzhijun on 2017/6/9.
 */
public class RxAppInterceptor extends RxAopInterceptor {

    @Override
    public void enterRequestIntercept(Object proxyObjec, Method method, Object[] objects) {
        int i = 0;
    }

    @Override
    public void exitRequestIntercept(Object proxyObjec, Method method, Object[] objects, long lengthMillis) {

    }
}
