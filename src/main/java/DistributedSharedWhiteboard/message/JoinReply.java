package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

import java.util.List;

/**
 * Reply from server if a user successfully registered
 */
@JsonSerializable
public class JoinReply extends Message {

    @JsonElement
    public Boolean success;

    @JsonElement
    public String[] userList;

    @JsonElement
    public String[] objectList;

    public JoinReply() {
    }

    public JoinReply(Boolean success, List<String> userList, String[] objectList) {
        this.success = success;
        this.userList = new String[userList.size()];

        for (int i = 0; i < userList.size(); i++) {
            this.userList[i] = userList.get(i);
        }
        this.objectList = objectList;
    }
}
