package com.lenovohit.bussiness_module_a;

import com.lenovohit.lrouter_api.core.LRProvider;

/**
 * Created by yuzhijun on 2017/6/1.
 */
public class BussinessModuleProvider extends LRProvider {
    @Override
    protected void registerActions() {
        registerAction("bussinessModuleA",new BussinessModuleAction());
    }
}
