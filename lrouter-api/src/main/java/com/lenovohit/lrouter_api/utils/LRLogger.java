package com.lenovohit.lrouter_api.utils;

import android.util.Log;

/**
 * Created by yuzhijun on 2017/5/27.
 */
public class LRLogger implements ILRLogger {
    private final String tag;

    public LRLogger(String _tag) {
        this.tag = _tag;
    }

    public LRLogger(Class<?> classType) {
        this(classType.getSimpleName());
    }

    @Override
    public void log(String msg, LogLevel level) {
        if (!LRLoggerFactory.isNeedLog) return;
        if (level.getLevel() < LRLoggerFactory.minLevel.getLevel()) return;
        switch (level) {
            case DBUG:
                Log.d(tag, msg);
                break;
            case INFO:
                Log.i(tag, msg);
                break;
            case WARN:
                Log.w(tag, msg);
                break;
            case ERROR:
                Log.e(tag, msg);
                break;
            default:
                break;
        }
    }

    @Override
    public void log(String msg, LogLevel level, Throwable th) {
        if (!LRLoggerFactory.isNeedLog) return;
        if (level.getLevel() < LRLoggerFactory.minLevel.getLevel()) return;
        switch (level) {
            case DBUG:
                Log.d(tag, msg, th);
                break;
            case INFO:
                Log.i(tag, msg, th);
                break;
            case WARN:
                Log.w(tag, msg, th);
                break;
            case ERROR:
                Log.e(tag, msg, th);
                break;
            default:
                break;
        }
    }
}
