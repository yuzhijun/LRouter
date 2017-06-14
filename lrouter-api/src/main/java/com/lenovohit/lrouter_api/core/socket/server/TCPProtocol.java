package com.lenovohit.lrouter_api.core.socket.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * TCPServerSelector与特定协议间通信的接口
 * Created by yuzhijun on 2017/6/13.
 */
public interface TCPProtocol {
    /**
     * 接收一个SocketChannel的处理
     * @param key
     * @throws IOException
     */
    void handleAccept(SelectionKey key) throws IOException;
    /**
     * 从一个SocketChannel读取信息的处理
     * @param key
     * @throws IOException
     */
    void handleRead(SelectionKey key) throws IOException;
    /**
     * 向一个SocketChannel写入信息的处理
     * @param key
     * @throws IOException
     */
    void handleWrite(SelectionKey key) throws IOException;

}
