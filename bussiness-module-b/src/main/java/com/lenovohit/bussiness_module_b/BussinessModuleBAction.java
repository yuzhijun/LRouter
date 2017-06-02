package com.lenovohit.bussiness_module_b;

import android.content.Context;
import android.text.TextUtils;

import com.lenovohit.lrouter_api.annotation.Action;
import com.lenovohit.lrouter_api.core.LRAction;
import com.lenovohit.lrouter_api.core.LRActionResult;

import java.util.HashMap;

/**
 * Created by yuzhijun on 2017/6/2.
 */
@Action(name = "ModuleBAction",provider = "com.lenovohit.bussiness_module_b.BussinessModuleBProvider")
public class BussinessModuleBAction extends LRAction {
    @Override
    public boolean needAsync(Context context, HashMap<String, String> requestData) {
        return false;
    }

    @Override
    public LRActionResult invoke(Context context, HashMap<String, String> requestData) {
        String temp = "";
        if(!TextUtils.isEmpty(requestData.get("1"))){
            temp+=requestData.get("1");
        }
        if(!TextUtils.isEmpty(requestData.get("2"))){
            temp+=requestData.get("2");
        }
        LRActionResult result = new LRActionResult.Builder()
                .code(LRActionResult.RESULT_SUCESS)
                .msg("success")
                .data(temp)
                .build();
        return result;
    }
}
