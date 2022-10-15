package com.example.distributedsharedwhiteboard.Shape;

@JsonSerializable
public class Triangle extends Shape {

    @JsonElement
    double firstX, secondX, thirdX;

    @JsonElement
    double firstY, secondY, thirdY;

    public Triangle() {
    }

    public Triangle(double firstX, double secondX, double thirdX, double firstY, double secondY, double thirdY) {
        this.firstX = firstX;
        this.secondX = secondX;
        this.thirdX = thirdX;
        this.firstY = firstY;
        this.secondY = secondY;
        this.thirdY = thirdY;
    }
}
