package com.lenovohit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yuzhijun on 2017/6/12.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface IntentInterceptor {
}
