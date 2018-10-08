package com.example.sweater.Client;

import com.example.sweater.Client.Client;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public interface AgentInterface extends Client {
    List<UserInterfece> getUsers();
}
