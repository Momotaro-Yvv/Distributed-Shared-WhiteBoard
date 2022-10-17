package com.example.distributedsharedwhiteboard;

import com.example.distributedsharedwhiteboard.Shape.Shape;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ObjectsList {
    private static ArrayList<JSONObject> objects;

    public ObjectsList () {
        objects = new ArrayList<>();
    }
    public void addAnObject(Shape shape){
        objects.add(shape.toString());
    };
    public void deleteAnObject(Shape shape){
        objects.remove(shape);
    };


}
