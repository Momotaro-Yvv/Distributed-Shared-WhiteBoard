package com.example.distributedsharedwhiteboard.Shape;

@JsonSerializable
public class Line extends Shape {

    @JsonElement
    double startX, startY, endX, endY;

    public Line() {
    }

    public Line(double startX, double startY, double endX, double endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }
}
