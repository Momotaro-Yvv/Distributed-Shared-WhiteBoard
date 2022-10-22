package com.example.distributedsharedwhiteboard.ShapeDrawing;

import com.example.distributedsharedwhiteboard.Util.JsonSerializationException;
import com.example.distributedsharedwhiteboard.message.MessageFactory;

/**
 *  Fundamental plane figures
 */
public class ShapeDrawing {

    // parameters for color

    public ShapeDrawing() {
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
