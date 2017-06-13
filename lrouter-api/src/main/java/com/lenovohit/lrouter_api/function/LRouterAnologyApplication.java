package com.lenovohit.lrouter_api.function;

import com.lenovohit.lrouter_api.base.AnologyApplication;
import com.lenovohit.lrouter_api.core.RemoteRouter;

/**
 * LRouter本身也是一个库,所以也有类application
 * Created by yuzhijun on 2017/5/27.
 */
public class LRouterAnologyApplication extends AnologyApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    protected void init(){
        //初始化远程路由
        RemoteRouter.getInstance(mApplication);
    }
}
