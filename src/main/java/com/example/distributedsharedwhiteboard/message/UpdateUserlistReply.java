package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Reply from user to confirm the Userlist update has been received
 */
@JsonSerializable
public class UpdateUserlistReply extends Message {

    @JsonElement
    public Boolean success;

    public UpdateUserlistReply(){}

    public UpdateUserlistReply(Boolean success){
        this.success = success;
    }
}