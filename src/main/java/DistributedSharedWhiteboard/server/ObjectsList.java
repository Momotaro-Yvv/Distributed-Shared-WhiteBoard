package DistributedSharedWhiteboard.server;

import DistributedSharedWhiteboard.Util.util;
import DistributedSharedWhiteboard.ShapeDrawing.ShapeDrawing;

import java.util.ArrayList;

public class ObjectsList {
    private final ArrayList<ShapeDrawing> objects;

    public ObjectsList() {
        objects = new ArrayList<>();
    }

    public void addAnObject(ShapeDrawing shapeDrawing) {
        objects.add(shapeDrawing);
    }

    public void clearObjectList() {
        objects.clear();
    }

    public String[] getObjects() {
        return util.TransferToShapeList(objects);
    }
}
