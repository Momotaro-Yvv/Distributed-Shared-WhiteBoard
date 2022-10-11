package com.example.distributedsharedwhiteboard.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


/**
 * This is where the server handle all remote calls from clients and return results
 */
public class RemoteImplement extends UnicastRemoteObject implements IRemoteObj {

    public RemoteImplement() throws RemoteException{

    }

    @Override
    // dummy function for testing
    public String Hello(String keywords) throws RemoteException {
        String up = keywords.toUpperCase();
        System.out.println(up);
        return up;
    }

}
