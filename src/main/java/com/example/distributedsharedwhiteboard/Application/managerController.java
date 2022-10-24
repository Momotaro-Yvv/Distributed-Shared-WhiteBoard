package com.example.distributedsharedwhiteboard.Application;

import com.example.distributedsharedwhiteboard.ShapeDrawing.*;
import com.example.distributedsharedwhiteboard.client.CreateWhiteBoard;
import com.example.distributedsharedwhiteboard.Util.*;

import com.example.distributedsharedwhiteboard.client.JoinWhiteBoard;
import com.example.distributedsharedwhiteboard.message.Message;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.distributedsharedwhiteboard.Util.util.readMsg;
import static com.example.distributedsharedwhiteboard.Util.util.writeMsg;

public class managerController extends userController {

    private Manager manager;

    private Stage stage;

    @Override
    protected void setUp() {
        super.setUp();

        // set user cell
        userList.setCellFactory(lv -> {

            ListCell<String> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem kickOutUser = new MenuItem("kick-out");
            kickOutUser.setOnAction(event -> {
                String item = cell.getItem();
                // code to kick user
                if (!item.equals(manager.getUserName())){
                    System.out.println("kick out " + item);
                    manager.sendKickUserMsg(item);
                }
            });
            contextMenu.getItems().add(kickOutUser);

            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell ;
        });
    }

