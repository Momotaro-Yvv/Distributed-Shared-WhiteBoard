package com.example.distributedsharedwhiteboard.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteObj extends Remote{
     public String Hello(String keywords) throws RemoteException;
}
