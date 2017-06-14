package com.lenovohit.lrouter;

import com.lenovohit.lrouter_api.annotation.ioc.Application;
import com.lenovohit.lrouter_api.base.AnologyApplication;

/**
 * Created by yuzhijun on 2017/6/1.
 */
@Application(name = "com.lenovohit.lrouter",priority = 999)
public class MainAnologyApplication extends AnologyApplication {
    @Override
    public void onCreate() {//在类application类里面将provider注册进本地路由内，每个模块有一个provider对应多个action
        super.onCreate();
    }
}
