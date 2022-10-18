package com.example.distributedsharedwhiteboard.Application;

import com.example.distributedsharedwhiteboard.Shape.Shape;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.net.InetAddress;

public class User {
    Boolean isManager = false;
    private SimpleStringProperty userName;
    private SimpleIntegerProperty userId;
    private SimpleStringProperty srvAddress;
    private SimpleIntegerProperty srvPort;

//    bidirectionalList

    private ObservableList<Node> objectList;


    private ObservableList<String> msgList;

    // user list
    private ObservableList<String> userList;


    //Constructors
    public User(InetAddress srvAddress, int srvPort, String username, int id){
//        this.srvAddress = new SimpleStringProperty(srvAddress);
        this.srvPort = new SimpleIntegerProperty(srvPort);
        this.userName = new SimpleStringProperty(username) ;
        this.userId = new SimpleIntegerProperty(id);
        this.objectList = FXCollections.observableArrayList();
        this.msgList = FXCollections.observableArrayList();
        this.userList = FXCollections.observableArrayList();
        this.userList.add(username);
    };

    //Getters

    public ObservableList<Node> getObjectList() {
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
    //Setters

    public void addObjectItem(Shape shape) {
        switch( shape.getClass().getName()){
            case "Circle":
                Node circle = new Circle();
                this.objectList.add(circle);
                break;
            case "Line":
                Node line = new Line();
                this.objectList.add(line);
                break;
            case "Path":
                Node path = new Path();
                this.objectList.add(path);
                break;
            case "Text":
                Node text = new Text();
                this.objectList.add(text);
                break;
            case "Triangle":
                Node triangle = new Polygon();
                this.objectList.add(triangle);
                break;
        };

    }

    public void addMsgItem(String item) {
        this.msgList.add(item);
    }

    public void addUserItem(String item) {
        this.userList.add(item);
    }

    //Methods

    protected void sendDrawMsg(){};
    protected void sendChatMsg(){};

    protected void sendQuitMsg(){};
}