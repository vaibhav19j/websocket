package com.scalable.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

@Service
public class MessageResolverImpl implements MessageResolver {

    Logger logger = LoggerFactory.getLogger(MessageResolverImpl.class);

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
