package com.lenovohit.lrouter_api.core;

/**
 * Created by yuzhijun on 2017/5/27.
 */
public class LocalRouterServiceWrapper {
    public Class<? extends LocalRouterService> targetClass = null;

    public LocalRouterServiceWrapper(Class<? extends LocalRouterService> logicClass) {
        this.targetClass = logicClass;
    }
}
