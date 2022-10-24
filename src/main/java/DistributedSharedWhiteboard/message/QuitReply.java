package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

/**
 * Reply from server that the user now has removed from userList
 */
@JsonSerializable
public class QuitReply extends Message {

    @JsonElement
    public Boolean success;

    public QuitReply() {
    }

    public QuitReply(Boolean success) {
        this.success = success;
    }
}
