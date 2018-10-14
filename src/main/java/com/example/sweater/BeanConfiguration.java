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
    MessageHandlerForWebAgent messageHandlerForWebAgent(){
        return new MessageHandlerForWebAgent();
    }

    @Bean
    MessageHandlerForWebUser messageHandlerForWebUser(){
        return new MessageHandlerForWebUser();
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
