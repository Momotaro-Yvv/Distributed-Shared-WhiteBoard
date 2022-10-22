package com.example.distributedsharedwhiteboard.server;

import com.example.distributedsharedwhiteboard.ShapeDrawing.ShapeDrawing;
import com.example.distributedsharedwhiteboard.Util.util;

import java.util.ArrayList;
import java.util.List;

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

    public List<String> getObjects() {
        List<String> shapes = new ArrayList<>();
        for (ShapeDrawing shapeDrawing : objects){
            shapes.add(util.TransferFromShape(shapeDrawing));
        }
        return shapes;
    }
}
