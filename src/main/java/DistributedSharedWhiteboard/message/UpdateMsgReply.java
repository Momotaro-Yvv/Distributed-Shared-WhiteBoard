package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

/**
 * Reply from client to receive server's update of chat box change
 */
@JsonSerializable
public class UpdateMsgReply extends Message {

    @JsonElement
    public Boolean success;

    public UpdateMsgReply() {
    }

    public UpdateMsgReply(Boolean success) {
        this.success = success;
    }
}
