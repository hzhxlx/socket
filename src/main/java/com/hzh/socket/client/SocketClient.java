package com.hzh.socket.client;

import lombok.SneakyThrows;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author hzh
 * socket作为客户端，向服务端请求连接
 */
@Component
public class SocketClient implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    public WebSocketClient client;

    private static int count = 0;

    public SocketClient() {
    }

    @Override
    public void afterPropertiesSet() {
        new Thread(() -> {
            try {
                createClient();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void createClient() throws InterruptedException {
        try {
            client = new WebSocketClient(new URI("ws://127.0.0.1:8080/socket"), new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    logger.info("连接成功");
                }

                //接收消息
                @Override
                public void onMessage(String msg) {
                    if (null != msg && !msg.trim().equals("")) {
                        if (msg.equals("over")) {
                            client.close();
                        }
                        try {
                            //对消息做业务处理
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
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
                    new SocketClient().createClient();
                }
            };
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        client.connect();

        while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
            if (count > 0) {
                Thread.sleep(3000000);
            }
            count++;
            logger.info("正在连接");
        }
    }
}
