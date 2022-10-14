package com.example.distributedsharedwhiteboard.server;

import com.example.distributedsharedwhiteboard.Shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class ObjectsList {
    private static List<Shape> objects;

    public ObjectsList () {}

    public ObjectsList ( Shape shape) {
        objects = new ArrayList<Shape>();
    }
    public void addAnObject(Shape shape){
        objects.add(shape);
    };
    public void deleteAnObject(Shape shape){
        objects.remove(shape);
    };


}
