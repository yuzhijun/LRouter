package com.lenovohit.rxlrouter_api.intercept;

import android.content.Context;

import com.lenovohit.lrouter_api.core.LRouterRequest;

import io.reactivex.Observable;

/**
 * 定义执行的接口
 * Created by yuzhijun on 2017/6/9.
 */
public interface IRxNavigation {
    Observable<String> rxNavigation(Context context, LRouterRequest request) throws Exception;
}
