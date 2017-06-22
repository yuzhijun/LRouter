package com.lenovohit.lrouter_api.core.socket.server;

import android.content.Context;

import com.lenovohit.lrouter_api.core.LRAction;
import com.lenovohit.lrouter_api.core.LRActionResult;
import com.lenovohit.lrouter_api.core.LRouterRequest;
import com.lenovohit.lrouter_api.core.LocalRouter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

/**
 * 服务端socket
 * Created by yuzhijun on 2017/6/13.
 */
public abstract class LRSocketAction<T> extends LRAction<T> {
    private  Selector selector;
    private ServerSocketChannel listenerChannel;
    // 缓冲区大小
    private static final int BufferSize = 1024;

    public LRSocketAction(){
        LocalRouter.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                startServer();
            }
        });
    }

    private synchronized void startServer(){
        try{
            // 创建选择器
            selector= Selector.open();
            // 打开监听信道
            listenerChannel = ServerSocketChannel.open();
            // 设置为非阻塞模式
            listenerChannel.configureBlocking(false);
            // 与本地端口绑定
            listenerChannel.socket().bind(new InetSocketAddress(socketPort()));
            // 将选择器绑定到监听信道,只有非阻塞信道才可以注册选择器.并在注册过程中指出该信道可以进行Accept操作
            listenerChannel.register(selector, SelectionKey.OP_ACCEPT);
            // 创建一个处理协议的实现类,由它来具体操作
            TCPProtocol protocol = new TCPProtocolImpl(BufferSize,this);
            while (selector.select() > 0){
                // 取得迭代器.selectedKeys()中包含了每个准备好某一I/O操作的信道的SelectionKey
                Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
                while (keyIter.hasNext()){
                    SelectionKey key = keyIter.next();
                    try{
                        if(key.isAcceptable()){
                            // 有客户端连接请求时
                            protocol.handleAccept(key);
                        }
                        if(key.isReadable()){
                            // 从客户端读取数据
                            protocol.handleRead(key);
                        }
                        if(key.isValid() && key.isWritable()){
                            // 客户端可写时
                            protocol.handleWrite(key);
                        }
                    }
                    catch(IOException ex){
                        // 出现IO异常（如客户端断开连接）时移除处理过的键
                        keyIter.remove();
                        continue;
                    }
                    // 移除处理过的键
                    keyIter.remove();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if (null != selector){
                    selector.close();
                }
                if (null != listenerChannel){
                    listenerChannel.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean needAsync(Context context, LRouterRequest<T> requestData) {
        return false;
    }

    @Override
    public LRActionResult invoke(Context context, LRouterRequest<T> requestData) {
        return null;
    }

    public abstract String socketInvoke(String receiveStr);
    public abstract int socketPort();
}
