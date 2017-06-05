package com.lenovohit.lrouter_api.intercept;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.CodeSignature;

/**
 * 拦截器
 * Created by yuzhijun on 2017/6/5.
 */
public abstract class AopInterceptor {

    public static void enterMethod(JoinPoint joinPoint){
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();

        String methodName = codeSignature.getName();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();


    }

    public static void exitMethod(JoinPoint joinPoint,long lengthMillis){

    }


}
