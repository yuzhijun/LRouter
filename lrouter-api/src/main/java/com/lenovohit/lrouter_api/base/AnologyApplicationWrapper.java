package com.lenovohit.lrouter_api.base;

/**
 * 用于排序来指定module类application的优先级
 * Created by yuzhijun on 2017/5/27.
 */
public class AnologyApplicationWrapper implements Comparable<AnologyApplicationWrapper>{
    //类application初始化优先级
    public int priority = 0;
    public Class<? extends AnologyApplication> anologyApplicationClass = null;

    public AnologyApplicationWrapper(int priority, Class<? extends AnologyApplication> anologyApplicationClass) {
        this.priority = priority;
        this.anologyApplicationClass = anologyApplicationClass;
    }

    @Override
    public int compareTo(AnologyApplicationWrapper o) {
        return o.priority - this.priority;
    }
}
