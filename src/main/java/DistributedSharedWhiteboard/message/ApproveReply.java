package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

/**
 * Reply from manager that if approve user's join or not
 */

@JsonSerializable
public class ApproveReply extends Message {

    @JsonElement
    public Boolean approve;

    @JsonElement
    public String username;

    public ApproveReply() {
    }

    public ApproveReply(Boolean approve, String username) {
        this.approve = approve;
        this.username = username;
    }
}
