package com.scalable.demo.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalable.demo.service.MessageResolver;
import com.scalable.demo.service.MessageResolverImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 *  Step 1 : will be to write our own Websockethandler to handle the websocket request and response
 *  Step 2: In config file pass this websocket handler to HandlerMapper in this case-simpleurlhanlder
 *  Step 3:
 */
@Component
public class MyWebSocketHandler implements WebSocketHandler {

    Logger logger = LoggerFactory.getLogger(MyWebSocketHandler.class);

    private MessageResolver messageResolver;
    private ObjectMapper objectMapper;

    @Autowired
    public MyWebSocketHandler(MessageResolver messageResolver, ObjectMapper objectMapper) {
        this.messageResolver = messageResolver;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> messages = session.receive()
                .map(message -> {
                    try {
                        String key = new String(message.getPayload().asInputStream().readAllBytes());
                        String answer = messageResolver.getMessage(key)==null ? "Hello "+key+" how are you? how can i help you?": messageResolver.getMessage(key) ;
                        return objectMapper.writeValueAsString((answer));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return ("I am down currently");
                    }

                })
                .map(session::textMessage);
        return session.send(messages);
    }
}
