package com.lenovohit.lrouter;

import android.content.Intent;

import com.lenovohit.annotation.IntentInterceptor;
import com.lenovohit.lrouter_api.intercept.StartupInterceptor;

/**
 * Created by yuzhijun on 2017/6/12.
 */
@IntentInterceptor
public class StartInterceptor extends StartupInterceptor {
    @Override
    public void intentIntercept(Object targetActivity, Intent intent) {
        int i = 0;
    }
}
