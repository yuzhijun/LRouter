package com.lenovohit.lrouter_api.core.socket.client;

import com.lenovohit.lrouter_api.base.LRouterAppcation;
import com.lenovohit.lrouter_api.core.callback.IRequestCallBack;
import com.lenovohit.lrouter_api.core.socket.Const;
import com.lenovohit.lrouter_api.utils.NetManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

/**
 * 接收线程
 * Created by yuzhijun on 2017/6/13.
 */
public class LRSocketReceiveThread extends Thread{
    private boolean mStart = true;
    private LRSocketClient mSocketClient;
    private IRequestCallBack mRequestCallBack;

    public LRSocketReceiveThread(String hostIP,int hostPort,LRSocketClient socketClient,IRequestCallBack requestCallBack){
        this.mRequestCallBack = requestCallBack;
        this.mSocketClient = socketClient;
        NetManager.getInstance().init(LRouterAppcation.getInstance());
    }

    public void setStart(boolean isStart) {
        this.mStart = isStart;
    }

    @Override
    public void run() {
        while (mStart){
            if (NetManager.getInstance().isNetworkConnected()){
                if (!mSocketClient.isConnect()) {
                    try {
                        sleep(Const.SOCKET_SLEEP_SECOND * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //如果客户端连接上了则开始读数据
                readSocket();
            }
        }
    }

    public void readSocket() {
        Selector selector = mSocketClient.getSelector();
        if (selector == null) {
            return;
        }
        try {
            // 如果没有数据过来则阻塞
            while (selector.select() > 0) {
                for (SelectionKey sk : selector.selectedKeys()) {
                    // 如果该SelectionKey对应的Channel中有可读的数据
                    if (sk.isReadable()) {
                        // 使用NIO读取Channel中的数据
                        SocketChannel sc = (SocketChannel) sk.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        try {
                            sc.read(buffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        buffer.flip();
                        String receivedString = "";
                        // 打印收到的数据
                        try {
                            receivedString = Charset.forName("UTF-8").newDecoder().decode(buffer).toString();
                            if (null != mRequestCallBack){
                                mRequestCallBack.onSuccess(receivedString);
                            }
                        } catch (CharacterCodingException e) {
                            e.printStackTrace();
                            if (null != mRequestCallBack){
                                mRequestCallBack.onFailure(e);
                            }
                        }
                        buffer.clear();
                        buffer = null;
                        try {
                            // 为下一次读取作准备
                            sk.interestOps(SelectionKey.OP_READ);
                            // 删除正在处理的SelectionKey
                            selector.selectedKeys().remove(sk);
                        } catch (CancelledKeyException e) {
                            e.printStackTrace();
                            if (null != mRequestCallBack){
                                mRequestCallBack.onFailure(e);
                            }
                        }
                    }
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            if (null != mRequestCallBack){
                mRequestCallBack.onFailure(e1);
            }
        } catch (ClosedSelectorException e2) {
            e2.printStackTrace();
            if (null != mRequestCallBack){
                mRequestCallBack.onFailure(e2);
            }
        }
    }
}
