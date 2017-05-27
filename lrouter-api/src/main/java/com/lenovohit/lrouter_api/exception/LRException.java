package com.lenovohit.lrouter_api.exception;

/**
 * 自定义的异常
 * Created by yuzhijun on 2017/5/27.
 */
public class LRException extends RuntimeException {

   public LRException(String errorMessage){
        super(errorMessage);
    }

    public LRException(String errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
    }

    //TODO
}
