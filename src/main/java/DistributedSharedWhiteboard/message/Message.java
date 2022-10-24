package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonSerializationException;

public class Message {
    @Override
    public String toString() {
        try {
            return MessageFactory.serialize(this);
        } catch (JsonSerializationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
