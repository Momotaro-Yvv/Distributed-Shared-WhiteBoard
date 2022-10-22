package com.example.distributedsharedwhiteboard.server;

import java.util.ArrayList;
import java.util.jar.Attributes;

public class MsgList {
    private ArrayList<String> messages;
    private ArrayList<String> names;

    public MsgList(){
        messages = new ArrayList<>();
        names = new ArrayList<>();
    }

    public void clearMsgList(){
        messages.clear();
        names.clear();
    }

    public void addNewMessage(String msg, String fromWhom){
        messages.add(msg);
        names.add(fromWhom);
    }

    //Getters

    public ArrayList<String> getMessages() {
        return messages;
    }

    public ArrayList<String> getNames() {
        return names;
    }
}
