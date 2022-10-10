package com.example.distributedsharedwhiteboard.client;

import com.example.distributedsharedwhiteboard.server.IRemoteObj;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIclient {
    public static void main(String[] args) throws NotBoundException, RemoteException {
        Registry registry = LocateRegistry.getRegistry("127.0.0.1",1099);
        IRemoteObj remoteObj = (IRemoteObj) registry.lookup("remoteObj");
        remoteObj.Hello("hi there!");
    }
}
