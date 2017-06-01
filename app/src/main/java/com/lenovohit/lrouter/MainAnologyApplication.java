package com.lenovohit.lrouter;

import com.lenovohit.lrouter_api.base.AnologyApplication;
import com.lenovohit.lrouter_api.core.LocalRouter;

/**
 * Created by yuzhijun on 2017/6/1.
 */

public class MainAnologyApplication extends AnologyApplication {
    @Override
    public void onCreate() {//在类application类里面将provider注册进本地路由内，每个模块有一个provider对应多个action
        super.onCreate();
        LocalRouter.getInstance(mApplication).registerProvider("main",new MainProvider());
    }
}
