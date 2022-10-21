package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Reply from client to receive server's update of chat box change
 */
@JsonSerializable
public class UpdateMsgReply extends Message{

    @JsonElement
    public Boolean success;

    public UpdateMsgReply(){}

    public UpdateMsgReply(Boolean success){
        this.success = success;
    }
}
