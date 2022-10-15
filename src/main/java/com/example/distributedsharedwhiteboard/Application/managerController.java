package com.example.distributedsharedwhiteboard.Application;

import java.io.File;
import java.io.FileInputStream;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.scene.Node;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.IOException;
import javafx.stage.FileChooser.ExtensionFilter;
import java.time.LocalDateTime;

public class managerController extends userController{

    @FXML
    protected ListView<String> userList;

    @FXML
    protected void handleSaveAs(ActionEvent event) {

        FileChooser savefile = new FileChooser();

        savefile.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png", "*.jpg"),
                new ExtensionFilter("All Files", "*.*"));
        savefile.setSelectedExtensionFilter(savefile.getExtensionFilters().get(0));
        savefile.setTitle("Save File");
        savefile.setInitialFileName("untitled-" + LocalDateTime.now().getNano());

        File file = savefile.showSaveDialog(stage);
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
    }
}
