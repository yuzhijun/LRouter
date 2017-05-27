package com.lenovohit.lrouter_api.utils;



/**
 * Created by yuzhijun on 2017/5/27.
 */
public class LRLoggerFactory {

    public static boolean isNeedLog;
    public static ILRLogger.LogLevel minLevel;

    static {
        isNeedLog = false;
        minLevel = ILRLogger.LogLevel.DBUG;
    }

    public static ILRLogger getLRLogger(String tag) {
        return getLRLogger(tag, null);
    }

    public static ILRLogger getLRLogger(Class<?> cls) {
        return getLRLogger(null, cls);
    }

    private static ILRLogger getLRLogger(String tag, Class<?> cls) {
        return cls != null ? new LRLogger((Class) cls) : new LRLogger(tag);
    }
}
