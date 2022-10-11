package com.example.distributedsharedwhiteboard.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteObj extends Remote{

     // dummy function for testing
     public String Hello(String keywords) throws RemoteException;


//     public String _updateUserList (String )
}
