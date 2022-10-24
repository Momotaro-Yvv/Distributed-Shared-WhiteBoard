package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

/**
 * Reply from server if the reload request is successful
 */
@JsonSerializable
public class ReloadReply extends Message {

    @JsonElement
    public Boolean success;

    public ReloadReply() {
    }

    public ReloadReply(Boolean success) {
        this.success = success;
    }

}
