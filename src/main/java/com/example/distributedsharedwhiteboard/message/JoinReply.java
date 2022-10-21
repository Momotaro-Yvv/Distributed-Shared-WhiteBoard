package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Reply from server if a user successfully registered
 */
@JsonSerializable
public class JoinReply extends Message{

    @JsonElement
    public Boolean success;

    @JsonElement
    public List<String> userList;

    @JsonElement
    public List<String> objectList;

    public JoinReply(){};

    public JoinReply(Boolean success, List<String> userList, List<String> objectList){
        this.success = success;
        this.userList = userList;
        this.objectList = objectList;
    };

}
