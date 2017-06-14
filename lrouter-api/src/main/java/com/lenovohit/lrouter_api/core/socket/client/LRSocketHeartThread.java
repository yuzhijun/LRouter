package com.lenovohit.lrouter_api.core.socket.client;

import com.lenovohit.lrouter_api.core.socket.Const;

/**
 * 心跳线程
 * Created by yuzhijun on 2017/6/14.
 */
public class LRSocketHeartThread extends Thread{
    private LRSocketClient mSocketClient;
    boolean stop = false;

    public LRSocketHeartThread(LRSocketClient socketClient){
        this.mSocketClient = socketClient;
    }

    /**
     * 连接socket到服务器, 并发送初始化的Socket信息
     * @return
     */
    private boolean reConnect() {
        return mSocketClient.reConnect();
    }

    public void stopThread() {
        stop = true;
    }

    @Override
    public void run() {
        stop = false;
        while (!stop) {
            // 发送一个心跳包看服务器是否正常
            boolean canConnectToServer = mSocketClient.canConnectToServer();
            if(canConnectToServer == false){
                reConnect();
            }
            try {
                Thread.sleep(Const.SOCKET_HEART_SECOND * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
