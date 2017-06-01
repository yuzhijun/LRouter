package com.lenovohit.lrouter;

import com.lenovohit.bussiness_module_a.BusinessModuleAnologyApplication;
import com.lenovohit.bussiness_module_a.BusinessModuleAServiceConnection;
import com.lenovohit.bussiness_module_b.BussinessModuleBServiceConnection;
import com.lenovohit.lrouter_api.base.LRouterAppcation;
import com.lenovohit.lrouter_api.core.RemoteRouter;

/**
 * 程序的application
 * Created by yuzhijun on 2017/5/27.
 */
public class MainApplication extends LRouterAppcation {
    @Override
    public void regiterLocalRouterService() {//将本地路由服务进行注册进远程路由内
        RemoteRouter.registerLocalRouterServiceConnection("com.lenovohit.lrouter",MainRouterConnectService.class);
        RemoteRouter.registerLocalRouterServiceConnection("com.lenovohit.lrouter:moduleA", BusinessModuleAServiceConnection.class);
        RemoteRouter.registerLocalRouterServiceConnection("com.lenovohit.lrouter:moduleB", BussinessModuleBServiceConnection.class);
    }

    @Override
    protected void regiterAnologyApplication() {//将类application类注册进Application内
        registerAnologyApplication("com.lenovohit.lrouter",999,MainAnologyApplication.class);
        registerAnologyApplication("com.lenovohit.lrouter:moduleA",998, BusinessModuleAnologyApplication.class);
    }

    @Override
    public boolean needMultipleProcess() {
        return true;
    }
}
