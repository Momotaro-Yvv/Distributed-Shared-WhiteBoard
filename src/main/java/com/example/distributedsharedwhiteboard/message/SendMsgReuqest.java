package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Request from user to send a chat message to chat box
 */
@JsonSerializable
public class SendMsgReuqest extends Message{

    @JsonElement
    public String username;

    @JsonElement
    public String msg;

    public SendMsgReuqest(){}

    public SendMsgReuqest(String username, String msg){
        this.username = username;
        this.msg = msg;
    }
}
