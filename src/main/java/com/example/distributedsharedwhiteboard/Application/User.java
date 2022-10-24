package com.example.distributedsharedwhiteboard.Application;

import com.example.distributedsharedwhiteboard.Logger;
import com.example.distributedsharedwhiteboard.ShapeDrawing.ShapeDrawing;
import com.example.distributedsharedwhiteboard.Util.JsonSerializationException;
import com.example.distributedsharedwhiteboard.message.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static com.example.distributedsharedwhiteboard.Util.util.*;

public class User {
    private Boolean isManager = false;
    private SimpleStringProperty userName;

    protected Logger logger;

//    bidirectionalList
    private ObservableList<ShapeDrawing> objectList;
    private ObservableList<String> msgList;
    private ObservableList<String> userList;
    private ObservableList<String> eventList;

    private ObservableList<ShapeDrawing> undrawedList;

    // Connection
    protected Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    protected BufferedReader bufferedReader;
    protected BufferedWriter bufferedWriter;

    private UpdateThread updateThread;

    //Constructors
    public User(String username, Socket socket) throws IOException {
        this.userName = new SimpleStringProperty(username) ;
        this.objectList = FXCollections.observableArrayList();
        this.msgList = FXCollections.observableArrayList();
        this.userList = FXCollections.observableArrayList();
        this.eventList = FXCollections.observableArrayList();
        this.undrawedList = FXCollections.observableArrayList();
        this.userList.add(username);

        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        this.logger = new Logger();
        updateThread = new UpdateThread(this.userName.getValue(), bufferedReader, bufferedWriter, logger,
                eventList, undrawedList, msgList, userList);
        updateThread.start();
    };

    //Getters

    public ObservableList<ShapeDrawing> getObjectList() {
        return objectList;
    }

    public ObservableList<ShapeDrawing> getUndrawedList() {
        return undrawedList;
    }

    public ObservableList<String> getMsgList() {
        return msgList;
    }

    public ObservableList<String> getUserList() {
        return userList;
    }

    public ObservableList<String> getEventList() {
        return eventList;
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

    protected void sendDrawMsg(ShapeDrawing shape) {
        try {
            writeMsg(bufferedWriter, new DrawRequest(userName.getValue(), shape));
        } catch (IOException e) {
            logger.logError("Failed to send DrawRequest...");
            throw new RuntimeException(e);
        }
    }


    protected void sendChatMsg(String msg){
        try {
            writeMsg(bufferedWriter, new SendMsgRequest(userName.getValue(), msg));
        } catch (IOException e) {
            logger.logError("Failed to send SendMsgRequest...");
            throw new RuntimeException(e);
        }
    };

    protected void sendQuitMsg(){
        try {
            writeMsg(bufferedWriter, new QuitRequest(userName.getValue()));
        } catch (IOException e) {
            logger.logError("Failed to send QuitRequest...");
            throw new RuntimeException(e);
        }
    };

    protected void addTodoEvent(String event) {
        eventList.add(event);
    }
}
