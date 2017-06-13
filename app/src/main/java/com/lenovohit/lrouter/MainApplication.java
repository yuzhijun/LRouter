package com.lenovohit.lrouter;

import com.lenovohit.lrouter_api.base.LRouterAppcation;

/**
 * 程序的application
 * Created by yuzhijun on 2017/5/27.
 */
public class MainApplication extends LRouterAppcation {


    @Override
    public boolean needMultipleProcess() {
        return true;
    }
}
