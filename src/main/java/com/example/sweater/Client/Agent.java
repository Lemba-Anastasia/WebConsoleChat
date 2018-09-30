package com.example.sweater.Client;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class Agent implements AgentInterface {
    private WebSocketSession socketSession;
    private String name;
    private User companion;

    public Agent(String clientName, WebSocketSession session) {
        name = clientName;
        socketSession = session;
    }

    @Override
    public void sendMessage(String message) {
        sendMessageToMyself(name + ": " + message);
        companion.sendMessageToMyself(name + ": " + message);
    }

    @Override
    public boolean isBusy() {
        return (companion != null);
    }


    @Override
    public void sendMessageToMyself(String message) {//TODO: remove try/catch block and add "throws"
        try {
            socketSession.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasConnectionObject(Object o) {
        return socketSession.equals(o);
    }

    @Override
    public void close() throws IOException {
        socketSession.close();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Client getCompanion() {
        return (Client) companion;
    }

    @Override
    public void setCompanion(Client companion) {
        this.companion = (User) companion;
    }

}
