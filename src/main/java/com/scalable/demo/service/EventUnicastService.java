package com.scalable.demo.service;

import com.scalable.demo.model.Event;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public interface EventUnicastService {

    void onNext(String next);

    Flux<String> getMessages();
}
