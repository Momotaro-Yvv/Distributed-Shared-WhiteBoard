package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Request from server to update any changes of chat box
 */
@JsonSerializable
public class UpdateMsgRequest extends Message{

    @JsonElement
    public String msg;

    @JsonElement
    public String sender;

    public UpdateMsgRequest(){}

    public UpdateMsgRequest(String sender, String msg){
        this.sender = sender;
        this.msg = msg;
    }

}
