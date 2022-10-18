package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

import java.util.ArrayList;
import java.util.Set;

@JsonSerializable
public class JoinReply extends Message{

    @JsonElement
    public Integer userId;

    @JsonElement
    public Set<String> userList;

    @JsonElement
    public ArrayList<String> objectList;

    public JoinReply(){};

    public JoinReply(Integer userId, Set<String> userList, ArrayList<String> objectList){
        this.userId = userId;
        this.userList = userList;
        this.objectList = objectList;
    };

}
