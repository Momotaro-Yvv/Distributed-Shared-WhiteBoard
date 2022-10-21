package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Reply from manager that if approve user's join or not
 */

@JsonSerializable
public class ApproveReply {

    @JsonElement
    public Boolean approve;

    public ApproveReply(){};

    public ApproveReply(Boolean approve){
        this.approve = approve;
    }
}
