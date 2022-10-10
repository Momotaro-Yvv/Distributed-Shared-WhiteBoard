package com.example.distributedsharedwhiteboard.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIserver {
    public static void main(String[] args) throws RemoteException, AlreadyBoundException {
        IRemoteObj remoteObj = new RemoteImplement();
        Registry r = LocateRegistry.createRegistry(1099);
        r.bind("remoteObj", remoteObj);
    }
}
