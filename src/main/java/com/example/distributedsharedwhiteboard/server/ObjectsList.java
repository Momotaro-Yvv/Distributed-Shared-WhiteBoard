package com.example.distributedsharedwhiteboard.server;

import com.example.distributedsharedwhiteboard.ShapeDrawing.ShapeDrawing;
import com.example.distributedsharedwhiteboard.Util.JsonSerializationException;
import com.example.distributedsharedwhiteboard.Util.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.distributedsharedwhiteboard.Util.util.TransferToShapeList;

public class ObjectsList {
    private ArrayList<ShapeDrawing> objects;

    public ObjectsList () {
        objects = new ArrayList<>();
    }
    public void addAnObject(ShapeDrawing shapeDrawing){
        objects.add(shapeDrawing);
    };
    public void deleteAnObject(ShapeDrawing shapeDrawing){
        objects.remove(shapeDrawing);
    };

    public void clearObjectList(){
        objects.clear();
    }

    public List<String> getObjects(){
        return TransferToShapeList(objects);
    }
}
