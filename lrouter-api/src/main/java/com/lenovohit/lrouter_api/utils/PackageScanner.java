package com.lenovohit.lrouter_api.utils;

import android.content.Context;

import com.lenovohit.lrouter_api.annotation.ApplicationInject;
import com.lenovohit.lrouter_api.annotation.ServiceInject;
import com.lenovohit.lrouter_api.annotation.ioc.Action;
import com.lenovohit.lrouter_api.annotation.ioc.Application;
import com.lenovohit.lrouter_api.annotation.ioc.Interceptor;
import com.lenovohit.lrouter_api.annotation.ioc.Provider;
import com.lenovohit.lrouter_api.annotation.ioc.Service;
import com.lenovohit.lrouter_api.base.AnologyApplication;
import com.lenovohit.lrouter_api.core.InjectorPriorityWrapper;
import com.lenovohit.lrouter_api.core.LocalRouterService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

/**
 * 扫描包下所有类
 * Created by yuzhijun on 2017/6/6.
 */
public class PackageScanner {
    /**
     * 扫描
     * */
    public static List<InjectorPriorityWrapper> scan(Context ctx){
        List<InjectorPriorityWrapper> clazzs = new ArrayList<>();
        try{
            PathClassLoader classLoader = (PathClassLoader) Thread
                    .currentThread().getContextClassLoader();

            DexFile dex = new DexFile(ctx.getPackageResourcePath());
            Enumeration<String> entries = dex.entries();
            while (entries.hasMoreElements()) {
                String entryName = entries.nextElement();
                if (entryName.contains("com.")){//过滤掉系统的类
                    Class<?> entryClass = Class.forName(entryName, false,classLoader);
                    if (null != entryClass.getAnnotation(Provider.class)){//如果有这些个注解则都返回
                        clazzs.add(new InjectorPriorityWrapper(InjectorPriorityWrapper.PROVIDER_PRIORITY,entryClass));
                    }else if ( null != entryClass.getAnnotation(Action.class)){
                        clazzs.add(new InjectorPriorityWrapper(InjectorPriorityWrapper.ACTION_PRIORITY,entryClass));
                    }else if( null != entryClass.getAnnotation(Interceptor.class)){
                        clazzs.add(new InjectorPriorityWrapper(InjectorPriorityWrapper.INTERCEPTOR_PRIORITY,entryClass));
                    }else if(null != entryClass.getAnnotation(Service.class)){
                        ServiceInject.injectService((Class<? extends LocalRouterService>) entryClass);
                    }else if(null != entryClass.getAnnotation(Application.class)){
                        ApplicationInject.injectApplicaiton((Class<? extends AnologyApplication>) entryClass);
                    }
                }
            }

            //进行实例化
            if (null != clazzs && clazzs.size() > 0){
                Collections.sort(clazzs);
                for (int i = 0; i < clazzs.size(); i ++){
                    InjectorPriorityWrapper clazz = clazzs.get(i);
                    clazz.mClass.newInstance();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return clazzs;
    }
}
