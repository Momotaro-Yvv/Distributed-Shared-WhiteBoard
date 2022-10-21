package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

@JsonSerializable
public class KickRequest extends Message{

    @JsonElement
    public String managerName;
    @JsonElement
    public String username;
    public KickRequest(){};

    public KickRequest(String managerName, String username){
        this.managerName = managerName;
        this.username = username;
    };

}
