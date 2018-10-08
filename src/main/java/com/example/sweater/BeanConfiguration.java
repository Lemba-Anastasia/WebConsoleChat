package com.example.sweater;

import com.example.sweater.consoleServer.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    Base base(){
        return new Base();
    }
    @Bean
    Server server(){return  new Server();}

    @Bean
    MessageHandler messageHandler(){
        return new MessageHandler();
    }

    @Bean
    WSAgentHandler socketHandlerAgent(){
        return new WSAgentHandler();
    }

    @Bean
    WSUserHandler socketHandlerUser(){
        return new WSUserHandler();
    }

}
