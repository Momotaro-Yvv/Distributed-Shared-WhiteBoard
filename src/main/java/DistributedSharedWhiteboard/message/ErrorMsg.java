package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

//TODO: if ErrorMsg is received, the sender will has to resend the request again
@JsonSerializable
public class ErrorMsg extends Message {

    @JsonElement
    public String msg;

    public ErrorMsg() {

    }

    public ErrorMsg(String msg) {
        this.msg = msg;
    }

}
