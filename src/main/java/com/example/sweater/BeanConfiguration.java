package com.example.sweater;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    Base base(){
        return new Base();
    }

    @Bean
    MessageHandler messageHandler(){
        return new MessageHandler();
    }

    @Bean
    SocketHandlerAgent socketHandlerAgent(){
        return new SocketHandlerAgent();
    }

    @Bean
    SocketHandlerUser socketHandlerUser(){
        return new SocketHandlerUser();
    }
}
