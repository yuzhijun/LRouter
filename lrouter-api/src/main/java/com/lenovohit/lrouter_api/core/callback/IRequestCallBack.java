package com.lenovohit.lrouter_api.core.callback;

/**
 * 请求的回调接口
 * Created by yuzhijun on 2017/6/7.
 */
public interface IRequestCallBack{
    void onSuccess(String result);
    void onFailure(Exception e);
}
