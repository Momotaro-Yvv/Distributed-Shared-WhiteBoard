package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Reply from server to tell if the shape successfully drawn on the whiteboard
 */
@JsonSerializable
public class DrawReply extends Message{
    @JsonElement
    public Boolean success;

    public DrawReply(){};

    public DrawReply(Boolean success){
        this.success = success;
    };
}
