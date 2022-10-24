package DistributedSharedWhiteboard.ShapeDrawing;

import DistributedSharedWhiteboard.Util.JsonSerializationException;
import DistributedSharedWhiteboard.message.MessageFactory;

/**
 * Fundamental plane figures
 */
public class ShapeDrawing {

    public ShapeDrawing() {
    }

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
