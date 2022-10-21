package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Reply from server that the user now has removed from userList
 */
@JsonSerializable
public class QuitReply extends Message {

    @JsonElement
    public Boolean success;

    public QuitReply(){}

    public QuitReply(Boolean success){
        this.success = success;
    }
}
