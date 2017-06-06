package com.lenovohit.lrouter_api.core;

import android.support.annotation.NonNull;

/**
 * 用来排序各个注入控件（因为注入是有先后顺序的有的控件）
 * Created by yuzhijun on 2017/6/6.
 */
public class InjectorPriorityWrapper implements Comparable<InjectorPriorityWrapper>{
    public static final int PROVIDER_PRIORITY = 9999;//provider的优先级
    public static final int ACTION_PRIORITY = 999;//action的优先级
    public static final int INTERCEPTOR_PRIORITY = 99;//interceptor优先级

    public int mPrority = 0;
    public Class<?> mClass = null;

    public InjectorPriorityWrapper(int priority,Class<?> clazz){
        this.mPrority = priority;
        this.mClass = clazz;
    }

    @Override
    public int compareTo(@NonNull InjectorPriorityWrapper o) {
        return o.mPrority - this.mPrority;
    }
}
