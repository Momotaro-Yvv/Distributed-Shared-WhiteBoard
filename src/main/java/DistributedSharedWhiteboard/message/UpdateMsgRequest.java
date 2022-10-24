package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

/**
 * Request from server to update any changes of chat box
 */
@JsonSerializable
public class UpdateMsgRequest extends Message {

    @JsonElement
    public String msg;

    @JsonElement
    public String byWhom;

    public UpdateMsgRequest() {
    }

    public UpdateMsgRequest(String msg, String byWhom) {
        this.byWhom = byWhom;
        this.msg = msg;
    }

}
