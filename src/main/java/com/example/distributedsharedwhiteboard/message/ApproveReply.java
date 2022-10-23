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

    @JsonElement
    public String clientIp;

    @JsonElement
    public Integer clientPort;

    public ApproveReply(){};

    public ApproveReply(Boolean approve, String username, String clientIp, Integer clientPort){
        this.approve = approve;
        this.username = username;
        this.clientIp  = clientIp;
        this.clientPort = clientPort;
    }
}
