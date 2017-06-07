package com.lenovohit.lrouter;

import com.lenovohit.lrouter_api.annotation.ioc.Interceptor;
import com.lenovohit.lrouter_api.intercept.AopInterceptor;

/**
 * Created by yuzhijun on 2017/6/7.
 */
@Interceptor
public class AppInterceptor1 extends AopInterceptor {
    @Override
    public void enterRequestIntercept(String methodName, String[] paramNames, Object[] paramValues) {

    }

    @Override
    public void exitRequestIntercept(String methodName, String[] paramNames, Object[] paramValues, long lengthMillis) {

    }
}
