package com.example.sweater;

import com.example.sweater.Client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.logging.Logger;

public class MessageHandlerForWebUser {
    private static final Logger log = Logger.getLogger(String.valueOf(MessageHandlerForWebUser.class));
    @Autowired
    Base base;

    public void handlingMessage(String sendingMessage, WebSocketSession webSession) {
        Client client = base.searchClientBySession(webSession);
        try {
            if (client != null) {
                onMessageReseivedFromUser(sendingMessage, (WebUser) client);
            } else {
                webSession.sendMessage(new TextMessage("server: You are not registered"));
            }
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }

    public void handlingCommandsMessage(String m, WebSocketSession session) throws IOException {
        if (m.matches("/reg(\\s+)user(\\s+)\\w+")) {
            UserInterfece u;
            if ((u = base.searchUserBySession(session)) != null) {
                u.sendMessageToMyself("server: You are already registered");
            } else {
                String clientName = m.split("/reg(\\s+)user(\\s+)")[1];
                log.info("UserNAME:" + clientName);
                WebUser webUser = new WebUser(clientName, session);
                base.addUser(webUser);
                webUser.sendMessageToMyself("server: You have registered");
            }

        } else if (m.matches("/leave(\\s*)")) {
            Client client;
            if ((client = base.searchClientBySession(session)) != null) {
                client.sendMessageToMyself("server: You left the chat with the previous companion");
                base.leaveChat(client);
            } else {
                session.sendMessage(new TextMessage("server: to register please"));
            }
        } else if (m.matches("/close(\\s*)")) {
            base.exit(session);
        } else if (m.matches("/leaveCurrentChat:(\\d+)")) {
            Client client;
            if ((client = base.searchClientBySession(session)) != null) {
                base.leaveCurrentChat(Integer.parseInt(m.split(":")[1]), (WebAgent) client);
            } else {
                session.sendMessage(new TextMessage("server: to register please"));
            }
        } else {
            session.sendMessage(new TextMessage("server: Uncorrected command"));
        }
    }

    public void onMessageReseivedFromUser(String message, WebUser client) throws IOException {
        if (!client.isBusy()) {
            base.addToQWaitingUsers(client);
            client.sendMessageToMyself("server: Waiting for a companion");
            client.setBufferMessages(message);
            client.sendMessageToMyself(client.getWaitingMessages());
            base.chatCreation();
            log.info("---------" + message + " from user " + client.getName());
        } else {
            client.sendMessage(client.getID() + "::" + client.getName() + ": " + message);
            log.info("---------" + message);
        }
    }
}
