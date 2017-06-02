package com.lenovohit.bussiness_module_b;

import com.lenovohit.lrouter_api.base.AnologyApplication;

/**
 * Created by yuzhijun on 2017/6/2.
 */

public class BusinessModuleBAnologyApplication extends AnologyApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        new BussinessModuleBProvider();
        new BussinessModuleBAction();
    }
}
