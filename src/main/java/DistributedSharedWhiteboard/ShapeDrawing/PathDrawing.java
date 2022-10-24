package DistributedSharedWhiteboard.ShapeDrawing;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

import javafx.scene.paint.Color;

@JsonSerializable
public class PathDrawing extends ShapeDrawing {

    @JsonElement
    public Double[] xs, ys;

    @JsonElement
    public Double red, green, blue, opacity;

    public PathDrawing() {
    }

    public PathDrawing(Double[] xs, Double[] ys, Color color) {
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        this.opacity = color.getOpacity();

        this.xs = xs;
        this.ys = ys;
    }
}
