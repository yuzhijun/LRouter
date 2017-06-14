package com.lenovohit.lrouter_api.core.socket;

import android.os.Handler;

import com.lenovohit.lrouter_api.core.callback.IRequestCallBack;
import com.lenovohit.lrouter_api.core.socket.client.LRSocketClient;
import com.lenovohit.lrouter_api.core.socket.client.LRSocketReceiveThread;
import com.lenovohit.lrouter_api.core.socket.client.LRSocketSendThread;

/**
 * 线程管理器
 * Created by yuzhijun on 2017/6/13.
 */
public class LRSocketThreadManager {
    private LRSocketThreadManager mSocketThreadManager = null;
    private LRSocketReceiveThread mReceiveThread = null;
    private LRSocketSendThread mSendThread = null;
    private LRSocketClient mLRSocketClient = null;

    public LRSocketThreadManager(String hostIP,int hostPort,IRequestCallBack requestCallBack){
        mLRSocketClient = new LRSocketClient(hostIP,hostPort);
        mReceiveThread = new LRSocketReceiveThread(hostIP,hostPort,mLRSocketClient,requestCallBack);
        mSendThread = new LRSocketSendThread(hostIP,hostPort,mLRSocketClient);
        startThreads();
    }

    private void startThreads(){
        mReceiveThread.start();
        mSendThread.start();
        mReceiveThread.setStart(true);
        mSendThread.setStart(true);
    }

    public void stopThreads() {
        mReceiveThread.setStart(false);
        mSendThread.setStart(false);
    }

    public  void releaseInstance() {
        if (mSocketThreadManager != null) {
            mSocketThreadManager.stopThreads();
            mSocketThreadManager = null;
        }
    }

    public void sendMsg(byte [] buffer, Handler handler) {
        MsgObject entity = new MsgObject(buffer, handler);
        mSendThread.addMsg2Queue(entity);
    }
}
