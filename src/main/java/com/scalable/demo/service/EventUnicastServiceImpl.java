package com.scalable.demo.service;

import com.scalable.demo.model.Event;
import com.scalable.demo.web.DefaultWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.CoreSubscriber;
import reactor.core.Disposable;
import reactor.core.publisher.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Consumer;

@Service
public class EventUnicastServiceImpl implements EventUnicastService {

    Logger logger = LoggerFactory.getLogger(EventUnicastServiceImpl.class);
    private EmitterProcessor<String> processor = EmitterProcessor.create();
    private DirectProcessor<String> processor2 = DirectProcessor.create();


//    private ConnectableFlux<String> connectableFlux = Flux.create(objectFluxSink -> {
//        objectFluxSink = this.processor
//        this.processor.subscribe(x->{
//            return objectFluxSink.next(x);
//        });
//
//    }).publish();

    private  Map<String,String> map = new HashMap();

   @PostConstruct()
   public void initialize(){
       map.put("initial", "Hello, I am bot. What is your Name?");
       map.put("", "hello how are you? Please type \"help\" to chat further");
       map.put("help","how can i help you? Pleae type \"ticket\" or \"problem\" or \"not working\"");
       map.put("ticket", "if you have raised ticket, please share ticket number with me?");
       map.put("not working", "can you please raise ticket via invgate? ");
       map.put("problem","Please contact with xyz@abc.com and do not forget ticket number");


   }
    @Override
    public void onNext(String next) {
            logger.info("nex is called with {}",next.replaceAll("\"",""));
            processor.onNext(map.get(next.replaceAll("\"","")));
            processor2.onNext(map.get(next.replaceAll("\"","")));
    }

    @Override
    public Flux<String> getMessages() {
        return processor2;
    }

    public String getMessage(String key){
       key =  key.replaceAll("\"","").toUpperCase();


       for(String x : map.keySet()){
           StringTokenizer str = new StringTokenizer(key, " \",");
           while(str.hasMoreTokens()){
               if (x.toUpperCase().contains(str.nextToken())){
                   return map.get(x);
               }
           }
       }
       return null;
    }


}
