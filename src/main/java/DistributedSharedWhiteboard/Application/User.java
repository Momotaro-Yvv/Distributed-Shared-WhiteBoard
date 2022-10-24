package DistributedSharedWhiteboard.Application;

import DistributedSharedWhiteboard.Logger;
import DistributedSharedWhiteboard.ShapeDrawing.*;
import DistributedSharedWhiteboard.Util.JsonSerializationException;
import DistributedSharedWhiteboard.message.DrawRequest;
import DistributedSharedWhiteboard.message.QuitRequest;
import DistributedSharedWhiteboard.message.SendMsgRequest;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static DistributedSharedWhiteboard.Util.util.*;

/**
 * This class stores information about user and send messages to the server
 * for the specified user interaction.
 */
public class User {

    private final SimpleStringProperty userName;

    protected Logger logger;

    // bidirectionalList
    private final ObservableList<ShapeDrawing> objectList;
    private final ObservableList<String> msgList;
    private final ObservableList<String> userList;
    private final ObservableList<ControllerCmd> eventList;

    protected ObservableList<ShapeDrawing> undrawedList;

    // Connection
    protected Socket socket;
    protected BufferedReader bufferedReader;
    protected BufferedWriter bufferedWriter;

    private final UpdateThread updateThread;

    //Constructors
    public User(String username, Socket socket) throws IOException {
        this.userName = new SimpleStringProperty(username);
        this.objectList = FXCollections.observableArrayList();
        this.msgList = FXCollections.observableArrayList();
        this.userList = FXCollections.observableArrayList();
        this.eventList = FXCollections.observableArrayList();
        this.undrawedList = FXCollections.observableArrayList();

        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        this.logger = new Logger();
        updateThread = new UpdateThread(this.userName.getValue(), bufferedReader, bufferedWriter, logger,
                eventList, undrawedList, msgList, userList);
        updateThread.start();
    }

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

    public ObservableList<ControllerCmd> getEventList() {
        return eventList;
    }

    public String getUserName() {
        return userName.get();
    }

    public SimpleStringProperty userNameProperty() {
        return userName;
    }

    public void addUserItem(String item) {
        this.userList.add(item);
    }

    //Setters

    public void setObjectList(String[] objectList) throws JsonSerializationException, IOException {
        for (String string : objectList) {
            this.objectList.add(TransferToShape(string));
        }
    }

    public void setUserList(String[] userList) {
        Collections.addAll(this.userList, userList);
    }

    //Methods

    /**
     * This method is called when user drawn a new shape onto whiteboard.
     *
     * @param shape - drawn shape
     */
    protected void sendDrawMsg(ShapeDrawing shape) {
        try {
            writeMsg(bufferedWriter, new DrawRequest(userName.getValue(), shape));
        } catch (IOException e) {
            logger.logError("Failed to send DrawRequest...");
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is called when user sent a message.
     *
     * @param msg - the message that user sent
     */
    protected void sendChatMsg(String msg) {
        try {
            writeMsg(bufferedWriter, new SendMsgRequest(userName.getValue(), msg));
        } catch (IOException e) {
            logger.logError("Failed to send SendMsgRequest...");
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is called when user want to quit whiteboard.
     */
    protected void sendQuitMsg() {
        try {
            writeMsg(bufferedWriter, new QuitRequest(userName.getValue()));
        } catch (IOException e) {
            logger.logError("Failed to send QuitRequest...");
            throw new RuntimeException(e);
        }
    }
}
