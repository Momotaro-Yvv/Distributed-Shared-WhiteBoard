package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Reply from server that the user has been kicked out as requested
 */
@JsonSerializable
public class KickReply extends Message{

    @JsonElement
    public Boolean success;

    @JsonElement
    public String userKickOut;

    public KickReply(){};

    public KickReply(Boolean success, String userKickOut) {
        this.success = success;
        this.userKickOut = userKickOut;
    }
}
