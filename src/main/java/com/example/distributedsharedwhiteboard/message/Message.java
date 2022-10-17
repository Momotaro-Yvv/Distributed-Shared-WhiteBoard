package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonSerializationException;

public class Message {
    @Override
    public String toString()  {
        try {
            return MessageFactory.serialize(this);
        } catch (JsonSerializationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
