package com.lenovohit.lrouter;

import com.lenovohit.lrouter_api.core.LRProvider;

/**
 * Created by yuzhijun on 2017/6/1.
 */

public class MainProvider extends LRProvider {
    @Override
    protected void registerActions() {//在provider里面注册进多个action,达到暴露action的效果
        registerAction("main",new MainAction());
    }
}
