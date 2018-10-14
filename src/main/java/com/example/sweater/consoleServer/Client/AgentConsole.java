package com.example.sweater.consoleServer.Client;

import com.example.sweater.Client.AgentInterface;
import com.example.sweater.Client.UserInterfece;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AgentConsole implements AgentInterface {
    private String name;
    private Socket socket;
    private UserInterfece companion;

    public AgentConsole(String name, Socket socket) {
        this.name = name;
        this.socket = socket;
    }

    @Override
    public boolean isBusy() {
        return (!(companion ==null));
    }

    public Socket getSocket() {
        return socket;
    }

    public void sendMessage(String message) throws IOException {
        if (companion instanceof UserConsole) {
            ((UserConsole)companion).getSocket().getOutputStream().write((name + ": " + message + "\n").getBytes());
            ((UserConsole)companion).getSocket().getOutputStream().flush();
        }else{
            companion.sendMessageToMyself(name + ": " + message);
        }
    }

    @Override
    public void sendMessageToMyself(String message) throws IOException {
        try {
            socket.getOutputStream().write((message + "\n").getBytes());
            socket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasConnectionObject(Object o) {
        return false;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public String getName() {
        return name;
    }

    public UserInterfece getCompanion() {
        return companion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentConsole agent = (AgentConsole) o;

        if (name != agent.getName()) return false;
        return socket == agent.getSocket();
    }

    public void setCompanion(UserInterfece freeUser) {
        companion=freeUser;
    }
}
