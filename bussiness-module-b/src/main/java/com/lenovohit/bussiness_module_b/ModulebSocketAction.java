package com.lenovohit.bussiness_module_b;

import com.lenovohit.annotation.Action;
import com.lenovohit.lrouter_api.core.socket.server.LRSocketAction;

/**
 * Created by yuzhijun on 2017/6/14.
 */
@Action(name = "ModulebSocketAction",provider = "ModuleBProvider")
public class ModulebSocketAction extends LRSocketAction {
    @Override
    public String socketInvoke(String receiveStr) {
        return receiveStr;
    }

    @Override
    public int socketPort() {
        return 10001;
    }
}
