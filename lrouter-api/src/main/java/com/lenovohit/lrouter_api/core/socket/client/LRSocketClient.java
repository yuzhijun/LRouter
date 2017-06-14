package com.lenovohit.lrouter_api.core.socket.client;

import com.lenovohit.lrouter_api.core.socket.Const;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * 用于跟服务端(各个模块)连接
 * Created by yuzhijun on 2017/6/13.
 */
public class LRSocketClient {
    // 信道选择器
    private Selector selector;
    // 与服务器通信的信道
    SocketChannel socketChannel;
    //连接的服务器IP地址
    private String hostIP;
    //连接的服务器监听的接口
    private int hostPort;

    private boolean init = false;

    public LRSocketClient(String hostIP,int hostPort){
        this.hostIP = hostIP;
        this.hostPort = hostPort;

        try{
            init();
            this.init = true;
        }catch (IOException e){
            this.init = false;
            e.printStackTrace();
        }catch (Exception e){
            this.init = false;
            e.printStackTrace();
        }
    }

    /**
     * 初始化
     * @throws IOException
     * */
    public void init() throws IOException{
        boolean done = false;

        try {
            // 打开监听信道并设置为非阻塞模式
            socketChannel = SocketChannel.open(new InetSocketAddress(hostIP, hostPort));
            if (socketChannel != null) {
                socketChannel.socket().setTcpNoDelay(false);
                socketChannel.socket().setKeepAlive(true);
                // 设置 读socket的timeout时间
                socketChannel.socket().setSoTimeout(Const.SOCKET_READ_TIMOUT);
                socketChannel.configureBlocking(false);

                // 打开并注册选择器到信道
                selector = Selector.open();
                if (selector != null) {
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    done = true;
                }
            }
        }finally {
            if (!done && selector != null) {
                selector.close();
            }
            if (!done) {
                socketChannel.close();
            }
        }
    }

    /**
     * 发送数据
     * @param bytes
     * @throws IOException
     */
    public void sendMsg(byte[] bytes) throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.wrap(bytes);

        if (socketChannel == null) {
            throw new IOException();
        }
        socketChannel.write(writeBuffer);
    }

    /**
     * Socket连接是否是正常的
     */
    public boolean isConnect() {
        boolean isConnect = false;
        if (this.init) {
            isConnect =  this.socketChannel.isConnected();
        }
        return isConnect;
    }

    public synchronized Selector getSelector() {
        return this.selector;
    }

    /**
     * 服务器是否关闭，通过发送一个socket信息
     *
     * @return
     */
    public boolean canConnectToServer() {
        try {
            if (socketChannel != null) {
                socketChannel.socket().sendUrgentData(0xff);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 关闭socket 重新连接
     * @return
     */
    public boolean reConnect() {
        closeTCPSocket();
        try {
            init();
            init = true;
        } catch (IOException e) {
            init = false;
            e.printStackTrace();
        }
        catch (Exception e) {
            init = false;
            e.printStackTrace();
        }
        return init;
    }

    /**
     * 关闭socket
     */
    public void closeTCPSocket() {
        try {
            if (socketChannel != null) {
                socketChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (selector != null) {
                selector.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
