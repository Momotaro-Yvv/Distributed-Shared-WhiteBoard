package com.example.distributedsharedwhiteboard.server;

import com.example.distributedsharedwhiteboard.Shape.Shape;
import com.example.distributedsharedwhiteboard.Util.util;

import java.util.ArrayList;
import java.util.List;

public class ObjectsList {
    private ArrayList<Shape> objects;

    public ObjectsList () {
        objects = new ArrayList<>();
    }
    public void addAnObject(Shape shape){
        objects.add(shape);
    };
    public void deleteAnObject(Shape shape){
        objects.remove(shape);
    };

    public List<String> getObjects() {
        List<String> shapes = new ArrayList<>();
        for (Shape shape: objects){
            shapes.add(util.TransferFromShape(shape));
        }
        return shapes;
    }
}
