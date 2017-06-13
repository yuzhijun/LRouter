package com.lenovohit.bussiness_module_a;

import com.lenovohit.lrouter_api.annotation.ioc.Application;
import com.lenovohit.lrouter_api.base.AnologyApplication;

/**
 * Created by yuzhijun on 2017/6/1.
 */
@Application(name = "com.lenovohit.lrouter:moduleA",priority = 997)
public class BusinessModuleAnologyApplication extends AnologyApplication {
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
