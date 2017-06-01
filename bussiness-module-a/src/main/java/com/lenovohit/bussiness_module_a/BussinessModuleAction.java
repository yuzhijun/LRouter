package com.lenovohit.bussiness_module_a;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.lenovohit.lrouter_api.core.LRAction;
import com.lenovohit.lrouter_api.core.LRActionResult;

import java.util.HashMap;

/**
 * Created by yuzhijun on 2017/6/1.
 */

public class BussinessModuleAction extends LRAction {
    @Override
    public boolean needAsync(Context context, HashMap<String, String> requestData) {
        return true;
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
        Toast.makeText(context, "SyncAction.invoke:"+temp, Toast.LENGTH_SHORT).show();
        LRActionResult result = new LRActionResult.Builder()
                .code(LRActionResult.RESULT_SUCESS)
                .msg("success")
                .data(temp)
                .build();
        return result;
    }
}
