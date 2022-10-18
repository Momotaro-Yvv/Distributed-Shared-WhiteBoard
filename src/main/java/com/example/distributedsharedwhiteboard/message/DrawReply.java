package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

@JsonSerializable
public class DrawReply {
    @JsonElement
    public Boolean success;

    public DrawReply(){};

    public DrawReply(Boolean success){
        this.success = success;
    };
}
