package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

@JsonSerializable
public class JoinReply extends Message{

    @JsonElement
    public Integer userId;

    public JoinReply(){};

    public JoinReply(Integer userId){
        this.userId = userId;
    };

}
