package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@JsonSerializable
public class JoinReply extends Message{

    @JsonElement
    public Boolean success;

    @JsonElement
    public List<String> userList;

    @JsonElement
    public ArrayList<String> objectList;

    public JoinReply(){};

    public JoinReply(Boolean success, List<String> userList, ArrayList<String> objectList){
        this.success = success;
        this.userList = userList;
        this.objectList = objectList;
    };

}
