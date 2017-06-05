package com.lenovohit.lrouter_api.intercept;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.concurrent.TimeUnit;

/**
 * Aop拦截request请求
 * Created by yuzhijun on 2017/6/5.
 */
@Aspect
public class RequstAspect {
    //筛选出用Navigation注解的所有方法
    private static final String POINTCUT_METHOD = "execution(@com.lenovohit.lrouter_api.intercept.ioc.Navigation * *(..))";
    //筛选出所有用Navigation注解的所有构造函数
    private static final String POINTCUT_CONSTRUCTOR = "execution(@com.lenovohit.lrouter_api.intercept.ioc.Navigation *.new(..))";

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotationWithNavigation(){}

    @Pointcut(POINTCUT_CONSTRUCTOR)
    public void constructorAnnotaionWithNavigation(){}

    @Around("methodAnnotationWithNavigation() || constructorAnnotaionWithNavigation()")
    public Object requestAndExecute(ProceedingJoinPoint joinPoint) throws Throwable{
        //执行方法前
        AopInterceptor.enterMethod(joinPoint);
        //方法执行
        long startNanos = System.nanoTime();
        Object result = joinPoint.proceed();
        long stopNanos = System.nanoTime();
        long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);
        //执行方法后
        AopInterceptor.exitMethod(joinPoint,lengthMillis);

        return result;
    }
}
