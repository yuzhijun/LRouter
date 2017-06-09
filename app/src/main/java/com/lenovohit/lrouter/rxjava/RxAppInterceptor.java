package com.lenovohit.lrouter.rxjava;

import com.lenovohit.rxlrouter_api.intercept.RxAopInterceptor;

/**
 * Created by yuzhijun on 2017/6/9.
 */
public class RxAppInterceptor extends RxAopInterceptor {
    @Override
    public void enterRequestIntercept(String methodName, String[] paramNames, Object[] paramValues) {
        int i = 0;
    }

    @Override
    public void exitRequestIntercept(String methodName, String[] paramNames, Object[] paramValues, long lengthMillis) {

    }
}