    @Override
    public void initialize(){
        manager = CreateWhiteBoard.getManager();
        System.out.println("Manager set up!");

        setUp();

        // bind variables
        Bindings.bindContentBidirectional(msgHistory.getItems(),manager.getMsgList());
//        manager.addMsgItem("test only : message"); // now can access msgHistory via msgList

        Bindings.bindContentBidirectional(userList.getItems(),manager.getUserList());
//        manager.addUserItem("Test only : user1");

        Bindings.bindContentBidirectional(manager.getObjectList(),drawedShapes);
        Bindings.bindContentBidirectional(manager.getEventList(),todoEvents);

        manager.getUndrawedList().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {

                while (c.next()) {

                    // if anything was added to list
                    if (c.wasAdded()) {
                        for (Object s : c.getAddedSubList()) {
                            // ask user to send a updateRequest to server
                            drawNewShape((ShapeDrawing) s);
                        }
                    }
                }
            }

        });

        todoEvents.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {

                while (c.next()) {

                    // if anything was added to list
                    if (c.wasAdded()) {
                        for (Object s : c.getAddedSubList()) {
                            String event = s.toString();
                            switch (event) {
                                case "showJoinRequest":
                                    showJoinRequest("testuser");
                                    break;
                            }
                        }
                    }
                }
            }

        });
    }

    @FXML
    protected void handleNew(ActionEvent event) {
        // check is there any unsaved changes
        if (isUnsaved) {
            showUnsaveDialog();
        }

        // clear all changes
        drawedShapes.clear();

        // clear the canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // TODO: send reload request to server
        List<ShapeDrawing> shapeDrawings = new ArrayList<>();
        manager.sendReloadRequest(shapeDrawings);
    }

    @FXML
    protected void handleOpen(ActionEvent event) {
        // prepare file chooser
        FileChooser openfile = new FileChooser();
        openfile.getExtensionFilters().addAll(
                new ExtensionFilter("txt Files", "*.txt"));
        openfile.setSelectedExtensionFilter(openfile.getExtensionFilters().get(0));
        openfile.setTitle("Open File");

        // display the file chooser dialog
        File file = openfile.showOpenDialog(stage);

        // import whiteboard from a txt file
        if (file != null) {
            try {
                // open file reader
                BufferedReader reader = new BufferedReader(new FileReader(file));

                // add shapes to undrawed list
                String line;
                while ((line = reader.readLine()) != null) {
                    ShapeDrawing shape = util.TransferToShape(line);

                    // TODO: add loaded shapes to undrawed list
                }

                // close reader
                reader.close();

            } catch (JsonSerializationException j) {
                j.printStackTrace();
            } catch (IOException ex) {
                System.out.println("Error!");
            }
        }

        // TODO: send reload request to server
    }

    @FXML
    protected void handleSave(ActionEvent event) {

        // prepare file chooser
        FileChooser savefile = new FileChooser();
        savefile.getExtensionFilters().addAll(
                new ExtensionFilter("text Files", "*.txt"),
                new ExtensionFilter("All Files", "*.*"));
        savefile.setSelectedExtensionFilter(savefile.getExtensionFilters().get(0));
        savefile.setTitle("Save File");
        savefile.setInitialFileName("untitled-" + LocalDateTime.now().getNano());

        // display the file chooser dialog
        File file = savefile.showSaveDialog(stage);

        // export whiteboard as a txt file
        if (file != null) {
            try {

                // open file writer
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));

                // add all drawed shapes to file
                for (ShapeDrawing s : drawedShapes) {
                    writer.write(util.TransferFromShape(s));
                    writer.newLine();
                    writer.flush();
                }

                // close file writer
                writer.close();

            } catch (IOException ex) {
                System.out.println("Error!");
            }
        }

        // update save state
        isUnsaved = false;
    }

    @FXML
    protected void handleSaveAs(ActionEvent event) {

        // prepare file chooser
        FileChooser savefile = new FileChooser();
        savefile.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png", "*.jpg"),
                new ExtensionFilter("All Files", "*.*"));
        savefile.setSelectedExtensionFilter(savefile.getExtensionFilters().get(0));
        savefile.setTitle("Save File");
        savefile.setInitialFileName("untitled-" + LocalDateTime.now().getNano());

        // display the file chooser dialog
        File file = savefile.showSaveDialog(stage);

        // export whiteboard as a PNG file
        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
                canvas.snapshot(null, writableImage);

                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);

            } catch (IOException ex) {
                System.out.println("Error!");
            }
        }

        // update save state
        isUnsaved = false;
    }

    @Override
    protected void handleQuit(ActionEvent event) {

        // check is there any unsaved changes
        if (isUnsaved) {
            boolean option = showUnsaveDialog();
            if (option) {
                manager.sendTerminateMsg();
                Platform.exit();
            }
        } else {
//            manager.sendTerminateMsg();
            Platform.exit();
        }
    }

    @Override
    protected void handleSendMsg(ActionEvent event) {

        // get msg
        String input = msg.getText();


        // check if text is empty
        if (input != null && input.length() != 0) {

            // add msg to message history
            msgHistory.getItems().add("Me: " + input);
            manager.sendChatMsg(input);
            // clear input
            msg.clear();
        }
    }

    @Override
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
                    // if a textbox is already existed
                    if (pane.getChildren().contains(textfield)) {

                        drawText();
                    }
                    pane.getChildren().add(textfield);
                    textfield.setLayoutX(startX);
                    textfield.setLayoutY(startY);
                    textfield.requestFocus();
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

                    int size = path.getElements().size();
                    Double[] xs = new Double[size];
                    Double[] ys = new Double[size];

                    gc.beginPath();

                    MoveTo mt = (MoveTo) path.getElements().get(0);
                    gc.moveTo(mt.getX(), mt.getY());
                    xs[0] = mt.getX();
                    ys[0] = mt.getY();

                    for (int i = 1; i < size; i++) {
                        LineTo lt = (LineTo) path.getElements().get(i);
                        gc.lineTo(lt.getX(), lt.getY());
                        xs[i] = lt.getX();
                        ys[i] = lt.getY();
                    }
                    gc.stroke();
                    PathDrawing pathDrawing = new PathDrawing(xs, ys, colorPicker.getValue());
                    drawedShapes.add(pathDrawing);
                    manager.sendDrawMsg(pathDrawing);

                    path.getElements().clear();
                    break;
                case "dm_line":
                    pane.getChildren().remove(line);

                    LineDrawing lineDrawing = new LineDrawing(line.getStartX(), line.getStartY(), line.getEndX(),
                            line.getEndY(), colorPicker.getValue());
                    drawedShapes.add(lineDrawing);
                    manager.sendDrawMsg(lineDrawing);

                    // add shape to canvas via gc
                    gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                    break;
                case "dm_circle":
                    double sideLen = Math.min(Math.abs(endX - startX), Math.abs(endY - startY));
                    pane.getChildren().remove(circle);

                    CircleDrawing circleDrawing = new CircleDrawing(Math.min(startX, endX), Math.min(startY, endY),
                            sideLen, colorPicker.getValue());
                    drawedShapes.add(circleDrawing);
                    manager.sendDrawMsg(circleDrawing);

                    // add shape to canvas via gc
                    gc.strokeOval(Math.min(startX, endX), Math.min(startY, endY), sideLen, sideLen);
                    break;
                case "dm_triangle":
                    double w = Math.abs(endX - startX);
                    pane.getChildren().remove(polygon);

                    Double[] tri_xs = new Double[]{
                            Math.min(startX, endX),
                            Math.min(startX, endX) + 0.5 * w,
                            Math.max(startX, endX)};
                    Double[] tri_ys = new Double[]{
                            Math.max(startY, endY),
                            Math.min(startY, endY),
                            Math.max(startY, endY)
                    };

                    TriangleDrawing triangleDrawing = new TriangleDrawing(tri_xs, tri_ys, colorPicker.getValue());
                    manager.sendDrawMsg(triangleDrawing);
                    drawedShapes.add(triangleDrawing);
                    // add shape to canvas via gc
                    gc.strokePolygon(DoubleTodouble(tri_xs), DoubleTodouble(tri_ys), 3);
                    break;
                case "dm_rectangle":
                    pane.getChildren().remove(rectangle);

                    RectangularDrawing rectangleDrawing = new RectangularDrawing(rectangle.getX(), rectangle.getY(),
                            rectangle.getWidth(), rectangle.getHeight(), colorPicker.getValue());
                    drawedShapes.add(rectangleDrawing);
                    manager.sendDrawMsg(rectangleDrawing);
                    // add shape to canvas via gc
                    gc.strokeRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
                    break;
            }
        }

        // update unsave status
        isUnsaved = true;
    }

    @Override
    protected void drawText() {

        String text = textfield.getText();

        if (text.length() > 0) {
            gc.fillText(text, textfield.getLayoutX(), textfield.getLayoutY());

            // add drawed shape to list
            TextDrawing drawing = new TextDrawing(textfield.getLayoutX(), textfield.getLayoutY(), text, colorPicker.getValue());
            manager.sendDrawMsg(drawing);
            drawedShapes.add(drawing);

            // update unsave status
            isUnsaved = true;

            textfield.clear();
        }

        pane.getChildren().remove(textfield);
    }

    /**
     * Invoke this message to display a dialog to notify user about unsaved changes
     * @return true if changes were saved or user insist on closing the application,
     *  otherwise return false to indicate user selects "cancel" option
     */
    protected boolean showUnsaveDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setContentText("There is unsaved changes. Do you want to save them?");

        ButtonType btYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        ButtonType btCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btYes, btNo, btCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == btYes) {
            // user chose "yes
            handleSave(null);
            return true;
        } else if (result.get() == btNo) {
            // user chose "no"
            return true;
        } else {
            // user chose CANCEL or closed the dialog
            return false;
        }
    }

    /**
     * // TODO: when received an ApproveRequest from server
     * Invoke this message when a user want to join the whiteboard
     * @param username - the username of user wants to join
     */
    protected void showJoinRequest(String username) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Join request");
        alert.setContentText("User: " + username + " want to join your whiteboard. Do you agree?");

        ButtonType btYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btNo = new ButtonType("No", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(btYes, btNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == btYes) {
            // manager chose "yes
            manager.approveJoinRequest(true);
        }else {
            manager.approveJoinRequest(false);
        }
    }

    /**
     * Invoke this message when a manager want to kick out a specified user
     * @param username - the username of kicked user
     */
    protected void KickOutUser(String username) {
        manager.sendKickUserMsg(username);
        System.out.println("kick out " + username);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}




