package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Reply from server that the chat message has been sent successfully
 */
@JsonSerializable
public class SendMsgReply extends Message{
    @JsonElement
    public Boolean success;

    public SendMsgReply(){}

    public SendMsgReply(Boolean success){
        this.success = success;
    }
}
