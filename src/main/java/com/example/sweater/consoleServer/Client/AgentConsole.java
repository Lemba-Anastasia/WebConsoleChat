package com.example.sweater.consoleServer.Client;

import com.example.sweater.Client.AgentInterface;
import com.example.sweater.Client.RESTClient.RestUser;
import com.example.sweater.Client.UserInterfece;
import com.example.sweater.IdCounter;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AgentConsole implements AgentInterface {
    private String name;
    private Socket socket;
    private UserInterfece companion;
    private int id;
    private final List<String> restMessages;

    public AgentConsole(String name, Socket socket) {
        this.name = name;
        this.socket = socket;
        id = IdCounter.getInstance().getId();
        restMessages = new ArrayList<>();
    }

    @Override
    public boolean isBusy() {
        return (!(companion == null));
    }

    public Socket getSocket() {
        return socket;
    }

    public void sendMessage(String message) throws IOException {
        flashRESTChanel();
        if (companion instanceof UserConsole) {
            ((UserConsole) companion).getSocket().getOutputStream().write((name + ": " + message + "\n").getBytes());
            ((UserConsole) companion).getSocket().getOutputStream().flush();
        } else {
            companion.sendMessageToMyself(name + ": " + message);
            if(!(companion instanceof RestUser)){
                companion.setInputMessagesForREST(name + ": " + message);
            }
        }
    }

    @Override
    public void sendMessageToMyself(String message) throws IOException {
        socket.getOutputStream().write((message + "\n").getBytes());
        socket.getOutputStream().flush();

    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public String getName() {
        return name;
    }

    public UserInterfece getCompanion() {
        return companion;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentConsole agent = (AgentConsole) o;

        if (!name.equals(agent.getName())) return false;
        return socket == agent.getSocket();
    }

    public void setCompanion(UserInterfece freeUser) {
        companion = freeUser;
    }

    @Override
    public void setInputMessagesForREST(String s) {
        synchronized (restMessages) {
            restMessages.add(s);
        }
    }

    @Override
    public List<String> getRESTInputMessages() {
        synchronized (restMessages) {
            return restMessages;
        }
    }

    @Override
    public void flashRESTChanel()  {
        restMessages.clear();
    }

    @Override
    public String toString() {
        return "ConsoleAgent{" +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", companion=" + companion +
                '}';
    }
}
