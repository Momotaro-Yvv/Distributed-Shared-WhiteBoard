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
    @JsonElement
    public String managerName;

    public CreateReply(){};

    public CreateReply(Boolean success, String managerName){
        this.success = success;
        this.managerName = managerName;
    };

}
