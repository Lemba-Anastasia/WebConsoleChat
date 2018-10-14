package com.example.sweater.Client;

import com.example.sweater.IdCounter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class WebUser implements UserInterfece {
    private WebSocketSession socketSession;
    private String name;
    private AgentInterface companion;
    private String waitingPutMessages;
    private int id;

    public WebUser(String clientName, WebSocketSession session) {
        this.name = clientName;
        this.socketSession = session;
        waitingPutMessages = "";
        id= IdCounter.getInstance().getId();
    }
    @Override
    public void setBufferMessages (String m) {
        waitingPutMessages += id + "::"+name + ": " + m + "\n";
    }

    @Override
    public void clearBuffer() {
        waitingPutMessages = "";
    }

    @Override
    public void sendMessage(String message) throws IOException {
        sendMessageToMyself(message);
        companion.sendMessageToMyself( message);
    }

    @Override
    public int getID(){
        return id;
    }

    @Override
    public boolean isBusy() {
        return (companion != null);
    }

    @Override
    public void sendMessageToMyself(String message) throws IOException{
        socketSession.sendMessage(new TextMessage(message));
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
    public AgentInterface getCompanion() {
        return (AgentInterface) companion;
    }

    @Override
    public void setCompanion(AgentInterface companion) {
        this.companion = companion;
    }

    @Override
    public String getWaitingMessages() {
        return waitingPutMessages;
    }
}
