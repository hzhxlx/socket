package com.hzh.socket.client;

import lombok.SneakyThrows;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author hzh
 * socket作为客户端，向服务端请求连接
 */
public class SocketClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);


    public static void createClient() {
        WebSocketClient client = null;
        try {
            client = new WebSocketClient(new URI("ws://127.0.0.1:8080/socket"), new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    logger.info("连接成功");
                }

                //接收消息
                @Override
                public void onMessage(String msg) {

                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    logger.info("连接已关闭");
                }

                @SneakyThrows
                @Override
                public void onError(Exception e) {
                    logger.error("发生错误，关闭");
                    //谨慎加这句  无限循环的可能
                    SocketClient.createClient();
                }
            };
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        client.connect();

        while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
            logger.info("正在连接");
        }
    }
}
