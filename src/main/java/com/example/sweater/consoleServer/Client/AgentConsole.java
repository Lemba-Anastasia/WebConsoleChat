package com.example.sweater.consoleServer.Client;

import com.example.sweater.Client.AgentInterface;
import com.example.sweater.Client.Client;

import java.io.IOException;
import java.net.Socket;

public class AgentConsole implements AgentInterface {
    private String name;
    private Socket socket;
    private UserConsole companion;
    public AgentConsole(String name, Socket socket) {
        this.name=name;
        this.socket=socket;
        companion=null;
    }
    @Override
    public boolean isBusy(){
        return (companion!=null);
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void sendMessage(String message) throws IOException {
        if(companion!=null){
            companion.getSocket().getOutputStream().write((name+": "+message+"\n").getBytes());
            companion.getSocket().getOutputStream().flush();
        }
    }

    @Override
    public void sendMessageToMyself(String message) throws IOException {
        try {
            socket.getOutputStream().write((message+"\n").getBytes());
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
    public String getName(){
        return name;
    }

    @Override
    public Client getCompanion() {
        return companion;
    }

    @Override
    public void setCompanion(Client companion) {
        this.companion=(UserConsole) companion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentConsole agent = (AgentConsole) o;

        if (name != agent.getName()) return false;
        return socket == agent.getSocket();
    }
    public void sendByfMessage(String message) throws IOException{
        socket.getOutputStream().write((message+"\n").getBytes());
        socket.getOutputStream().flush();
    }
}
