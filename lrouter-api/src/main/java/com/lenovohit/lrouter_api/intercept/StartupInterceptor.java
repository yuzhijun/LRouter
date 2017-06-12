package com.lenovohit.lrouter_api.intercept;

import android.content.Intent;

/**
 * 拦截跳转时候intent请求
 * Created by yuzhijun on 2017/6/12.
 */
public abstract class StartupInterceptor {
    public abstract void intentIntercept(Object targetActivity, Intent intent);
}
