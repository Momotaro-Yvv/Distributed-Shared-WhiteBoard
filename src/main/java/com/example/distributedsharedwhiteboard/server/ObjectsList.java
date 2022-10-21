package com.example.distributedsharedwhiteboard.server;

import com.example.distributedsharedwhiteboard.Shape.Shape;

import java.util.ArrayList;

public class ObjectsList {
    private ArrayList<String> objects;

    public ObjectsList () {
        objects = new ArrayList<>();
    }
    public void addAnObject(Shape shape){
        objects.add(shape.toString());
    };
    public void deleteAnObject(Shape shape){
        objects.remove(shape);
    };

    public ArrayList<String> getObjects() {
        return objects;
    }
}
