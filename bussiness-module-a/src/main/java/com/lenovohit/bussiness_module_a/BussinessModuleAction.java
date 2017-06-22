package com.lenovohit.bussiness_module_a;

import android.content.Context;
import android.text.TextUtils;

import com.lenovohit.annotation.Action;
import com.lenovohit.lrouter_api.core.LRAction;
import com.lenovohit.lrouter_api.core.LRActionResult;
import com.lenovohit.lrouter_api.core.LRouterRequest;

/**
 * Created by yuzhijun on 2017/6/1.
 */
@Action(name = "bussinessModuleA",provider = "bussinessModuleA")
public class BussinessModuleAction extends LRAction {

    @Override
    public boolean needAsync(Context context, LRouterRequest requestData) {
        return true;
    }

    @Override
    public LRActionResult invoke(Context context, LRouterRequest requestData) {
        String temp = "";
        if(!TextUtils.isEmpty((CharSequence) requestData.getParams().get("1"))){
            temp+=requestData.getParams().get("1");
        }
        if(!TextUtils.isEmpty((CharSequence) requestData.getParams().get("2"))){
            temp+=requestData.getParams().get("2");
        }
        LRActionResult result = new LRActionResult.Builder()
                .code(LRActionResult.RESULT_SUCESS)
                .msg("success")
                .data(temp)
                .build();
        return result;
    }
}
