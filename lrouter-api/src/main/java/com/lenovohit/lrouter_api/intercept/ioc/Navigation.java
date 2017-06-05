package com.lenovohit.lrouter_api.intercept.ioc;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * 用于请求的注解，主要是用于拦截请求做埋点检测
 * Created by yuzhijun on 2017/6/5.
 */
@Target({TYPE, METHOD, CONSTRUCTOR}) @Retention(CLASS)
public @interface Navigation {
}
