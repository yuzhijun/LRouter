package com.lenovohit.lrouter_api.intercept.ioc;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * 拦截界面请求
 * Created by yuzhijun on 2017/6/9.
 */
@Target({TYPE, METHOD, CONSTRUCTOR}) @Retention(CLASS)
public @interface Reach {
}
