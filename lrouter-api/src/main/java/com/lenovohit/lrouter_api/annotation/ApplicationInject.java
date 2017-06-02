package com.lenovohit.lrouter_api.annotation;

import com.lenovohit.lrouter_api.annotation.ioc.Application;
import com.lenovohit.lrouter_api.base.AnologyApplication;
import com.lenovohit.lrouter_api.base.LRouterAppcation;
import com.lenovohit.lrouter_api.exception.LRException;
import com.lenovohit.lrouter_api.utils.ILRLogger;
import com.lenovohit.lrouter_api.utils.LRLoggerFactory;

/**
 * 用于类application的注入
 * Created by yuzhijun on 2017/6/2.
 */
public class ApplicationInject {
    private static final String TAG = "ApplicationInject";

    public static void injectApplicaiton(Class<? extends AnologyApplication> clazz){
        try{
            if (null == clazz){
                throw new LRException("注入的Clazz不能为空");
            }

            //注入的类是否有Application注解
            Application application = clazz.getAnnotation(Application.class);
            if (null != application){
                String name = application.name();
                int priority = application.priority();

                LRouterAppcation.getInstance().registerAnologyApplication(name,priority,clazz);
            }
        }catch (Exception e){
            e.printStackTrace();
            LRLoggerFactory.getLRLogger(TAG).log("注入类Application失败", ILRLogger.LogLevel.ERROR);
        }
    }
}
