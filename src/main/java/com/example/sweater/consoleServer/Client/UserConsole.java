package com.example.sweater.consoleServer.Client;

import com.example.sweater.Client.Client;
import com.example.sweater.Client.UserInterfece;

import java.io.IOException;
import java.net.Socket;

public class UserConsole implements UserInterfece {
    private String name;
    private Socket socket;
    private AgentConsole companion;
    private String waitingPutMessages;
    public UserConsole(String name, Socket socket) {
        this.name=name;
        this.socket=socket;
        companion=null;
        waitingPutMessages="";
    }

    @Override
    public void sendMessage(String message) throws IOException {
        if(companion!=null){
            companion.getSocket().getOutputStream().write((name+": "+message+"\n").getBytes());
            companion.getSocket().getOutputStream().flush();
        }
    }

    @Override
    public void sendMessageToMyself(String message)  {
        try {
            socket.getOutputStream().write((message+"\n").getBytes());
            socket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean hasConnectionObject(Object o) {
        return socket.equals(o);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public boolean isBusy() {
        return (companion!=null);
    }

    @Override
    public void setCompanion(Client companion) {
        this.companion= (AgentConsole) companion;
    }

    public Socket getSocket() {
        return socket;
    }
    @Override
    public Client getCompanion(){
        return companion;
    }
    @Override
    public String getName(){
        return name;
    }

    public void setBufferMessages(String m){
        waitingPutMessages+=name+": "+m+"\n";
    }
    @Override
    public void clearBuffer(){waitingPutMessages="";}

    @Override
    public String getWaitingMessages() {
        return waitingPutMessages;
    }

    public boolean isWaiting(){
        return !waitingPutMessages.equals("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserConsole user = (UserConsole) o;

        if (name != user.getName()) return false;
        return socket == user.getSocket();
    }
}
