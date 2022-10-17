package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

@JsonSerializable
public class QuitMsg extends Message{

    @JsonElement
    public Integer userId;

    public QuitMsg(){};

    public QuitMsg(Integer userId){
        this.userId = userId;
    };

}
