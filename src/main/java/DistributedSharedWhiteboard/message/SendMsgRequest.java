package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

/**
 * Request from user to send a chat message to chat box
 */
@JsonSerializable
public class SendMsgRequest extends Message {

    @JsonElement
    public String username;

    @JsonElement
    public String msg;

    public SendMsgRequest() {
    }

    public SendMsgRequest(String username, String msg) {
        this.username = username;
        this.msg = msg;
    }
}
