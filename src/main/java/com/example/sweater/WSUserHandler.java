package com.example.sweater;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.Map;

@Component
public class WSUserHandler extends TextWebSocketHandler {
    @Autowired
    MessageHandlerForWebUser messageHandlerForWebUser;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Map<String, String> value = new Gson().fromJson(message.getPayload(), Map.class);
        String sendingMessage = value.get("message");
        if (sendingMessage.charAt(0) == '/') messageHandlerForWebUser.handlingCommandsMessage(sendingMessage, session);
        else {
            messageHandlerForWebUser.handlingMessage(sendingMessage, session);
        }
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        messageHandlerForWebUser.handlingCommandsMessage("/close ",session);
    }

}
