package org.glassfish.tyrus.sample.echo;

import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/echo")
public class EchoEndpoint {

    @OnOpen
    public void onOpen(Session session) throws IOException {
        session.getBasicRemote().sendText("onOpen");
        System.out.println("EchoEndpoint onOpen connection request...");
    }

    @OnMessage
    public void echo(Session session, String message) throws IOException {
        session.getBasicRemote().sendText(message + " (from Siva server )");
        System.out.println("EchoEndpoint onMessage: " + message);
        if (message.contains("bye")) {
            session.close();
        }
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }
}
