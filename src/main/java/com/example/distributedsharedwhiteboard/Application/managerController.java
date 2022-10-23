package com.example.distributedsharedwhiteboard.Application;

import com.example.distributedsharedwhiteboard.ShapeDrawing.CircleDrawing;
import com.example.distributedsharedwhiteboard.ShapeDrawing.ShapeDrawing;
import com.example.distributedsharedwhiteboard.client.CreateWhiteBoard;
import com.example.distributedsharedwhiteboard.Util.*;

import com.example.distributedsharedwhiteboard.client.JoinWhiteBoard;
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
import java.util.Optional;

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
        Bindings.bindContentBidirectional(manager.getMsgList(), msgHistory.getItems());
        manager.addMsgItem("test only : message"); // now can access msgHistory via msgList

        Bindings.bindContentBidirectional(manager.getUserList(),userList.getItems());
        manager.addUserItem("Test only : user1");

        Bindings.bindContentBidirectional(manager.getObjectList(),drawedShapes);

        drawedShapes.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {

                while (c.next()) {

                    // if anything was added to list
                    if (c.wasAdded()) {
                        for (Object s : c.getAddedSubList()) {
                            // ask user to send a updateRequest to server
                            ShapeDrawing shape = (ShapeDrawing) s;
                            manager.sendDrawMsg(shape);
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
        manager.sendReloadRequest();
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
        } else {
            // manager chose "no"
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




