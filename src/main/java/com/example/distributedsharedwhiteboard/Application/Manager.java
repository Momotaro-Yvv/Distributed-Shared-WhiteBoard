package com.example.distributedsharedwhiteboard.Application;

import java.net.InetAddress;

public class Manager extends User {

    //Constructors
    public Manager (String username){
        super(username);
    };

    //Getters

    //Setters

    //Methods
    void approveJoinRequest(boolean decision){};

    void sendKickUserMsg(String username){};

    void sendTerminateMsg(){};

}
