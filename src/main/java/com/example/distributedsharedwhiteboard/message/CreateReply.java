package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Reply from server to tell client if a new white board has been successfully created
 */
@JsonSerializable
public class CreateReply extends Message{

    @JsonElement
    public Boolean success;

    public CreateReply(){};

    public CreateReply(Boolean success){
        this.success = success;
    };

}
