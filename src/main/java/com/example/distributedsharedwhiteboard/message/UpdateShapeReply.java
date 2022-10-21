package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Reply from user to confirm the update has been received
 */
@JsonSerializable
public class UpdateShapeReply extends Message{

    @JsonElement
    public Boolean success;

    public UpdateShapeReply(){}

    public UpdateShapeReply(Boolean success){
        this.success = success;
    }
}
