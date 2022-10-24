package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

/**
 * Reply from server that the chat message has been sent successfully
 */
@JsonSerializable
public class SendMsgReply extends Message {
    @JsonElement
    public Boolean success;

    public SendMsgReply() {
    }

    public SendMsgReply(Boolean success) {
        this.success = success;
    }
}
