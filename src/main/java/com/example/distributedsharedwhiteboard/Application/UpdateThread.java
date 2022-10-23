package com.example.distributedsharedwhiteboard.Application;

import com.example.distributedsharedwhiteboard.Logger;
import com.example.distributedsharedwhiteboard.Util.JsonSerializationException;
import com.example.distributedsharedwhiteboard.message.ApproveRequest;
import com.example.distributedsharedwhiteboard.message.ErrorMsg;
import com.example.distributedsharedwhiteboard.message.Message;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.BasicPermission;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.LinkedBlockingDeque;

import static com.example.distributedsharedwhiteboard.Util.util.readMsg;
import static com.example.distributedsharedwhiteboard.Util.util.writeMsg;

public class UpdateThread extends Thread {
    private LinkedBlockingDeque<Socket> incomingConnections;
    private Logger logger;

    public UpdateThread(LinkedBlockingDeque<Socket> incomingConnections, Logger logger) {
        this.incomingConnections = incomingConnections;
        this.logger = logger;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Socket socket = incomingConnections.take();
                processUpdate(socket);
            } catch (InterruptedException e) {
                logger.logWarn("Update Thread got interrupted.");
                throw new RuntimeException(e);
            }
        }
    }

    private void processUpdate(Socket socket){
        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            // keep the channel up for communication with server
            while (true){
                Message messageFromServer;
                try {
                    messageFromServer = readMsg(bufferedReader);
                } catch (JsonSerializationException e1) {
                    writeMsg(bufferedWriter,new ErrorMsg("Invalid message"));
                    return;
                }
                if (!messageFromServer.getClass().getName().equals(ApproveRequest.class.getName())){
                    ApproveRequest approveRequest = (ApproveRequest) messageFromServer;
                    String userJoining = approveRequest.username;
                    Boolean approve = showJoinRequest(userJoining);
                    
                    writeMsg(bufferedWriter, new ApproveRequest(approve));
                }
            }
        } catch (IOException e) {
            logger.logWarn("Update thread received IO exception on socket.");
            throw new RuntimeException(e);
        }


    }
}