package com.lenovohit.bussiness_module_a;

import com.lenovohit.lrouter_api.base.AnologyApplication;
import com.lenovohit.lrouter_api.core.LocalRouter;

/**
 * Created by yuzhijun on 2017/6/1.
 */

public class BusinessModuleAnologyApplication extends AnologyApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        LocalRouter.getInstance(mApplication).registerProvider("bussinessModuleA",new BussinessModuleProvider());
    }
}
