package com.lenovohit.lrouter_api.core.socket;

/**
 * 用于放服务器连接的常量
 * Created by yuzhijun on 2017/6/13.
 */
public class Const {
    //读超时时间
    public final static int SOCKET_READ_TIMOUT = 15 * 1000;
    //如果没有连接无服务器。读线程的sleep时间
    public final static int SOCKET_SLEEP_SECOND = 3 ;
    //心跳包发送间隔时间
    public final static int SOCKET_HEART_SECOND = 3 ;
}
