package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

import java.net.Socket;

/**
 * Reply from manager that if approve user's join or not
 */

@JsonSerializable
public class ApproveReply extends Message{

    @JsonElement
    public Boolean approve;

    @JsonElement
    public String username;
    public ApproveReply(){};

    public ApproveReply(Boolean approve, String username){
        this.approve = approve;
        this.username = username;
    }
}
