package com.lenovohit.lrouter_api.core;

import java.util.HashMap;

/**
 * 用于存放Action
 * Created by yuzhijun on 2017/5/27.
 */
public abstract class LRProvider {
    private boolean mValid = true;
    //一个provider有多个action
    private HashMap<String,LRAction> mActions;

    public LRProvider(){
        mActions = new HashMap<>();
    }

    public void registerAction(String actionName,LRAction action){
        mActions.put(actionName,action);
    }

    public LRAction findAction(String actionName){
        if (null == mActions){
            return null;
        }
        return mActions.get(actionName);
    }

    public boolean isValid(){
        return mValid;
    }
}
