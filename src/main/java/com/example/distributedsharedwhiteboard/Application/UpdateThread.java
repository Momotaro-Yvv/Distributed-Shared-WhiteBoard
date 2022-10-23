package com.example.distributedsharedwhiteboard.Application;

import com.example.distributedsharedwhiteboard.Logger;
import com.example.distributedsharedwhiteboard.Util.JsonSerializationException;
import com.example.distributedsharedwhiteboard.message.ApproveRequest;
import com.example.distributedsharedwhiteboard.message.ErrorMsg;
import com.example.distributedsharedwhiteboard.message.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.BasicPermission;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.LinkedBlockingDeque;

import static com.example.distributedsharedwhiteboard.Util.util.readMsg;
import static com.example.distributedsharedwhiteboard.Util.util.writeMsg;

public class UpdateThread extends Thread {
    private Logger logger;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public UpdateThread(BufferedReader bufferedReader, BufferedWriter bufferedWriter, Logger logger) {
        this.logger = logger;
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Message msgFromSvr = readMsg(bufferedReader);

                if (!msgFromSvr.getClass().getName().equals(ApproveRequest.class.getName())){
                    ApproveRequest approveRequest = (ApproveRequest) msgFromSvr;
                    String userJoining = approveRequest.username;
                    System.out.println("ApproveRequest: " + userJoining + " want to join ");
                    writeMsg(bufferedWriter,new ApproveReply(true));
                }

            } catch (JsonSerializationException e1) {

                try {
                    writeMsg(bufferedWriter, new ErrorMsg("Invalid message"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                logger.logWarn("Invalid message");
                return;
            } catch (IOException e) {
//                logger.logWarn("Something went wrong with the connection.");
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
//                    Boolean approve = showJoinRequest(userJoining);
                    
//                    writeMsg(bufferedWriter, new ApproveRequest(approve));
                }
            }
        } catch (IOException e) {
            logger.logWarn("Update thread received IO exception on socket.");
            throw new RuntimeException(e);
        }


    }
}