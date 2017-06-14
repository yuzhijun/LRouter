package com.lenovohit.lrouter_api.core.socket.client;

import android.os.Handler;
import android.os.Message;

import com.lenovohit.lrouter_api.core.socket.MsgObject;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 客户端发送线程(有消息要发送先进入队列，队列如果有要发送的消息则再进行发送)
 * Created by yuzhijun on 2017/6/13.
 */
public class LRSocketSendThread extends Thread{
    private static final String TAG = "LRSocketSendThread";
    private boolean mStart = true;
    private List<MsgObject> sendMsgQueue;
    private LRSocketClient mLRSocketClient;

    public LRSocketSendThread(LRSocketClient socketClient){
        this.mLRSocketClient = socketClient;
        sendMsgQueue = new CopyOnWriteArrayList<>();
    }

    //设置开始发送消息线程
    public void setStart(boolean isStart) {
        this.mStart = isStart;
        synchronized (this) {
            notify();
        }
    }

    // 使用socket发送消息
    public void addMsg2Queue(MsgObject msg) {
        synchronized (this) {
            this.sendMsgQueue.add(msg);
            notify();
        }
    }

    // 使用socket发送消息
    public boolean sendMsg(byte[] msg) throws Exception {
        if (msg == null) {
            return false;
        }

        try {
            mLRSocketClient.sendMsg(msg);
        } catch (Exception e) {
            throw (e);
        }
        return true;
    }

    @Override
    public void run() {
        while (mStart){
            synchronized (sendMsgQueue){
                for (MsgObject msg : sendMsgQueue){
                    Handler handler = msg.getHandler();
                    try {
                        sendMsg(msg.getBytes());
                        sendMsgQueue.remove(msg);
                        // 成功消息，通过hander回传
                        if (handler != null) {
                            Message message = new Message();
                            message.obj = msg.getBytes();
                            message.what =1;
                            handler.sendMessage(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // 错误消息，通过hander回传
                        if (handler != null) {
                            Message message =  new Message();
                            message.obj = msg.getBytes();
                            message.what = 0;
                            handler.sendMessage(message);
                        }
                    }
                }
            }

            synchronized (this){// 发送完消息后，线程进入等待状态
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
