package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

/**
 * Reply from user to confirm the user removal has been received
 */
@JsonSerializable
public class UpdateDeleteUserReply extends Message {

    @JsonElement
    public Boolean success;

    public UpdateDeleteUserReply() {
    }

    public UpdateDeleteUserReply(Boolean success) {
        this.success = success;
    }
}
