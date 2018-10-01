package com.example.sweater;

import com.example.sweater.Client.Agent;
import com.example.sweater.Client.Client;
import com.example.sweater.Client.User;
import com.example.sweater.Client.AgentInterface;
import com.example.sweater.Client.UserInterfece;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.logging.Logger;

public class MessageHandler {
    private static final Logger log = Logger.getLogger(String.valueOf(MessageHandler.class));
    @Autowired
    Base base;

    public void handlingMessage(String sendingMessage, WebSocketSession webSession) {
        Client client = base.searchClientBySession(webSession);
        try {
            if (client != null) onMessageReseived(sendingMessage, client);
            else {
                webSession.sendMessage(new TextMessage("server: You are not registered"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handlingCommandsMessage(String m, WebSocketSession session) throws IOException {
        if (m.matches("/reg(\\s+)user(\\s+)\\w+")) {
            UserInterfece u;
            if ((u = base.searchUserBySession(session)) != null) {
                u.sendMessageToMyself("server: You are aleady registered");
            } else {
                String clientName = m.split("/reg(\\s+)user(\\s+)")[1];
                log.info("UserNAME:" + clientName);
                User user = new User(clientName, session);
                base.addUser(user);
                user.sendMessageToMyself("server: You have registered");
            }

        } else if (m.matches("/reg(\\s+)agent(\\s+)\\w+")) {
            AgentInterface a;
            if ((a = base.searchAgentOnSession(session)) != null) {
                a.sendMessageToMyself("server: You are aleady registered");
            } else {
                String clientName = m.split("/reg(\\s+)agent(\\s+)")[1];
                log.info("AgentNAME:" + clientName);
                Agent agent = new Agent(clientName, session);
                base.addAgent(agent);
                agent.sendMessageToMyself("server: You have registered");
                base.addToQWaitingAgent(agent);
                base.chatCreation();
            }
        } else if (m.matches("/leave(\\s*)")) {
            Client client;
            if ((client = base.searchClientBySession(session)) != null) {
                client.sendMessageToMyself("server: You left the chat with the previous companion");
                base.leaveChat(client);
            } else {
                session.sendMessage(new TextMessage("server: register please"));
            }
        } else if (m.matches("/close(\\s*)")) {
            base.exit(session);
        } else {
            session.sendMessage(new TextMessage("server: Uncorrect command"));
        }
    }
    public void onMessageReseived(String message, Client client) throws IOException {
        User freeUser = null;
        AgentInterface freeAgent;
        if (!client.isBusy()) {
            client.sendMessageToMyself("server: Waiting for a companion");
            if (client instanceof User) {
                base.addToQWaitingUsers((User) client);
                if ((freeAgent = base.getQueueOfWaitingAgents().peekFirst()) != null) {
                    client.setCompanion(freeAgent);
                    freeAgent.setCompanion(client);
                    freeUser = (User) client;
                    freeUser.sendMessageToMyself("server: You are connected to " + freeAgent.getName());
                    log.info("---------" + freeUser.getWaitingMessages() + message);
                    freeUser.sendMessage(freeUser.getWaitingMessages() + message);
                    if (freeUser.isWaiting())
                        freeUser.clearBuffer();
                    base.getQueueOfWaitingAgents().removeFirst();
                    base.getQueueOfWaitingUsers().removeFirst();
                } else {
                    ((User) client).setBufferMessages(message);
                    ((User) client).sendMessageToMyself(message);
                }
            }
        } else {
            client.sendMessage(message);
            log.info("---------" + message);
        }
    }
}
