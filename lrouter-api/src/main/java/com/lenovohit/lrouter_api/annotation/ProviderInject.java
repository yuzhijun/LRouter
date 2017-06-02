package com.lenovohit.lrouter_api.annotation;

import com.lenovohit.lrouter_api.annotation.ioc.Provider;
import com.lenovohit.lrouter_api.base.LRouterAppcation;
import com.lenovohit.lrouter_api.core.LRProvider;
import com.lenovohit.lrouter_api.core.LocalRouter;
import com.lenovohit.lrouter_api.exception.LRException;
import com.lenovohit.lrouter_api.utils.ILRLogger;
import com.lenovohit.lrouter_api.utils.LRLoggerFactory;

/**
 * 用于provider的注解注入
 * Created by yuzhijun on 2017/6/2.
 */
public class ProviderInject {
    private static final String TAG = "ProviderInject";

    public static void injectProvider(LRProvider lrProvider){
        try{
            if (null == lrProvider){
                throw new LRException("注入的provider不能为空");
            }
            Class<? extends LRProvider> clazz = lrProvider.getClass();
            //查询provider类上是否有@Provider注解
            Provider provider = clazz.getAnnotation(Provider.class);
            if (null != provider){//如果存在provider注解则进行添加
                String name = provider.name();
                LocalRouter.getInstance(LRouterAppcation.getInstance()).registerProvider(name,lrProvider);
            }
        }catch (Exception e){
            e.printStackTrace();
            LRLoggerFactory.getLRLogger(TAG).log("注入provider失败", ILRLogger.LogLevel.ERROR);
        }
    }
}
