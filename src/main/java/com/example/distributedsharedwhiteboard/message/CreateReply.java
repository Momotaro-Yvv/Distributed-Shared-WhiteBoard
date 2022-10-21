package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

@JsonSerializable
public class CreateReply extends Message{

    @JsonElement
    public Boolean success;

    public CreateReply(){};

    public CreateReply(Boolean success){
        this.success = success;
    };

}
