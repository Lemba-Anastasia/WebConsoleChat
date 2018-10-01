package com.example.sweater.Client;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class User implements UserInterfece {
    private WebSocketSession socketSession;
    private String name;
    private AgentInterface companion;
    private String waitingPutMessages;

    public User(String clientName, WebSocketSession session) {
        this.name = clientName;
        this.socketSession = session;
        waitingPutMessages = "";
    }

    public void setBufferMessages (String m) {
        waitingPutMessages += name + ": " + m + "\n";
    }

    @Override
    public void clearBuffer() {
        waitingPutMessages = "";
    }

    @Override
    public void sendMessage(String message) throws IOException {
        sendMessageToMyself(name + ": " + message);
        companion.sendMessageToMyself(name + ": " + message);
    }

    @Override
    public boolean isBusy() {
        return (companion != null);
    }

    @Override
    public void sendMessageToMyself(String message) {
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
        this.companion = (AgentInterface) companion;//я вижу косяк
    }

    public boolean isWaiting() {
        return !waitingPutMessages.equals("");
    }
    @Override
    public String getWaitingMessages() {
        return waitingPutMessages;
    }
}
