package com.example.distributedsharedwhiteboard.Application.Models;

import java.net.InetAddress;

public class Manager extends User{
    Boolean isManager = true;

    //Constructors
    public Manager (InetAddress srvAddress, int srvPort, String username, int id){
        super(srvAddress,srvPort,username,id);
    };

    //Getters

    //Setters

    //Methods

    void sendKickUserMsg(){};

    void sendTerminateMsg(){};

}
