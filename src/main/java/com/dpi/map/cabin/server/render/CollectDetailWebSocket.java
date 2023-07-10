package com.dpi.map.cabin.server.render;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.util.MultiValueMap;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;


@ServerEndpoint(path = "/render-message-ws", port = "${ws.render.port1}")
public class CollectDetailWebSocket {

    /**
     * 与某个客户端的连接会话， 需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * concurrent包的线程安全Set，用来存放么个客户端对应的MyWebSocket对象
     */
    private static CopyOnWriteArraySet<CollectDetailWebSocket> webSocketSet = new CopyOnWriteArraySet<>();

    /**
     *建立ws连接前的配置
     */
/*    @BeforeHandshake
    public void handshake(Session session, HttpHeaders headers, @RequestParam String req, @RequestParam MultiValueMap reqMap, @PathVariable String arg, @PathVariable Map pathMap){
        //采用stomp子协议
        session.setSubprotocols("stomp");
        if (!"ok".equals(req)){
            System.out.println("Authentication failed!");
            session.close();
        }
    }*/

    @OnOpen
    public void onOpen(Session session, HttpHeaders headers, @RequestParam String req, @RequestParam MultiValueMap reqMap, @PathVariable String arg, @PathVariable Map pathMap){
        // 添加对象到set中
        webSocketSet.add(this);
        this.session = session;
        System.out.println("new connection");
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        // 从对象集合中删除该连接对象
        webSocketSet.remove(this);
        System.out.println("one connection closed");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(Session session, String msg) {
        System.out.println("接收的消息为：" + msg);
        session.sendText(msg);
    }
    @OnBinary
    public void onBinary(Session session, byte[] bytes) {
        for (byte b : bytes) {
            System.out.println(b);
        }
        session.sendBinary(bytes);
    }

    public void sendMessage(String message) throws IOException {
        this.session.sendText(message);
    }

    /**
     * 群发消息到客户端， 此处是发送给连接的所有用户。
     * 可以实现根据UID发送给指定的用户
     * @param message
     * @throws IOException
     */
    public static void sendInfoToClient(String message) throws IOException {
        for (CollectDetailWebSocket myWebSocket : webSocketSet) {
            myWebSocket.sendMessage(message);
        }
    }


    @OnEvent
    public void onEvent(Session session, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    System.out.println("read idle");
                    break;
                case WRITER_IDLE:
                    System.out.println("write idle");
                    break;
                case ALL_IDLE:
                    System.out.println("all idle");
                    break;
                default:
                    break;
            }
        }
    }
}
