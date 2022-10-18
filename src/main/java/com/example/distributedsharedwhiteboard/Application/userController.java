package com.example.distributedsharedwhiteboard.Application;

import com.example.distributedsharedwhiteboard.Application.User;
import com.example.distributedsharedwhiteboard.client.JoinWhiteBoard;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.scene.shape.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;

/**
 * A controller that handle both manager and user operation for the WhiteBoard Application
 */
public class userController {

    @FXML
    protected Stage stage;

    @FXML
    protected TextField msg;

    @FXML
    protected ListView<String> MsgHistory;
    // TODO: change css to avoid similarities between msgList & userList

    @FXML
    protected ListView<String> userList;

    @FXML
    protected ColorPicker colorPicker;

    @FXML
    protected ToggleGroup drawMode;

    @FXML
    protected Pane pane;

    @FXML
    protected Canvas canvas;

    // shapes for drawing
    @FXML
    protected Line line;

    @FXML
    protected TextField textfield;

    @FXML
    protected Circle circle;

    @FXML
    protected Rectangle rectangle;

    @FXML
    protected Path path;

    @FXML
    protected Polygon polygon;

    protected String modeID;

    protected GraphicsContext gc;

    private double startX, startY, endX, endY;

    //Model
    private User user;

    // This allows the implementing class to perform any necessary post-processing on the content.
    // It also provides the controller with access to the resources that were used to load the
    // document and the location that was used to resolve relative paths within the document
    // (commonly equivalent to the location of the document itself).
    public void initialize() {
        user = JoinWhiteBoard.getUser();
        // bind variables
        Bindings.bindContentBidirectional(MsgHistory.getItems(), user.getMsgList());
        user.addMsgItem("test only : message"); // now can access msgHistory via msgList

        Bindings.bindContentBidirectional(userList.getItems(), user.getUserList());
        user.addUserItem("Test only : user1");

        Bindings.bindContentBidirectional( user.getObjectList(), pane.getChildren());
        com.example.distributedsharedwhiteboard.Shape.Circle circle1 = new com.example.distributedsharedwhiteboard.Shape.Circle(1, 1, 1);
        user.addObjectItem(circle1);

        // select freehand by default
        drawMode.getToggles().get(0).setSelected(true);

        // set default color as black
        colorPicker.setValue(Color.BLACK);

        // prepare shapes for canvas
        line = new Line();
        textfield = new TextField();
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

            System.out.println(input);

            // add msg to message history
            MsgHistory.getItems().add("user54686: " + input);

            // clear input
            msg.clear();
        }
    }

    @FXML
    protected void handleQuit(ActionEvent event) {
        user.sendQuitMsg();
        Platform.exit();
    }

    @FXML
    protected void handleDraw(MouseEvent event) {

        // check mouse event
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            System.out.println("Mouse pressed");

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
            System.out.println("Mouse dragged");
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
            System.out.println("Mouse released");
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

    // TODO: draw shape from server update
    protected void drawNewShape(Shape shape) {

    }

    // Invoke this message to display a dialog with error message
    protected void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("An error occurred.");
        alert.setContentText(message);

        alert.showAndWait();
    }

    // Invoke this message to display a dialog with info message
    protected void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info Dialog");
        alert.setContentText(message);

        alert.showAndWait();
    }

    public void setUser(User user) {
        this.user = user;
    }
}