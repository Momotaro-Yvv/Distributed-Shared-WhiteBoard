package com.example.distributedsharedwhiteboard.Application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

/**
 * A controller that handle both manager and user operation for the WhiteBoard Application
 */
public class userController {

    @FXML
    private TextField msg;

    @FXML
    private ListView MsgHistory;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private ToggleGroup drawMode;

    @FXML
    private Pane pane;

    @FXML
    private Canvas canvas;

    // shapes for drawing

    @FXML
    private Line line;

    @FXML
    private Text text;

    @FXML
    private Circle circle;

    @FXML
    private Rectangle rectangle;

    @FXML
    private Path path;

    @FXML
    private Polygon polygon;

    private String modeID;

    private GraphicsContext gc;

    private double startX, startY, endX, endY;


//        /*
//         * On enter the application , Log some information about the configuration settings.
//         */
//        System.out.println(welcome);
//        System.out.println("using internet address ["+address.getHostName()+":"+port+"]");
//        System.out.println("using basedir ["+dir+"] - all filenames will be relative to this basedir");
//        System.out.println("using sharer secret ["+secret+"]");
//        System.out.println("socket timeout ["+timeout+"]");

    // This allows the implementing class to perform any necessary post-processing on the content.
    // It also provides the controller with access to the resources that were used to load the
    // document and the location that was used to resolve relative paths within the document
    // (commonly equivalent to the location of the document itself).
    public void initialize() {

        // select freehand by default
        drawMode.getToggles().get(0).setSelected(true);

        // set default color as black
        colorPicker.setValue(Color.BLACK);

        // prepare shapes for canvas
        line = new Line();
        text = new Text();
        circle = new Circle();
        rectangle = new Rectangle();
        path = new Path();
        polygon = new Polygon();
    }

    @FXML
    protected void handleSendMsg(ActionEvent event) {

        // get msg
        String input = msg.getText();

        // check if text is empty
        if (input != null && input.length() != 0) {

            // add msg to message history
            MsgHistory.getItems().addAll("user54686: " + input);

            // clear input
            msg.clear();
        }
    }

    @FXML
    protected void handleQuit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    protected void handleDraw(MouseEvent event) {

        // check mouse event
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {

            // set drawing mode
            modeID = ((ToggleButton)(drawMode.getSelectedToggle())).getId();

            // set start x and y
            startX = event.getX();
            startY = event.getY();

            switch (modeID) {
                case "dm_free":
                    pane.getChildren().add(path);

                    path.setStroke(colorPicker.getValue());

                    path.getElements().add(new MoveTo(startX, startY));
                    break;
                case "dm_text":
                    break;
                case "dm_line":
                    // add shape to pane
                    pane.getChildren().add(line);

                    // set color
                    line.setStroke(colorPicker.getValue());

                    // set coordinates for shape
                    line.setStartX(startX);
                    line.setStartY(startY);
                    line.setEndX(startX);
                    line.setEndY(startY);
                    break;
                case "dm_circle":
                    pane.getChildren().add(circle);

                    circle.setStroke(colorPicker.getValue());
                    circle.setFill(null);

                    circle.setCenterX(startX);
                    circle.setCenterY(startY);
                    circle.setRadius(0.f);
                    break;
                case "dm_triangle":
                    pane.getChildren().add(polygon);

                    polygon.setStroke(colorPicker.getValue());
                    polygon.setFill(null);
                    break;
                case "dm_rectangle":
                    // add shape to pane
                    pane.getChildren().add(rectangle);

                    // set color
                    rectangle.setStroke(colorPicker.getValue());
                    rectangle.setFill(null);

                    // set coordinates for shape
                    rectangle.setX(startX);
                    rectangle.setY(startY);
                    rectangle.setWidth(0f);
                    rectangle.setHeight(0f);
                    break;
            }

        }else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {

            endX = event.getX();
            endY = event.getY();

            switch (modeID) {
                case "dm_free":
                    path.getElements().add(new LineTo(endX, endY));
                    break;
                case "dm_line":
                    line.setEndX(endX);
                    line.setEndY(endY);
                    break;
                case "dm_circle":

                    double sideLen = Math.min(Math.abs(endX - startX), Math.abs(endY - startY));

                    circle.setCenterX(Math.min(startX, endX) + (sideLen / 2));
                    circle.setCenterY(Math.min(startY, endY) + (sideLen / 2));
                    circle.setRadius(sideLen / 2);
                    break;
                case "dm_triangle":
                    double w = Math.abs(endX - startX);
                    polygon.getPoints().clear();
                    polygon.getPoints().addAll(new Double[]{
                            Math.min(startX, endX), Math.max(startY, endY),
                            Math.min(startX, endX) + 0.5 * w, Math.min(startY, endY),
                            Math.max(startX, endX), Math.max(startY, endY) });
                    break;
                case "dm_rectangle":

                    rectangle.setX(Math.min(startX, endX));
                    rectangle.setY(Math.min(startY, endY));

                    rectangle.setWidth(Math.abs(endX - startX));
                    rectangle.setHeight(Math.abs(endY - startY));
                    break;
            }

        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {

            endX = event.getX();
            endY = event.getY();

            switch (modeID) {
                case "dm_free":
                    pane.getChildren().remove(path);

                    // add shape to canvas via gc
                    gc = canvas.getGraphicsContext2D();
                    gc.setStroke(colorPicker.getValue());

                    gc.beginPath();
                    for (int i = 1; i < path.getElements().size(); i++) {
                        LineTo lt = (LineTo) path.getElements().get(i);
                        gc.lineTo(lt.getX(), lt.getY());
                    }
                    gc.stroke();

                    path.getElements().clear();
                    break;
                case "dm_line":
                    pane.getChildren().remove(line);

                    // add shape to canvas via gc
                    gc = canvas.getGraphicsContext2D();
                    gc.setStroke(colorPicker.getValue());
                    gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                    break;
                case "dm_circle":
                    double sideLen = Math.min(Math.abs(endX - startX), Math.abs(endY - startY));
                    pane.getChildren().remove(circle);

                    // add shape to canvas via gc
                    gc = canvas.getGraphicsContext2D();
                    gc.setStroke(colorPicker.getValue());
                    gc.strokeOval(Math.min(startX, endX), Math.min(startY, endY), sideLen, sideLen);
                    break;
                case "dm_triangle":
                    double w = Math.abs(endX - startX);
                    pane.getChildren().remove(polygon);

                    // add shape to canvas via gc
                    gc = canvas.getGraphicsContext2D();
                    gc.setStroke(colorPicker.getValue());

                    gc.strokePolygon(new double[]{
                                    Math.min(startX, endX),
                                    Math.min(startX, endX) + 0.5 * w,
                                    Math.max(startX, endX)},
                            new double[]{
                                    Math.max(startY, endY),
                                    Math.min(startY, endY),
                                    Math.max(startY, endY)}, 3);
                    break;
                case "dm_rectangle":
                    pane.getChildren().remove(rectangle);

                    // add shape to canvas via gc
                    gc = canvas.getGraphicsContext2D();
                    gc.setStroke(colorPicker.getValue());
                    gc.strokeRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
                    break;
            }
        }
    }

}