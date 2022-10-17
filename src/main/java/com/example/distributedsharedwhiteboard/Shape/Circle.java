package com.example.distributedsharedwhiteboard.Shape;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

@JsonSerializable
public class Circle extends Shape{

    @JsonElement
    double centerX, centerY, radius;

    public Circle() {
    }

    public Circle(double centerX, double centerY, double radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }
}
