package com.example.sweater;

import com.example.sweater.Client.*;
import com.example.sweater.Client.WebAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;

public class MessageHandlerForWebAgent {
    private static final Logger log = Logger.getLogger(String.valueOf(MessageHandlerForWebAgent.class));
    @Autowired
    Base base;

    public void handlingMessage(String sendingMessage, WebSocketSession webSession) {
        Client client = base.searchClientBySession(webSession);
        try {
            if (client != null) {
                onMessageReseivedFromAgent(sendingMessage, (WebAgent) client);
            } else {
                webSession.sendMessage(new TextMessage("server: You are not registered"));
            }
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }

    public void handlingCommandsMessage(String m, WebSocketSession session) throws IOException {
        if (m.matches("/reg(\\s+)agent(\\s+)\\w+(\\s+)\\d+")) {
            AgentInterface a;
            int maxCountOfUsers;
            if ((a = base.searchAgentOnSession(session)) != null) {
                a.sendMessageToMyself("server: You are already registered");
            } else {
                String clientName = m.split("/reg(\\s+)agent(\\s+)")[1].split("(\\s+)\\d+")[0];
                maxCountOfUsers = parseInt(m.split("/reg(\\s+)agent(\\s+)\\w+(\\s+)")[1]);
                log.info("AgentNAME:" + clientName);
                WebAgent webAgent = new WebAgent(clientName, session, maxCountOfUsers);
                base.addAgent(webAgent);
                webAgent.sendMessageToMyself("server: You have registered");
                base.addToQWaitingAgent(webAgent);
                base.chatCreation();
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

    public void onMessageReseivedFromAgent(String message, WebAgent client) throws IOException {
        log.info("---------" + message + " from agent " + client);
        if (!client.getUsers().isEmpty()) {
            String[] strings = message.split("::");
            client.sendMessage(strings[1], Integer.parseInt(strings[0]));
        }
    }
}