package com.lenovohit.bussiness_module_b;

import com.lenovohit.lrouter_api.annotation.ioc.Provider;
import com.lenovohit.lrouter_api.core.LRAction;
import com.lenovohit.lrouter_api.core.LRProvider;

/**
 * Created by yuzhijun on 2017/6/2.
 */
@Provider(name = "ModuleBProvider")
public class BussinessModuleBProvider extends LRProvider {
    @Override
    protected void registerActions() {

    }

    @Override
    public void registerAction(String actionName, LRAction action) {
        super.registerAction(actionName, action);
    }
}
