package com.example.distributedsharedwhiteboard.Application.Controllers;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.IOException;
import javafx.stage.FileChooser.ExtensionFilter;

public class managerController extends userController {

    @FXML
    protected Stage stage;

    @FXML
    protected void handleSaveAs(ActionEvent event) {

        FileChooser savefile = new FileChooser();
        savefile.setSelectedExtensionFilter(new ExtensionFilter("Image Files", "*.png", "*.jpg"));
        savefile.setTitle("Save File");

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
