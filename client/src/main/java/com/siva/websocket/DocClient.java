package com.siva.websocket;

import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DocClient {
    private static CountDownLatch messageLatch;
    private static AtomicInteger count = new AtomicInteger();
    private static final String SENT_MESSAGE = "Hello World";

    public static void main(String[] args) {
        try {
            messageLatch = new CountDownLatch(1);


            final ClientEndpointConfig cec = ClientEndpointConfig.Builder
                    .create().configurator(new ClientEndpointConfig.Configurator() {
                        public void beforeRequest(Map<String, List<String>> headers) {
                            System.out.println("Setting user cookie in beforeRequest ...");
                            ArrayList<String> value = new ArrayList<>();
                            value.add("user=TyrusUser");
                            headers.put("cookie", value);
                        }
                    }).build();
            ClientManager client = ClientManager.createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(final Session session, EndpointConfig config) {
                    try {
                        session.addMessageHandler(new MessageHandler.Whole<String>() {

                            public void onMessage(String message) {
                                System.out.println("Received message: "
                                        + message);
                                try {
                                    if (count.get() > 10) {
                                        messageLatch.countDown();
                                        session.getBasicRemote().sendText("bye");
                                    }
                                    session.getBasicRemote().sendText(SENT_MESSAGE + " - " + count.incrementAndGet());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        session.getBasicRemote().sendText(SENT_MESSAGE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, cec, new URI("ws://localhost:8080/echo"));
            messageLatch.await(900, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
