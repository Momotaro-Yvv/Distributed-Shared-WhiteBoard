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
    public String[] userList;

    @JsonElement
    public String[] objectList;

    public JoinReply(){};

    public JoinReply(Boolean success, List<String> userList, List<String> objectList){
        this.success = success;
        this.userList = new String[userList.size()];
        this.objectList = new String[objectList.size()];

        for(int i=0;i<userList.size();i++) {
            this.userList[i]=userList.get(i);
        }
        for(int i=0;i<objectList.size();i++) {
            this.objectList[i]=objectList.get(i);
        }
    };

}
