package com.lenovohit.bussiness_module_b;

import com.lenovohit.lrouter_api.annotation.ioc.Application;
import com.lenovohit.lrouter_api.base.AnologyApplication;

/**
 * Created by yuzhijun on 2017/6/2.
 */
@Application(name = "com.lenovohit.lrouter:moduleB",priority = 996)
public class BusinessModuleBAnologyApplication extends AnologyApplication {
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
