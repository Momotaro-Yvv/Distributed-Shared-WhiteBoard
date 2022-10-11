package com.example.distributedsharedwhiteboard.server;

import com.example.distributedsharedwhiteboard.Logger;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIserver {

    static String svrIPAddress = "127.0.0.1";
    static int svrPort = 1999;
    static Logger logger = new Logger();

    static String welcomeMsg = " --- Welcome to DS White Board Server ---";

    public static void main(String[] args) throws RemoteException, AlreadyBoundException {

        // Initialize a Registry and bind with remoteObj
        IRemoteObj remoteObj = new RemoteImplement();
        Registry r = LocateRegistry.getRegistry(1099);
        r.bind("remoteObj", remoteObj);

        logger.logInfo(welcomeMsg);
        logger.logInfo("Connecting to Server Registry - ["+svrIPAddress+":"+svrPort+"]");
        logger.logDebug("White Board RMI is ready ... Waiting for Connections");
    }
}
