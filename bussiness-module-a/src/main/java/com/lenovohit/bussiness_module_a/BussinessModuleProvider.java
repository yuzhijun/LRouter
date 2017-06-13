package com.lenovohit.bussiness_module_a;

import com.lenovohit.lrouter_api.annotation.ioc.Provider;
import com.lenovohit.lrouter_api.core.LRAction;
import com.lenovohit.lrouter_api.core.LRProvider;

/**
 * Created by yuzhijun on 2017/6/1.
 */
@Provider(name = "bussinessModuleA")
public class BussinessModuleProvider extends LRProvider {

    @Override
    public void registerAction(String actionName, LRAction action) {
        super.registerAction(actionName, action);
    }
}
