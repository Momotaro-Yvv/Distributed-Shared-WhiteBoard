package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

@JsonSerializable
public class CreateReply extends Message{

    @JsonElement
    public Integer userId;

    public CreateReply(){};

    public CreateReply(Integer userId){
        this.userId = userId;
    };

}
