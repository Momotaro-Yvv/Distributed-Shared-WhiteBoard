package com.example.distributedsharedwhiteboard.Application;

import com.example.distributedsharedwhiteboard.Logger;
import com.example.distributedsharedwhiteboard.ShapeDrawing.ShapeDrawing;
import com.example.distributedsharedwhiteboard.Util.JsonSerializationException;
import com.example.distributedsharedwhiteboard.message.ApproveRequest;
import com.example.distributedsharedwhiteboard.message.ErrorMsg;
import com.example.distributedsharedwhiteboard.message.*;
import javafx.collections.ObservableList;

import javafx.application.Platform;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.BasicPermission;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.LinkedBlockingDeque;

import static com.example.distributedsharedwhiteboard.Util.util.*;

public class UpdateThread extends Thread {
    private Logger logger;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ObservableList<String> eventList;

    private ObservableList<ShapeDrawing> undrawedList;
    private ObservableList<String> msgList;
    private ObservableList<String> userList;
    private String username;

    public UpdateThread(String username, BufferedReader bufferedReader, BufferedWriter bufferedWriter, Logger logger,
                        ObservableList<String> eventList,
                        ObservableList<ShapeDrawing> undrawedList,
                        ObservableList<String> msgList,
                        ObservableList<String> userList) {
        this.username = username;
        this.logger = logger;
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
        this.eventList = eventList;
        this.undrawedList = undrawedList;
        this.msgList = msgList;
        this.userList = userList;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {

            try {
                Message msgFromSvr = readMsg(bufferedReader);
                logger.logDebug(msgFromSvr.toString());

                if (msgFromSvr.getClass().getName() == ApproveRequest.class.getName()) {
                    ApproveRequest approveRequest = (ApproveRequest) msgFromSvr;
                    String userJoining = approveRequest.username;
                    System.out.println("ApproveRequest: " + userJoining + " want to join ");

                    Platform.runLater(() -> {
                        eventList.add("showJoinRequest");
                    });
                } else if (msgFromSvr.getClass().getName() == DrawReply.class.getName()) {
                    DrawReply drawReply = (DrawReply) msgFromSvr;
                    logger.logDebug(drawReply.toString());

                } else if (msgFromSvr.getClass().getName() == SendMsgReply.class.getName()) {
                    logger.logDebug("received SendMsgReply!!!");
                    SendMsgReply sendMsgReply = (SendMsgReply) msgFromSvr;
                    Boolean success = sendMsgReply.success;
                    logger.logDebug(sendMsgReply.toString());


                } else if (msgFromSvr.getClass().getName() == QuitReply.class.getName()) {
                    QuitReply quitReply = (QuitReply) msgFromSvr;
                    if (quitReply.success) {
                        eventList.add("handleQuit");
                        break;
                    }
                } else if (msgFromSvr.getClass().getName() == UpdateMsgRequest.class.getName()) {
                    // add msg to GUI chat window
                    UpdateMsgRequest updateMsgRequest = (UpdateMsgRequest) msgFromSvr;
                    String byWhom = updateMsgRequest.byWhom;
                    if (!byWhom.equals(username)){
                        logger.logDebug(username+ ": The new shape is not from me.....");
                        String msg = updateMsgRequest.msg;
                        String line = byWhom + ": " + msg;
                        Platform.runLater(() -> {
                            msgList.add(line);
                        });
                    };
                } else if (msgFromSvr.getClass().getName() == UpdateDeleteUserRequest.class.getName()) {
                    UpdateDeleteUserRequest updateDeleteUserRequest = (UpdateDeleteUserRequest) msgFromSvr;
                    String userNameToDelete = updateDeleteUserRequest.deleteUserName;
                    if (!userNameToDelete.equals(username)){
                        Platform.runLater(() -> {
                            userList.remove(userNameToDelete);
                        });
                    }

                } else if (msgFromSvr.getClass().getName() == UpdateShapeRequest.class.getName()) {
                    UpdateShapeRequest updateShapeRequest = (UpdateShapeRequest) msgFromSvr;
                    String byWhom = updateShapeRequest.byWhom;
                    String shapeString =  updateShapeRequest.shape;
                    logger.logDebug("username: "+ username + "byWhom: "+ byWhom);
                    if (!byWhom.equals(username)){
                        logger.logDebug("The new shape is not from me.....");
                        ShapeDrawing shapeDrawing = TransferToShape(shapeString);
                        Platform.runLater(() -> {
                            undrawedList.add(shapeDrawing);
                        });
                    }
                } else if (msgFromSvr.getClass().getName() == UpdateUserlistRequest.class.getName()){
                    UpdateUserlistRequest updateUserlistRequest = (UpdateUserlistRequest) msgFromSvr;
                    String newUserName = updateUserlistRequest.newUserName;
                    if(!newUserName.equals(username)){
                        Platform.runLater(() -> {
                            userList.add(newUserName);
                        });
                    }
                } else {
                    logger.logDebug(msgFromSvr.toString());
                }
            } catch (JsonSerializationException j) {
                throw new RuntimeException(j);
            } catch (IOException e) {
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