package DistributedSharedWhiteboard.ShapeDrawing;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

import javafx.scene.paint.Color;

@JsonSerializable
public class LineDrawing extends ShapeDrawing {

    @JsonElement
    public Double startX, startY, endX, endY;

    @JsonElement
    public Double red, green, blue, opacity;

    public LineDrawing() {
    }

    public LineDrawing(Double startX, Double startY, Double endX, Double endY, Color color) {
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        this.opacity = color.getOpacity();

        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }
}
