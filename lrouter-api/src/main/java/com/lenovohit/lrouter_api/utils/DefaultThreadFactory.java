package com.lenovohit.lrouter_api.utils;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池工厂类
 * Created by yuzhijun on 2017/6/7.
 */
public class DefaultThreadFactory implements ThreadFactory {
    private static final String TAG = "DefaultThreadFactory";
    private static final AtomicInteger poolNumber = new AtomicInteger(1);

    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final String namePrefix;

    public DefaultThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = "LRouter task pool No." + poolNumber.getAndIncrement() + ", thread No.";
    }

    @Override
    public Thread newThread(@NonNull Runnable runnable) {
        String threadName = namePrefix + threadNumber.getAndIncrement();
        LRLoggerFactory.getLRLogger(TAG).log( "Thread production, name is [" + threadName + "]", ILRLogger.LogLevel.INFO);
        Thread thread = new Thread(group, runnable, threadName, 0);
        if (thread.isDaemon()) {   //设为非后台线程
            thread.setDaemon(false);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) { //优先级为normal
            thread.setPriority(Thread.NORM_PRIORITY);
        }

        // 捕获多线程处理中的异常
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                LRLoggerFactory.getLRLogger(TAG).log("Running task appeared exception! Thread [" + thread.getName() + "], because [" + ex.getMessage() + "]", ILRLogger.LogLevel.INFO);
            }
        });
        return thread;
    }
}
