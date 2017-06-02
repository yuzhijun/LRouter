package com.lenovohit.lrouter_api.annotation;

import com.lenovohit.lrouter_api.annotation.ioc.Service;
import com.lenovohit.lrouter_api.core.LocalRouterService;
import com.lenovohit.lrouter_api.core.RemoteRouter;
import com.lenovohit.lrouter_api.exception.LRException;
import com.lenovohit.lrouter_api.utils.ILRLogger;
import com.lenovohit.lrouter_api.utils.LRLoggerFactory;

/**
 * 用于注入本地service的
 * Created by yuzhijun on 2017/6/2.
 */
public class ServiceInject {
    private static final String TAG = "ServiceInject";

    public static void injectService(Class<? extends LocalRouterService> clazz){
        try{
            if (null == clazz){
                throw new LRException("注入的Clazz不能为空");
            }
            //获得到带Service注解
            Service service = clazz.getAnnotation(Service.class);
            if (null != service){
                String name = service.name();
                RemoteRouter.registerLocalRouterServiceConnection(name,clazz);
            }
        }catch (Exception e){
            e.printStackTrace();
            LRLoggerFactory.getLRLogger(TAG).log("注入Service失败", ILRLogger.LogLevel.ERROR);
        }
    }
}
