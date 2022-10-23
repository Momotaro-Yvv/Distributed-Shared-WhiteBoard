package com.example.distributedsharedwhiteboard.Application;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Manager extends User {

    //Constructors
    public Manager (String username, Socket socket) throws IOException {
        super(username, socket);
    };

    //Getters

    //Setters

    //Methods
    void approveJoinRequest(boolean decision){};

    void sendKickUserMsg(String username){};

    void sendTerminateMsg(){};

    void sendReloadRequest(){}

}
