package com.lenovohit.lrouter_api.utils;

import android.content.Context;

import com.lenovohit.lrouter_api.core.InjectorPriorityWrapper;

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
                if (entryName.contains("com.lenovohit")){//过滤掉系统的类
                    Class<?> entryClass = Class.forName(entryName, false,classLoader);
                    if (entryName.contains("Provider$$Inject")){
                        clazzs.add(new InjectorPriorityWrapper(InjectorPriorityWrapper.PROVIDER_PRIORITY,entryClass));
                    }else if (entryName.contains("Action$$Inject")){
                        clazzs.add(new InjectorPriorityWrapper(InjectorPriorityWrapper.ACTION_PRIORITY,entryClass));
                    }else if(entryName.contains("$$Inject")){
                        ((Injector)entryClass.newInstance()).inject();
                    }
                }
            }

            //进行实例化
            if (null != clazzs && clazzs.size() > 0){
                Collections.sort(clazzs);
                for (int i = 0; i < clazzs.size(); i ++){
                    InjectorPriorityWrapper clazz = clazzs.get(i);
                    ((Injector)clazz.mClass.newInstance()).inject();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return clazzs;
    }
}
