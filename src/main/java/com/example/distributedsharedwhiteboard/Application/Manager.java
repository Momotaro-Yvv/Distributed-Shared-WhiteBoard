package com.example.distributedsharedwhiteboard.Application;

import java.net.InetAddress;

public class Manager extends User {
    Boolean isManager = true;

    //Constructors
    public Manager (InetAddress srvAddress, int srvPort, String username, int id){
        super(srvAddress,srvPort,username,id);
    };

    //Getters

    //Setters

    //Methods
    void approveJoinRequest(boolean desicion){};

    void sendKickUserMsg(String username){};

    void sendTerminateMsg(){};

}
