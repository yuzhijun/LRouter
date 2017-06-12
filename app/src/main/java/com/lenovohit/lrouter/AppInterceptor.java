package com.lenovohit.lrouter;

import com.lenovohit.lrouter_api.annotation.ioc.Interceptor;
import com.lenovohit.lrouter_api.intercept.AopInterceptor;

/**
 * 请求拦截器
 * Created by yuzhijun on 2017/6/5.
 */
@Interceptor
public class AppInterceptor extends AopInterceptor {

    @Override
    public void enterRequestIntercept(String methodName, String[] paramNames, Object[] paramValues) {
        int i = 0;
    }

    @Override
    public void exitRequestIntercept(String methodName, String[] paramNames, Object[] paramValues, long lengthMillis) {

    }
}
