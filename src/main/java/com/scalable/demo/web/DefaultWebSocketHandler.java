package com.scalable.demo.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalable.demo.service.EventUnicastService;
import com.scalable.demo.service.EventUnicastServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class DefaultWebSocketHandler implements WebSocketHandler {

    private EventUnicastServiceImpl eventUnicastService;


    Logger logger = LoggerFactory.getLogger(DefaultWebSocketHandler.class);

    private ObjectMapper objectMapper;

    @Autowired
    public DefaultWebSocketHandler(EventUnicastServiceImpl eventUnicastService, ObjectMapper objectMapper) {
        this.eventUnicastService = eventUnicastService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> messages = session.receive()
//                 .doOnNext(message -> {
//                     try {
//                         logger.info("reading the message {}", message);
//                         String key = new String(message.getPayload().asInputStream().readAllBytes());
//                         this.eventUnicastService.onNext(key);
//                     } catch (IOException e) {
//                         e.printStackTrace();
//                     }
//                 })
                .map(message -> {
                    // or read message here
                    try {
                        String key = new String(message.getPayload().asInputStream().readAllBytes());
                        logger.info("key is "+key);

                        String answer = eventUnicastService.getMessage(key)==null ? "Hello "+key+" how are you? how can i help you?":eventUnicastService.getMessage(key) ;
                        return objectMapper.writeValueAsString((answer));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return ("I am down currently");
                    }

                })
//                .flatMap(o -> {
//                    try {
//                        logger.info("returning {}", o);
//                        logger.info("returning {}", objectMapper.writeValueAsString(o));
//                        return Mono.just(objectMapper.writeValueAsString(o));
//                    } catch (JsonProcessingException e) {
//                        return Mono.error(e);
//                    }
//                })
                .map(session::textMessage);
        return session.send(messages);
    }
}
