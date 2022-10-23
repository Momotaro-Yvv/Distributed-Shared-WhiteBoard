package com.example.distributedsharedwhiteboard.Application;

import com.example.distributedsharedwhiteboard.Logger;
import com.example.distributedsharedwhiteboard.ShapeDrawing.ShapeDrawing;
import com.example.distributedsharedwhiteboard.Util.JsonSerializationException;
import com.example.distributedsharedwhiteboard.message.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.example.distributedsharedwhiteboard.Util.util.*;

public class User {
    private Boolean isManager = false;
    private SimpleStringProperty userName;

    private Logger logger;

//    bidirectionalList
    private ObservableList<ShapeDrawing> objectList;
    private ObservableList<String> msgList;
    private ObservableList<String> userList;

    // Connection
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    //Constructors
    public User(String username, Socket socket) throws IOException {
        this.userName = new SimpleStringProperty(username) ;
        this.objectList = FXCollections.observableArrayList();
        this.msgList = FXCollections.observableArrayList();
        this.userList = FXCollections.observableArrayList();
        this.userList.add(username);

        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

    };

    //Getters

    public ObservableList<ShapeDrawing> getObjectList() {
        return objectList;
    }

    public ObservableList<String> getMsgList() {
        return msgList;
    }

    public ObservableList<String> getUserList() {
        return userList;
    }

    public Boolean getManager() {
        return isManager;
    }

    public String getUserName() {
        return userName.get();
    }

    public SimpleStringProperty userNameProperty() {
        return userName;
    }


    public void addMsgItem(String item) {
        this.msgList.add(item);
    }

    public void addUserItem(String item) {
        this.userList.add(item);
    }


    public void addObjectItem(ShapeDrawing shapeDrawing) {
//        switch( shapeDrawing.getClass().getName()){
//            case "Circle":
//                Node circle = new Circle();
//                this.objectList.add(circle);
//                break;
//            case "Line":
//                Node line = new Line();
//                this.objectList.add(line);
//                break;
//            case "Path":
//                Node path = new Path();
//                this.objectList.add(path);
//                break;
//            case "Text":
//                Node text = new Text();
//                this.objectList.add(text);
//                break;
//            case "Triangle":
//                Node triangle = new Polygon();
//                this.objectList.add(triangle);
//                break;
//        };

    }

    //Setters

    public void setObjectList(String[] objectList) throws JsonSerializationException, IOException {
        for (String string: objectList){
            this.objectList.add(TransferToShape(string));
        }
    }

    public void setMsgList(String[] msgList) {
        for (String string: msgList){
            this.msgList.add(string);
        }
    }

    public void setUserList(String[] userList) {
        for (String string: userList){
        this.userList.add(string);
        }
    }

    //Methods

    protected Boolean sendDrawMsg(ShapeDrawing shape) {
        try {
            writeMsg(bufferedWriter, new DrawRequest(userName.getName(), shape));
        } catch (IOException e) {
            logger.logError("Failed to send DrawRequest...");
            throw new RuntimeException(e);
        }

        Message msgFromSvr;
        try {
            msgFromSvr = readMsg(bufferedReader);
        } catch (JsonSerializationException | IOException e1) {
            try {
                writeMsg(bufferedWriter,new ErrorMsg("Invalid message"));
            } catch (IOException e) {
                logger.logDebug("Error happened when receiving DrawReply from server");
                throw new RuntimeException(e);
            }
            return false;
        }

        if (msgFromSvr.getClass().getName() == DrawReply.class.getName()) {
            DrawReply drawReply =  (DrawReply) msgFromSvr;
            return drawReply.success;
        }
        return false;
    }

    protected Boolean sendChatMsg(String msg){
        try {
            writeMsg(bufferedWriter, new SendMsgReuqest(userName.getName(), msg));
        } catch (IOException e) {
            logger.logError("Failed to send SendMsgRequest...");
            throw new RuntimeException(e);
        }
        Message msgFromSvr;
        try {
            msgFromSvr = readMsg(bufferedReader);
        } catch (JsonSerializationException | IOException e1) {
            try {
                writeMsg(bufferedWriter,new ErrorMsg("Invalid message"));
            } catch (IOException e) {
                logger.logDebug("Error happened when receiving SendMsgReply from server");
                throw new RuntimeException(e);
            }
            return false;
        }

        if (msgFromSvr.getClass().getName() == SendMsgReply.class.getName()) {
            SendMsgReply sendMsgReply =  (SendMsgReply) msgFromSvr;
            return sendMsgReply.success;
        }
        return false;
    };

    protected Boolean sendQuitMsg(){
        try {
            writeMsg(bufferedWriter, new QuitRequest(userName.getName()));
        } catch (IOException e) {
            logger.logError("Failed to send QuitRequest...");
            throw new RuntimeException(e);
        }
        Message msgFromSvr;
        try {
            msgFromSvr = readMsg(bufferedReader);
        } catch (JsonSerializationException | IOException e1) {
            try {
                writeMsg(bufferedWriter,new ErrorMsg("Invalid message"));
            } catch (IOException e) {
                logger.logDebug("Error happened when receiving SendMsgReply from server");
                throw new RuntimeException(e);
            }
            return false;
        }

        if (msgFromSvr.getClass().getName() == QuitReply.class.getName()) {
            QuitReply quitReply =  (QuitReply) msgFromSvr;
            return quitReply.success;
        }
        return false;
    };
}
