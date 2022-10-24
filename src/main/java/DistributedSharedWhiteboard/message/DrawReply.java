package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

/**
 * Reply from server to tell if the shape successfully drawn on the whiteboard
 */
@JsonSerializable
public class DrawReply extends Message {
    @JsonElement
    public Boolean success;

    public DrawReply() {
    }

    public DrawReply(Boolean success) {
        this.success = success;
    }
}
