package com.example.distributedsharedwhiteboard.Shape;

import com.example.distributedsharedwhiteboard.Util.JsonSerializationException;
import com.example.distributedsharedwhiteboard.message.MessageFactory;

/**
 *  Fundamental plane figures
 */
public class Shape {

    public Shape() {
    }

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
